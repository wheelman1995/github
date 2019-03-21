package ru.wheelman.github.model.datasources.remote

import okhttp3.Headers
import retrofit2.Response
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.model.entities.User
import java.util.regex.Pattern
import javax.inject.Inject

@AppScope
class GithubService @Inject constructor(private val githubApi: GithubApi) {

    private var nextPageKey: Long? = null

    internal suspend fun loadInitialUsers(
        perPage: Int,
        onSuccess: suspend (List<User>) -> Unit,
        onError: suspend (String) -> Unit
    ) {
        val response = githubApi.getUsers(perPage).await()
        processResponse(response, onSuccess, onError)
    }

    private suspend fun processResponse(
        response: Response<List<GithubUser>>,
        onSuccess: suspend (List<User>) -> Unit,
        onError: suspend (String) -> Unit
    ) {
        val body = response.body()
        if (response.isSuccessful && body != null) {
            nextPageKey = findNextPageKey(response.headers())
            val users = body.map {
                User(
                    it.id,
                    it.login,
                    it.avatarUrl
                )
            }
            onSuccess(users)
        } else {
            onError(response.errorBody()?.string() ?: "Unknown error")
        }
    }

    internal suspend fun loadNextUsers(
        perPage: Int,
        onSuccess: suspend (List<User>) -> Unit,
        onError: suspend (String) -> Unit
    ) = nextPageKey?.let {
        val response = githubApi.getNextUsers(perPage, nextPageKey!!).await()
        processResponse(response, onSuccess, onError)
    } ?: onError("Could not find next page")

    private fun findNextPageKey(headers: Headers): Long? {
        val linkHeader = headers.get("Link")
        return linkHeader?.let {
            val pattern = Pattern.compile("since=\\d+")
            val matcher = pattern.matcher(it)
            val match = if (matcher.find()) matcher.group() else null
            match?.substring(6)?.toLongOrNull()
        }
    }
}