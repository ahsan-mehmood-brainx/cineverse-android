package com.example.cineverse.ui.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.cineverse.databinding.ItemGenreChipBinding
import com.example.cineverse.domain.model.Genre

/** Horizontal, checkable genre chip list. Selection is purely visual until genre filtering ships. */
class GenreAdapter : BaseListAdapter<Genre, ItemGenreChipBinding>(DIFF_CALLBACK) {

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup): ItemGenreChipBinding =
        ItemGenreChipBinding.inflate(inflater, parent, false)

    override fun bind(binding: ItemGenreChipBinding, item: Genre, position: Int) {
        binding.root.text = item.name
    }

    private companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Genre>() {
            override fun areItemsTheSame(oldItem: Genre, newItem: Genre) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Genre, newItem: Genre) = oldItem == newItem
        }
    }
}
