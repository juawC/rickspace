package com.app.juawcevada.rickspace.ui.shared

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


/**
 * PagedList Recycler view to be used with data bound views.
 */


abstract class PagedRecyclerAdapter<DataType, BindingType : ViewDataBinding>(
        comparator: DiffUtil.ItemCallback<DataType>
) : PagedListAdapter<DataType, PagedRecyclerAdapter.ViewHolder<BindingType>>(comparator) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<BindingType> {
        val binding = createBinding(parent)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<BindingType>, position: Int) {
        getItem(position)?.let {
            bind(holder.binding, it)
            holder.binding.executePendingBindings()
        }
    }

    protected abstract fun bind(binding: BindingType, item: DataType)

    protected abstract fun createBinding(parent: ViewGroup): BindingType

    class ViewHolder<out DataType : ViewDataBinding>(
            val binding: DataType
    ) : RecyclerView.ViewHolder(binding.root)
}
