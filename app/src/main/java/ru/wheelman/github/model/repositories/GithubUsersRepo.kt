package ru.wheelman.github.model.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.model.datasources.local.UsersDb
import ru.wheelman.github.model.datasources.remote.GithubService
import ru.wheelman.github.model.datasources.remote.GithubService.Companion.PER_PAGE_DEFAULT
import ru.wheelman.github.model.datasources.remote.PageKeyedGithubDataSource
import ru.wheelman.github.model.datasources.remote.UsersBoundaryCallback
import ru.wheelman.github.model.entities.Result
import ru.wheelman.github.model.entities.User
import javax.inject.Inject

@AppScope
class GithubUsersRepo @Inject constructor(
    private val usersDb: UsersDb,
    private val githubService: GithubService
) : IGithubUsersRepo {

    private val config = PagedList.Config.Builder()
        .setInitialLoadSizeHint(PER_PAGE_DEFAULT)
        .setPageSize(PER_PAGE_DEFAULT)
        .build()

    override suspend fun getUsers(scope: CoroutineScope): Result =
        withContext(Dispatchers.IO) {
            val errors = MutableLiveData<String>()
            val factory = usersDb.usersDao().getUsers()
            val usersBoundaryCallback = UsersBoundaryCallback(
                scope,
                githubService,
                errors,
                usersDb
            )
            val livePagedList = LivePagedListBuilder<Int, User>(
                factory,
                config
            ).setBoundaryCallback(usersBoundaryCallback)
                .build()
            Result(errors, livePagedList)
        }

    override suspend fun tryFetchingUsersFromNetwork(
        onSuccess: (suspend () -> Unit)?,
        onError: (suspend (String) -> Unit)?
    ) = withContext(Dispatchers.IO) {
        githubService.getUsers(
            onSuccess = { users ->
                usersDb.usersDao().deleteAllUsers()
                usersDb.usersDao().insertUsers(users)
                onSuccess?.invoke()
            },
            onError = { onError?.invoke(it) }
        )
    }

    override suspend fun findUsers(query: String, scope: CoroutineScope): Result =
        withContext(Dispatchers.IO) {
            val errors = MutableLiveData<String>()
            val factory = PageKeyedGithubDataSource.Factory(
                githubService,
                scope,
                query,
                errors
            )
            val livePagedList = LivePagedListBuilder<Long, User>(
                factory,
                config
            ).build()
            Result(errors, livePagedList)
        }
}