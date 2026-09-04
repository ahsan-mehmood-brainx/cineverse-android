package com.example.cineverse.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineverse.R
import com.example.cineverse.databinding.FragmentFavoritesBinding
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.ui.common.adapter.MovieAdapter
import com.example.cineverse.ui.common.adapter.SwipeToDeleteCallback
import com.example.cineverse.ui.common.extension.setVisible
import com.example.cineverse.util.Resource
import com.example.cineverse.util.collectOnStarted
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels()
    private val adapter = MovieAdapter(::openMovieDetail)

    private var confirmRemovalDialog: androidx.appcompat.app.AlertDialog? = null
    private var swipedPosition = RecyclerView.NO_POSITION

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
        binding.recyclerView.adapter = adapter

        SwipeToDeleteCallback(binding.recyclerView) { position ->
            val movie = adapter.currentList.getOrNull(position)
            if (movie != null) {
                swipedPosition = position
                viewModel.requestRemoval(movie)
            } else {
                adapter.notifyItemChanged(position)
            }
        }.attach()

        viewModel.uiState.collectOnStarted(this) { state -> render(state) }
        viewModel.pendingRemoval.collectOnStarted(this) { movie -> renderPendingRemoval(movie) }
    }

    private fun render(state: Resource<List<Movie>>) {
        binding.progressBar.setVisible(state is Resource.Loading)
        binding.errorStateLayout.setVisible(state is Resource.Error)
        binding.recyclerView.setVisible(state !is Resource.Error)
        binding.emptyStateLayout.setVisible(state is Resource.Success && state.data.isEmpty())

        if (state is Resource.Success) {
            adapter.submitList(state.data)
        }
    }

    private fun renderPendingRemoval(movie: Movie?) {
        if (movie == null) {
            confirmRemovalDialog?.dismiss()
            confirmRemovalDialog = null
            return
        }

        val positionToRestore = swipedPosition
        fun restoreSwipedRow() {
            if (positionToRestore != RecyclerView.NO_POSITION) {
                adapter.notifyItemChanged(positionToRestore)
            }
        }

        confirmRemovalDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.remove_favorite_title)
            .setMessage(getString(R.string.remove_favorite_message, movie.title))
            .setPositiveButton(R.string.action_remove) { _, _ -> viewModel.confirmPendingRemoval() }
            .setNegativeButton(R.string.action_cancel) { _, _ ->
                viewModel.cancelPendingRemoval()
                restoreSwipedRow()
            }
            .setOnCancelListener {
                viewModel.cancelPendingRemoval()
                restoreSwipedRow()
            }
            .show()
    }

    private fun openMovieDetail(movie: Movie) {
        findNavController().navigate(FavoritesFragmentDirections.actionFavoritesToMovieDetail(movie.id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        confirmRemovalDialog = null
        _binding = null
    }

    private companion object {
        const val SPAN_COUNT = 3
    }
}
