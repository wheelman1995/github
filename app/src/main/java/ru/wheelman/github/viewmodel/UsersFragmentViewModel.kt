package ru.wheelman.github.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.wheelman.github.App
import ru.wheelman.github.model.entities.User
import ru.wheelman.github.model.repositories.IGithubUsersRepo
import java.io.IOException
import javax.inject.Inject

class UsersFragmentViewModel : ViewModel() {

    @Inject
    internal lateinit var githubUsersRepo: IGithubUsersRepo
    @Inject
    internal lateinit var usersAdapterViewModel: UsersAdapterViewModel
    private val _showError = MutableLiveData<Boolean>()
    val showError: LiveData<Boolean> = _showError

    init {
        initDagger()
        loadUsers()
    }

    private fun initDagger() {
        App.appComponent.inject(this)
    }

    private fun loadUsers() {
        viewModelScope.launch {
            try {
                val users = githubUsersRepo.getUsers()
                usersAdapterViewModel.users = users
            } catch (e: IOException) {
                _showError.value = true
            }
        }
    }

    class UsersAdapterViewModel @Inject constructor() {

        private var adapterViewModelListener: AdapterViewModelListener? = null
        internal var users = listOf<User>()
            set(value) {
                if (field != value) {
                    field = value
                    adapterViewModelListener?.notifyDataSetChanged()
                }
            }

        fun subscribe(adapterViewModelListener: AdapterViewModelListener) {
            this.adapterViewModelListener = adapterViewModelListener
        }

        fun unsubscribe() {
            adapterViewModelListener = null
        }

        fun getItemCount() = users.size

        fun getUsername(position: Int) = users[position].name

        fun getAvatarUrl(position: Int) = users[position].avatarUrl
    }
}