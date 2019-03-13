package ru.wheelman.github.viewmodel

interface AdapterViewModelListener {

    fun notifyDataSetChanged()
    fun notifyItemChanged(position: Int, payload: Any? = null)
    fun notifyItemInserted(position: Int)
    fun notifyItemMoved(fromPosition: Int, toPosition: Int)
    fun notifyItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any? = null)
    fun notifyItemRangeInserted(positionStart: Int, itemCount: Int)
    fun notifyItemRangeRemoved(position: Int, itemCount: Int)
    fun notifyItemRemoved(position: Int)

}