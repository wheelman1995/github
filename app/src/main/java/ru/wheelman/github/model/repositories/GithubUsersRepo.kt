package ru.wheelman.github.model.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.model.datasources.remote.GithubApi
import ru.wheelman.github.model.datasources.remote.GithubUser
import ru.wheelman.github.model.entities.User
import java.io.IOException
import javax.inject.Inject

@AppScope
class GithubUsersRepo @Inject constructor(private val githubApi: GithubApi) : IGithubUsersRepo {

    override suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        val response = githubApi.getUsers().execute()
        val body = response.body()
        if (body != null && response.isSuccessful) {
            mapGithubUser(body)
        } else {
            throw IOException()
        }
    }

    private fun mapGithubUser(githubUsers: List<GithubUser>): List<User> =
        githubUsers.map {
            User(
                it.id,
                it.login,
                it.avatarUrl
            )
        }
}