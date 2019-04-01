package ru.wheelman.github.model.datasources.remote

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.wheelman.github.logd
import ru.wheelman.github.model.datasources.local.UsersDb
import ru.wheelman.github.model.entities.User
import ru.wheelman.github.model.repositories.GithubUsersRepo.Companion.INITIAL_LOAD_SIZE
import ru.wheelman.github.model.repositories.GithubUsersRepo.Companion.PAGE_SIZE

class UsersBoundaryCallback constructor(
    private val scope: CoroutineScope,
    private val githubService: GithubService,
    private val usersDb: UsersDb
) : PagedList.BoundaryCallback<User>() {

    internal val errors = MutableLiveData<String>()
    private val onSuccess: suspend (List<User>) -> Unit =
        { users -> usersDb.usersDao().insertUsers(users) }
    private val onError: suspend (String) -> Unit = { error -> errors.postValue(error) }

    override fun onZeroItemsLoaded() {
        scope.launch {
            logd("UsersBoundaryCallback onZeroItemsLoaded")
            githubService.getUsers(
                perPage = INITIAL_LOAD_SIZE,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: User) {
        logd("UsersBoundaryCallback onItemAtEndLoaded")
        scope.launch {
            githubService.getUsers(
                perPage = PAGE_SIZE,
                pageKey = itemAtEnd.nextUserId,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }
}