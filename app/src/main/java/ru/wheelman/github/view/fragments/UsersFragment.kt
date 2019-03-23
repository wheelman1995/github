package ru.wheelman.github.view.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import ru.wheelman.github.App
import ru.wheelman.github.R
import ru.wheelman.github.databinding.FragmentUsersBinding
import ru.wheelman.github.view.databinding.DataBindingComponent
import ru.wheelman.github.viewmodel.UsersFragmentViewModel
import javax.inject.Inject

class UsersFragment : Fragment() {

    @Inject
    internal lateinit var dataBindingComponent: DataBindingComponent
    private lateinit var viewModel: UsersFragmentViewModel
    private lateinit var allUsersRvAdapter: UsersRvAdapter
    private lateinit var foundUsersRvAdapter: UsersRvAdapter
    private lateinit var binding: FragmentUsersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initDagger()
        binding = FragmentUsersBinding.inflate(inflater, container, false, dataBindingComponent)
        initVariables()
        initUi()
        initListeners()
        initBinding()
        return binding.root
    }

    private fun initBinding() {
        binding.viewModel = viewModel
    }

    private fun initListeners() {
        viewModel.errors.observe(this, Observer {
            Snackbar.make(
                requireView(),
                it,
                Snackbar.LENGTH_LONG
            ).show()
        })
        viewModel.allUsersLivePagedList.observe(this, Observer {
            allUsersRvAdapter.submitList(it)
        })
        viewModel.foundUsersLivePagedList.observe(this, Observer {
            foundUsersRvAdapter.submitList(it)
        })
        viewModel.showAllUsers.observe(this, Observer { showAllUsers ->
            if (showAllUsers) {
                binding.rvUsers.swapAdapter(allUsersRvAdapter, false)
            } else {
                binding.rvUsers.swapAdapter(foundUsersRvAdapter, false)
            }
        })
    }

    private fun initDagger() {
        App.appComponent.inject(this)
    }

    private fun initUi() {
        setHasOptionsMenu(true)
        binding.rvUsers.setHasFixedSize(true)
    }

    private fun initVariables() {
        viewModel = ViewModelProviders.of(this).get(UsersFragmentViewModel::class.java)
        allUsersRvAdapter = UsersRvAdapter(dataBindingComponent)
        foundUsersRvAdapter = UsersRvAdapter(dataBindingComponent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.run {
            queryHint = getString(R.string.enter_username)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.searchQuery.onNext(newText)
                    return true
                }
            })
        }
    }
}