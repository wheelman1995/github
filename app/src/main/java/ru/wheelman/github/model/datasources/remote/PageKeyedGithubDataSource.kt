//package ru.wheelman.github.model.datasources.remote
//
//import androidx.lifecycle.MutableLiveData
//import androidx.paging.DataSource
//import androidx.paging.PageKeyedDataSource
//import okhttp3.Headers
//import ru.wheelman.github.di.qualifiers.ErrorsLiveDataQualifier
//import ru.wheelman.github.di.scopes.AppScope
//import java.util.regex.Pattern
//import javax.inject.Inject
//
//class PageKeyedGithubDataSource constructor(
//    private val githubApi: GithubApi,
//    private val errors: MutableLiveData<String>
//) : PageKeyedDataSource<Long, GithubUser>() {
//
//    override fun loadInitial(
//        params: LoadInitialParams<Long>,
//        callback: LoadInitialCallback<Long, GithubUser>
//    ) {
//        try {
//            val response = githubApi.getUsers(params.requestedLoadSize).execute()
//            val body = response.body()
//            if (body == null || !response.isSuccessful) {
//                this.errors.postValue(response.errorBody()?.string() ?: "Unknown error")
//                return
//            }
//            val nextPageKey = findNextPageKey(response.headers())
//            callback.onResult(body, null, nextPageKey)
//        } catch (e: Exception) {
//            this.errors.postValue(e.message ?: "Unknown error")
//        }
//    }
//
//    override fun loadAfter(
//        params: LoadParams<Long>,
//        callback: LoadCallback<Long, GithubUser>
//    ) {
//        try {
//            val response = githubApi.getNextUsers(
//                params.requestedLoadSize,
//                params.key
//            ).execute()
//            val body = response.body()
//            if (body == null || !response.isSuccessful) {
//                this.errors.postValue(response.errorBody()?.string() ?: "Unknown error")
//                return
//            }
//            val adjacentPageKey = findNextPageKey(response.headers())
//            callback.onResult(body, adjacentPageKey)
//        } catch (e: Exception) {
//            this.errors.postValue(e.message ?: "Unknown error")
//        }
//    }
//
//    private fun findNextPageKey(headers: Headers): Long? {
//        val linkHeader = headers.get("Link")
//        return linkHeader?.let {
//            val pattern = Pattern.compile("since=\\d+")
//            val matcher = pattern.matcher(it)
//            val match = if (matcher.find()) matcher.group() else null
//            match?.substring(6)?.toLongOrNull()
//        }
//    }
//
//    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, GithubUser>) {}
//
//    @AppScope
//    class Factory @Inject constructor(
//        private val githubApi: GithubApi,
//        @ErrorsLiveDataQualifier private val errors: MutableLiveData<String>
//    ) : DataSource.Factory<Long, GithubUser>() {
//        override fun create(): DataSource<Long, GithubUser> =
//            PageKeyedGithubDataSource(githubApi, errors)
//    }
//}