package ru.wheelman.github.model.datasources.remote

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.wheelman.github.di.qualifiers.ErrorsLiveDataQualifier
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.logd
import ru.wheelman.github.model.datasources.local.UsersDb
import ru.wheelman.github.model.entities.User
import javax.inject.Inject

@AppScope
class UsersBoundaryCallback @Inject constructor(
    private val scope: CoroutineScope,
    private val githubService: GithubService,
    @ErrorsLiveDataQualifier private val errors: MutableLiveData<String>,
    private val usersDb: UsersDb,
    private val perPage: Int
) : PagedList.BoundaryCallback<User>() {

    private val onSuccess: suspend (List<User>) -> Unit =
        { users -> usersDb.usersDao().insertUsers(users) }
    private val onError: suspend (String) -> Unit = { error -> errors.postValue(error) }

    override fun onZeroItemsLoaded() {
        scope.launch {
            logd("UsersBoundaryCallback onZeroItemsLoaded")
            githubService.loadInitialUsers(perPage, onSuccess, onError)
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: User) {
        scope.launch {
            logd("UsersBoundaryCallback onItemAtEndLoaded")
            githubService.loadNextUsers(perPage, onSuccess, onError)
        }
    }
}