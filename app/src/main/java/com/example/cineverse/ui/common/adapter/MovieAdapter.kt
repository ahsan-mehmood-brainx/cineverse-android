package com.example.cineverse.ui.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.example.cineverse.R
import com.example.cineverse.databinding.ItemMovieCardBinding
import com.example.cineverse.domain.model.Movie
import java.util.Locale

/** Horizontal movie-card list shared by the Trending, Popular and Top Rated sections. */
class MovieAdapter(
    private val onMovieClick: (Movie) -> Unit
) : BaseListAdapter<Movie, ItemMovieCardBinding>(DIFF_CALLBACK) {

    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup): ItemMovieCardBinding =
        ItemMovieCardBinding.inflate(inflater, parent, false)

    override fun bind(binding: ItemMovieCardBinding, item: Movie, position: Int) {
        binding.posterImage.load(item.posterUrl) {
            placeholder(R.drawable.ic_movie_placeholder)
            error(R.drawable.ic_movie_placeholder)
        }
        binding.titleText.text = item.title
        binding.ratingText.text = String.format(Locale.getDefault(), "%.1f", item.rating)
        binding.root.setOnClickListener { onMovieClick(item) }
    }

    private companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
        }
    }
}
