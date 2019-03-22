package ru.wheelman.github.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.*
import androidx.paging.PagedList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import ru.wheelman.github.App
import ru.wheelman.github.logd
import ru.wheelman.github.model.entities.Result
import ru.wheelman.github.model.entities.User
import ru.wheelman.github.model.repositories.IGithubUsersRepo
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UsersFragmentViewModel : ViewModel() {

    @Inject
    internal lateinit var githubUsersRepo: IGithubUsersRepo
    private val _errors: MediatorLiveData<String> = MediatorLiveData()
    private val _allUsersLivePagedList: MediatorLiveData<PagedList<User>> = MediatorLiveData()
    private val _foundUsersLivePagedList: MediatorLiveData<PagedList<User>> = MediatorLiveData()
    val usersBeingUpdated = ObservableBoolean(false)
    internal val searchQuery = PublishSubject.create<String>()
    private var lastSearchResult: Result? = null
    private val disposables = CompositeDisposable()
    private val _showAllUsers = MutableLiveData<Boolean>().apply { value = true }

    internal val allUsersLivePagedList: LiveData<PagedList<User>> = _allUsersLivePagedList
    internal val foundUsersLivePagedList: LiveData<PagedList<User>> = _foundUsersLivePagedList
    internal val errors: LiveData<String> = _errors
    internal val showAllUsers: LiveData<Boolean>
        get() = _showAllUsers

    init {
        initDagger()
        initListeners()
        loadUsers()
    }

    private fun initListeners() {
        val d = searchQuery
            .debounce(300L, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                logd("onQueryTextChange $it ${Thread.currentThread().name}")
                if (it.isEmpty()) {
                    if (_showAllUsers.value == false) _showAllUsers.value = true
                    return@subscribe
                }
                if (_showAllUsers.value == true) _showAllUsers.value = false
                removePreviousSearchSource()
                loadData {
                    val result = githubUsersRepo.findUsers(it, viewModelScope + Dispatchers.IO)
                    _errors.addSource(result.errors) { _errors.value = it }
                    _foundUsersLivePagedList.addSource(result.livePagedList) {
                        _foundUsersLivePagedList.value = it
                    }
                    lastSearchResult = result
                }
            }
        disposables.add(d)
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
            usersBeingUpdated.set(true)
            block()
            usersBeingUpdated.set(false)
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
            usersBeingUpdated.set(false)
        }
    }

    override fun onCleared() {
        disposables.dispose()
    }
}