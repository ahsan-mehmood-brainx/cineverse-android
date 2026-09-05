package com.example.cineverse.ui.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import coil.load
import com.example.cineverse.R
import com.example.cineverse.databinding.ItemMovieCardBinding
import com.example.cineverse.domain.model.Movie
import java.util.Locale

/**
 * Horizontal movie-card list shared by the Trending, Popular and Top Rated sections.
 *
 * The favorite button only renders when [onFavoriteClick] is supplied, so screens that don't
 * need it (e.g. Favorites, which already offers swipe-to-delete) get the plain card unchanged.
 */
class MovieAdapter(
    private val onMovieClick: (Movie) -> Unit,
    private val onFavoriteClick: ((Movie) -> Unit)? = null
) : BaseListAdapter<Movie, ItemMovieCardBinding>(DIFF_CALLBACK) {

    private var favoriteIds: Set<Int> = emptySet()

    /** Updates which items render a filled heart; re-binds visible rows to reflect the change. */
    fun setFavoriteIds(ids: Set<Int>) {
        if (ids == favoriteIds) return
        favoriteIds = ids
        notifyItemRangeChanged(0, itemCount)
    }

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
        bindFavoriteButton(binding, item)
    }

    private fun bindFavoriteButton(binding: ItemMovieCardBinding, item: Movie) {
        val onFavoriteClick = onFavoriteClick
        binding.favoriteButton.isVisible = onFavoriteClick != null
        if (onFavoriteClick == null) return

        val isFavorite = item.id in favoriteIds
        val context = binding.favoriteButton.context
        binding.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline
        )
        binding.favoriteButton.imageTintList = ContextCompat.getColorStateList(
            context,
            if (isFavorite) R.color.favorite_heart_tint else R.color.white
        )
        binding.favoriteButton.contentDescription = context.getString(
            if (isFavorite) R.string.action_remove_favorite else R.string.action_add_favorite
        )
        binding.favoriteButton.setOnClickListener { onFavoriteClick(item) }
    }

    private companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
        }
    }
}
