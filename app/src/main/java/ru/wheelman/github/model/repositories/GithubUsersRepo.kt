package ru.wheelman.github.model.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ru.wheelman.github.di.qualifiers.ErrorsLiveDataQualifier
import ru.wheelman.github.di.scopes.AppScope
import ru.wheelman.github.model.entities.Result
import ru.wheelman.github.model.entities.User
import javax.inject.Inject

@AppScope
class GithubUsersRepo @Inject constructor(
    private val factory: DataSource.Factory<Long, User>,
    @ErrorsLiveDataQualifier private val errors: MutableLiveData<String>,
    private val config: PagedList.Config
) : IGithubUsersRepo {

    override suspend fun getUsers(): Result {
        val livePagedList = LivePagedListBuilder<Long, User>(
            factory,
            config
        ).build()
        return Result(errors, livePagedList)
    }
}