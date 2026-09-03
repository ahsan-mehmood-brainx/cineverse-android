package com.example.cineverse.ui.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

/**
 * Generic ViewBinding + DiffUtil ListAdapter. Feature adapters extend this and provide
 * the binding inflater, the item DiffUtil callback and the bind function — no boilerplate
 * RecyclerView.Adapter/DiffUtil.ItemCallback wiring per screen.
 */
abstract class BaseListAdapter<T : Any, B : ViewBinding>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, BaseListAdapter.BindingViewHolder<B>>(diffCallback) {

    abstract fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup): B
    abstract fun bind(binding: B, item: T, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<B> {
        val binding = inflateBinding(LayoutInflater.from(parent.context), parent)
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<B>, position: Int) {
        bind(holder.binding, getItem(position), position)
    }

    class BindingViewHolder<B : ViewBinding>(val binding: B) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)
}
