package com.example.cineverse.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cineverse.databinding.FragmentSearchBinding
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.ui.common.adapter.MovieAdapter
import com.example.cineverse.ui.common.extension.setVisible
import com.example.cineverse.util.Resource
import com.example.cineverse.util.collectOnStarted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private val adapter = MovieAdapter(::openMovieDetail)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
        binding.recyclerView.adapter = adapter

        binding.retryButton.setOnClickListener { viewModel.retry() }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.onQueryChanged(text?.toString().orEmpty())
        }

        viewModel.uiState.collectOnStarted(this) { state -> render(state) }
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

    private fun openMovieDetail(movie: Movie) {
        findNavController().navigate(SearchFragmentDirections.actionSearchToMovieDetail(movie.id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val SPAN_COUNT = 3
    }
}
