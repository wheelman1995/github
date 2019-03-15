package ru.wheelman.github.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    init {
        initDagger()
        loadUsers()
    }

    private fun initDagger() {
        App.appComponent.inject(this)
    }

    private fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = githubUsersRepo.getUsers()
            _errors.addSource(result.errors) { _errors.postValue(it) }
            _livePagedList.addSource(result.livePagedList) { _livePagedList.postValue(it) }
        }
    }
}