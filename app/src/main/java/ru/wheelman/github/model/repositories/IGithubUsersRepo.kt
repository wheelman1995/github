package ru.wheelman.github.model.repositories

import kotlinx.coroutines.CoroutineScope
import ru.wheelman.github.model.entities.Result

interface IGithubUsersRepo {

    suspend fun getUsers(scope: CoroutineScope): Result
    suspend fun findUsers(query: String, scope: CoroutineScope): Result
    suspend fun tryFetchingUsersFromNetwork(
        onSuccess: (suspend () -> Unit)? = null,
        onError: (suspend (String) -> Unit)? = null
    )
}
