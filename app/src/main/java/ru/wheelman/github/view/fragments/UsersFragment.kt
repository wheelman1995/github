package ru.wheelman.github.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import ru.wheelman.github.App
import ru.wheelman.github.databinding.FragmentUsersBinding
import ru.wheelman.github.view.databinding.DataBindingComponent
import ru.wheelman.github.viewmodel.UsersFragmentViewModel
import javax.inject.Inject

class UsersFragment : Fragment() {

    @Inject
    internal lateinit var dataBindingComponent: DataBindingComponent
    private lateinit var viewModel: UsersFragmentViewModel
    private lateinit var usersRvAdapter: UsersRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initDagger()
        val binding = FragmentUsersBinding.inflate(inflater, container, false)
        initVariables()
        initListeners()
        initBinding(binding)
        return binding.root
    }

    private fun initBinding(binding: FragmentUsersBinding) {
        binding.adapter = usersRvAdapter
    }

    private fun initListeners() {
        viewModel.errors.observe(this, Observer {
            Snackbar.make(
                requireView(),
                it,
                Snackbar.LENGTH_LONG
            ).show()
        })
        viewModel.livePagedList.observe(this, Observer {
            usersRvAdapter.submitList(it)
        })
    }

    private fun initDagger() {
        App.appComponent.inject(this)
    }

    private fun initVariables() {
        viewModel = ViewModelProviders.of(this).get(UsersFragmentViewModel::class.java)
        usersRvAdapter = UsersRvAdapter(dataBindingComponent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}