package ru.wheelman.github.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.wheelman.github.App
import ru.wheelman.github.databinding.FragmentAvatarBinding
import ru.wheelman.github.view.databinding.DataBindingComponent
import javax.inject.Inject

class AvatarFragment : Fragment() {

    @Inject internal lateinit var dataBindingComponent: DataBindingComponent
    private val args: AvatarFragmentArgs by navArgs()
    private val navController: NavController by lazy { findNavController() }
    private lateinit var binding: FragmentAvatarBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initDagger()
        binding = FragmentAvatarBinding.inflate(inflater, container, false, dataBindingComponent)
        initBinding()
        initUi()
        return binding.root
    }

    private fun initUi() {
        setHasOptionsMenu(true)
    }

    private fun initBinding() {
        binding.avatarUrl = args.avatarUrl
    }

    private fun initDagger() {
        App.appComponent.inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> navController.navigateUp()
        else -> super.onOptionsItemSelected(item)
    }
}