package ru.wheelman.github.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import ru.wheelman.github.App
import ru.wheelman.github.model.entities.User
import ru.wheelman.github.model.repositories.IGithubUsersRepo
import javax.inject.Inject

class UsersFragmentViewModel : ViewModel() {

    @Inject
    internal lateinit var githubUsersRepo: IGithubUsersRepo
    private val _errors: MediatorLiveData<String> = MediatorLiveData()
    private val _livePagedList: MediatorLiveData<PagedList<User>> = MediatorLiveData()
    internal val errors: LiveData<String> = _errors
    internal val livePagedList: LiveData<PagedList<User>> = _livePagedList
    val usersBeingUpdated = ObservableBoolean(false)

    init {
        initDagger()
        loadUsers()
    }

    private fun initDagger() {
        App.appComponent.inject(this)
    }

    private fun loadUsers() {
        viewModelScope.launch {
            usersBeingUpdated.set(true)
            val result = githubUsersRepo.getUsers(viewModelScope + Dispatchers.IO, PER_PAGE)
            _errors.addSource(result.errors) { _errors.value = it }
            _livePagedList.addSource(result.livePagedList) { _livePagedList.value = it }
            usersBeingUpdated.set(false)
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            githubUsersRepo.tryFetchingUsersFromNetwork(PER_PAGE) {
                _livePagedList.value?.dataSource?.invalidate()
            }
            usersBeingUpdated.set(false)
        }
    }

    private companion object {
        private const val PER_PAGE = 20
    }
}