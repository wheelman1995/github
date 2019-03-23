package ru.wheelman.github.model.datasources.remote

import androidx.annotation.IntRange
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Headers
import retrofit2.Response
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.model.entities.User
import java.util.regex.Pattern
import javax.inject.Inject

@AppScope
class GithubService @Inject constructor(private val githubApi: GithubApi) {

    companion object {
        private const val PER_PAGE_MIN = 1L
        private const val PER_PAGE_MAX = 100L
        const val PER_PAGE_DEFAULT = 20
        private const val SINCE = 0L
    }

    private var searchDeferred: Deferred<SearchResult>? = null
    private val mutex = Mutex()

    internal suspend fun getUsers(
        @IntRange(from = PER_PAGE_MIN, to = PER_PAGE_MAX) perPage: Int = PER_PAGE_DEFAULT,
        pageKey: Long? = SINCE,
        onSuccess: suspend (List<User>) -> Unit,
        onError: suspend (String) -> Unit
    ) {
        pageKey?.let {
            val response: Response<List<GithubUser>>
            try {
                response = githubApi.getUsers(perPage, pageKey).await()
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
                return
            }
            val body = response.body()
            if (response.isSuccessful && body != null) {
                if (body.isEmpty()) {
                    onError("No users received")
                    return
                }
                val users = body.subList(0, body.lastIndex).mapIndexed { index, githubUser ->
                    User(
                        githubUser.id,
                        githubUser.login,
                        githubUser.avatarUrl,
                        githubUser.score,
                        body[index + 1].id
                    )
                }.toMutableList()
                val nextUserIdOfLastUser = findNextUserIdOfLastUser(response.headers())
                val (id, login, avatarUrl, score) = body.last()
                users.add(
                    User(
                        id,
                        login,
                        avatarUrl,
                        score,
                        nextUserIdOfLastUser
                    )
                )
                onSuccess(users)
            } else {
                onError(response.errorBody()?.string() ?: "Unknown error")
            }
        } ?: onError("Could not find next page")
    }

    internal suspend fun findUsers(
        @IntRange(from = PER_PAGE_MIN, to = PER_PAGE_MAX) perPage: Int = PER_PAGE_DEFAULT,
        page: Long = 1,
        query: String,
        onSuccess: suspend (List<User>) -> Unit,
        onError: suspend (String) -> Unit
    ) {
        val result: SearchResult
        try {
            val newSearchDeferred = githubApi.searchUsers(query, page, perPage)
            mutex.withLock {
                cancelPreviousSearch()
                searchDeferred = newSearchDeferred
            }
            result = newSearchDeferred.await()
        } catch (e: Exception) {
            onError(e.message ?: "Unknown error")
            return
        }
        onSuccess(result.items.map {
            User(
                it.id,
                it.login,
                it.avatarUrl,
                it.score,
                null
            )
        })
    }

    private fun cancelPreviousSearch() {
        val deferred = searchDeferred
        deferred?.let {
            deferred.cancel()
        }
    }

    private fun findNextUserIdOfLastUser(headers: Headers): Long? {
        val linkHeader = headers.get("Link")
        return linkHeader?.let {
            val pattern = Pattern.compile("since=\\d+")
            val matcher = pattern.matcher(it)
            val match = if (matcher.find()) matcher.group() else null
            match?.substring(6)?.toLongOrNull()
        }
    }
}