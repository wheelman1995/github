package ru.wheelman.github.view.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.wheelman.github.databinding.ItemUserBinding
import ru.wheelman.github.view.databinding.DataBindingComponent
import ru.wheelman.github.viewmodel.AdapterViewModelListener
import ru.wheelman.github.viewmodel.UsersFragmentViewModel.UsersAdapterViewModel

class UsersRvAdapter(
    private val usersAdapterViewModel: UsersAdapterViewModel,
    private val dataBindingComponent: DataBindingComponent
) : RecyclerView.Adapter<UsersRvAdapter.VH>() {

    init {
        subscribe()
    }

    private fun subscribe() {
        usersAdapterViewModel.subscribe(object : AdapterViewModelListener {
            override fun notifyDataSetChanged() = this@UsersRvAdapter.notifyDataSetChanged()
            override fun notifyItemChanged(position: Int, payload: Any?) =
                this@UsersRvAdapter.notifyItemChanged(position, payload)

            override fun notifyItemInserted(position: Int) =
                this@UsersRvAdapter.notifyItemInserted(position)

            override fun notifyItemMoved(fromPosition: Int, toPosition: Int) =
                this@UsersRvAdapter.notifyItemMoved(fromPosition, toPosition)

            override fun notifyItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) =
                this@UsersRvAdapter.notifyItemRangeChanged(positionStart, itemCount, payload)

            override fun notifyItemRangeInserted(positionStart: Int, itemCount: Int) =
                this@UsersRvAdapter.notifyItemRangeInserted(positionStart, itemCount)

            override fun notifyItemRangeRemoved(position: Int, itemCount: Int) =
                this@UsersRvAdapter.notifyItemRangeRemoved(position, itemCount)

            override fun notifyItemRemoved(position: Int) =
                this@UsersRvAdapter.notifyItemRemoved(position)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
            dataBindingComponent
        )
        binding.usersAdapterViewModel = usersAdapterViewModel
        return VH(binding)
    }

    override fun getItemCount() = usersAdapterViewModel.getItemCount()

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(position)

    internal fun onDestroyView() = usersAdapterViewModel.unsubscribe()

    inner class VH(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

        internal fun bind(position: Int) {
            binding.position = position
        }
    }
}