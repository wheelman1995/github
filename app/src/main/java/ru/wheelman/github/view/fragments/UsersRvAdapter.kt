package ru.wheelman.github.view.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.wheelman.github.databinding.ItemUserBinding
import ru.wheelman.github.model.entities.User
import ru.wheelman.github.view.databinding.DataBindingComponent
import ru.wheelman.github.view.fragments.UsersRvAdapter.VH

class UsersRvAdapter(
    private val dataBindingComponent: DataBindingComponent
) : PagedListAdapter<User, VH>(USER_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
            dataBindingComponent
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    private companion object {

        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {

            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem
        }
    }

    inner class VH(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        internal fun bind(user: User?) {
            binding.user = user
        }
    }
}