package com.example.cineverse.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import coil.load
import com.example.cineverse.R
import com.example.cineverse.databinding.FragmentMovieDetailBinding
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.ui.common.extension.setVisible
import com.example.cineverse.util.Resource
import com.example.cineverse.util.collectOnStarted
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MovieDetailFragment : Fragment() {

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MovieDetailViewModel by viewModels()

    /** Backs the Share toolbar action; only shareable once the movie has loaded. */
    private var loadedMovie: Movie? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarMenu()
        binding.retryButton.setOnClickListener { viewModel.retry() }
        binding.favoriteButton.setOnClickListener { viewModel.toggleFavorite() }

        viewModel.uiState.collectOnStarted(this) { state -> render(state) }
        viewModel.isFavorite.collectOnStarted(this) { isFavorite -> renderFavoriteButton(isFavorite) }
    }

    private fun setupToolbarMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.movie_detail_toolbar_menu, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.findItem(R.id.action_share)?.isEnabled = loadedMovie != null
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.action_share -> {
                        shareMovie()
                        true
                    }
                    else -> false
                }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun shareMovie() {
        val movie = loadedMovie ?: return
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_movie_text, movie.title))
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
    }

    private fun renderFavoriteButton(isFavorite: Boolean) {
        val iconRes = if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline
        val tintRes = if (isFavorite) R.color.favorite_heart_tint else R.color.bottom_nav_item_color
        binding.favoriteButton.setImageResource(iconRes)
        binding.favoriteButton.imageTintList = ContextCompat.getColorStateList(requireContext(), tintRes)
        binding.favoriteButton.contentDescription = getString(
            if (isFavorite) R.string.action_remove_favorite else R.string.action_add_favorite
        )
    }

    private fun render(state: Resource<Movie>) {
        binding.progressBar.setVisible(state is Resource.Loading)
        binding.errorStateLayout.setVisible(state is Resource.Error)
        binding.contentScrollView.setVisible(state is Resource.Success)

        if (state is Resource.Success) {
            bindMovie(state.data)
        }

        val hadMovie = loadedMovie != null
        loadedMovie = (state as? Resource.Success)?.data
        if (hadMovie != (loadedMovie != null)) {
            requireActivity().invalidateMenu()
        }
    }

    private fun bindMovie(movie: Movie) {
        binding.posterImageView.load(movie.posterUrl) {
            placeholder(R.drawable.ic_movie_placeholder)
            error(R.drawable.ic_movie_placeholder)
        }
        binding.titleTextView.text = movie.title
        binding.metaTextView.text = getString(
            R.string.movie_meta_format,
            String.format(Locale.getDefault(), "%.1f", movie.rating),
            movie.releaseDate,
            movie.genre
        )
        binding.overviewTextView.text = movie.overview
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
