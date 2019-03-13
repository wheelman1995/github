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
import ru.wheelman.github.R
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
        viewModel.showError.observe(this, Observer {
            if (it) Snackbar.make(
                requireView(),
                getString(R.string.something_went_wrong),
                Snackbar.LENGTH_LONG
            ).show()
        })
    }

    private fun initDagger() {
        App.appComponent.inject(this)
    }

    private fun initVariables() {
        viewModel = ViewModelProviders.of(this).get(UsersFragmentViewModel::class.java)
        usersRvAdapter = UsersRvAdapter(viewModel.usersAdapterViewModel, dataBindingComponent)
    }

    override fun onDestroyView() {
        usersRvAdapter.onDestroyView()
        super.onDestroyView()
    }
}