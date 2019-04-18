package ru.wheelman.github.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import ru.wheelman.github.App
import ru.wheelman.github.model.entities.Result
import ru.wheelman.github.model.entities.User
import ru.wheelman.github.model.repositories.IGithubUsersRepo
import javax.inject.Inject

class UsersFragmentViewModel : ViewModel() {

    @Inject internal lateinit var githubUsersRepo: IGithubUsersRepo
    @Inject internal lateinit var countingIdlingResource: CountingIdlingResource
    private val _errors: MediatorLiveData<String> = MediatorLiveData()
    private val _allUsersLivePagedList: MediatorLiveData<PagedList<User>> = MediatorLiveData()
    private val _foundUsersLivePagedList: MediatorLiveData<PagedList<User>> = MediatorLiveData()
    private var lastSearchResult: Result? = null
    private val _showAllUsers = MutableLiveData<Boolean>().apply { value = true }
    internal val allUsersLivePagedList: LiveData<PagedList<User>> = _allUsersLivePagedList
    internal val foundUsersLivePagedList: LiveData<PagedList<User>> = _foundUsersLivePagedList
    internal val errors: LiveData<String> = _errors
    internal val showAllUsers: LiveData<Boolean>
        get() = _showAllUsers
    val loading = ObservableBoolean(false)

    init {
        initDagger()
        loadUsers()
    }

    private fun removePreviousSearchSource() {
        val localLastSearchResult = lastSearchResult
        localLastSearchResult?.let {
            _errors.removeSource(localLastSearchResult.errors)
            _foundUsersLivePagedList.removeSource(localLastSearchResult.livePagedList)
        }
    }

    private fun initDagger() {
        App.appComponent.inject(this)
    }

    private fun loadUsers() {
        loadData {
            val result = githubUsersRepo.getUsers(viewModelScope + Dispatchers.IO)
            _errors.addSource(result.errors) { _errors.value = it }
            _allUsersLivePagedList.addSource(result.livePagedList) {
                _allUsersLivePagedList.value = it
            }
        }
    }

    private fun loadData(block: suspend () -> Unit) {
        viewModelScope.launch {
            countingIdlingResource.increment()
            loading.set(true)
            block()
            loading.set(false)
            countingIdlingResource.decrement()
        }
    }

    fun onRefresh() {
        if (_showAllUsers.value == true) {
            loadData {
                githubUsersRepo.tryFetchingUsersFromNetwork(
                    { _allUsersLivePagedList.value?.dataSource?.invalidate() },
                    { _errors.postValue(it) }
                )
            }
        } else {
            _foundUsersLivePagedList.value?.dataSource?.invalidate()
            loading.set(false)
        }
    }

    fun onQueryTextChange(query: CharSequence) {
        if (query.isEmpty()) {
            if (_showAllUsers.value == false) _showAllUsers.value = true
            return
        }
        if (_showAllUsers.value == true) _showAllUsers.value = false
        loadData {
            val result =
                githubUsersRepo.findUsers(query.toString(), viewModelScope + Dispatchers.IO)
            _errors.addSource(result.errors) { _errors.value = it }
            _foundUsersLivePagedList.addSource(result.livePagedList) {
                _foundUsersLivePagedList.value = it
            }
            removePreviousSearchSource()
            lastSearchResult = result
        }
    }
}