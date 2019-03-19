package ru.wheelman.github.model.repositories

import kotlinx.coroutines.CoroutineScope
import ru.wheelman.github.model.entities.Result

interface IGithubUsersRepo {

    suspend fun getUsers(scope: CoroutineScope, perPage: Int): Result
    suspend fun tryFetchingUsersFromNetwork(perPage: Int, onSuccess: (suspend () -> Unit)? = null)
}
