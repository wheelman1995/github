package ru.wheelman.github.model.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.wheelman.github.di.qualifiers.ErrorsLiveDataQualifier
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.model.datasources.local.UsersDb
import ru.wheelman.github.model.datasources.remote.GithubService
import ru.wheelman.github.model.datasources.remote.UsersBoundaryCallback
import ru.wheelman.github.model.entities.Result
import ru.wheelman.github.model.entities.User
import javax.inject.Inject

@AppScope
class GithubUsersRepo @Inject constructor(
    @ErrorsLiveDataQualifier private val errors: MutableLiveData<String>,
    private val usersDb: UsersDb,
    private val githubService: GithubService
) : IGithubUsersRepo {

    override suspend fun getUsers(scope: CoroutineScope, perPage: Int): Result =
        withContext(Dispatchers.IO) {
            _tryFetchingUsersFromNetwork(perPage)
            val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(perPage)
                .setPageSize(perPage)
                .build()
            val factory = usersDb.usersDao().getUsers()
            val usersBoundaryCallback = UsersBoundaryCallback(
                scope,
                githubService,
                errors,
                usersDb,
                perPage
            )
            val livePagedList = LivePagedListBuilder<Int, User>(
                factory,
                config
            ).setBoundaryCallback(usersBoundaryCallback)
                .build()
            Result(errors, livePagedList)
        }

    private suspend fun _tryFetchingUsersFromNetwork(
        perPage: Int,
        onSuccess: (suspend () -> Unit)? = null
    ) {
        githubService.loadInitialUsers(
            perPage,
            { users ->
                usersDb.usersDao().deleteAllUsers()
                usersDb.usersDao().insertUsers(users)
                onSuccess?.invoke()
            },
            { error ->
                errors.postValue(error)
            }
        )
    }

    override suspend fun tryFetchingUsersFromNetwork(
        perPage: Int,
        onSuccess: (suspend () -> Unit)?
    ) = withContext(Dispatchers.IO) {
        _tryFetchingUsersFromNetwork(perPage, onSuccess)
    }
}