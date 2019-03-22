package ru.wheelman.github.model.datasources.remote

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.wheelman.github.model.entities.User

class PageKeyedGithubDataSource(
    private val scope: CoroutineScope,
    private val githubService: GithubService,
    private val query: String,
    private val errors: MutableLiveData<String>
) : PageKeyedDataSource<Long, User>() {

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Long, User>
    ) {
        scope.launch {
            githubService.findUsers(
                query = query,
                onSuccess = {
                    callback.onResult(it, null, 2)
                },
                onError = {
                    errors.postValue(it)
                }
            )
        }
    }

    override fun loadAfter(
        params: LoadParams<Long>,
        callback: LoadCallback<Long, User>
    ) {
        scope.launch {
            githubService.findUsers(
                query = query,
                page = params.key,
                onSuccess = {
                    callback.onResult(it, params.key + 1)
                },
                onError = {
                    errors.postValue(it)
                }
            )
        }
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, User>) {}

    class Factory(
        private val githubService: GithubService,
        private val scope: CoroutineScope,
        private val query: String,
        private val errors: MutableLiveData<String>
    ) : DataSource.Factory<Long, User>() {
        override fun create(): DataSource<Long, User> =
            PageKeyedGithubDataSource(scope, githubService, query, errors)
    }
}