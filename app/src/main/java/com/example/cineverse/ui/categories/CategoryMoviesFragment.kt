package com.example.cineverse.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cineverse.databinding.FragmentCategoryMoviesBinding
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.ui.common.adapter.MovieAdapter
import com.example.cineverse.ui.common.extension.setVisible
import com.example.cineverse.util.Resource
import com.example.cineverse.util.collectOnStarted
import dagger.hilt.android.AndroidEntryPoint

/** One ViewPager2 page: the movie list for a single [MovieCategory]. */
@AndroidEntryPoint
class CategoryMoviesFragment : Fragment() {

    private var _binding: FragmentCategoryMoviesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryMoviesViewModel by viewModels()
    private val adapter = MovieAdapter(::openMovieDetail)

    private val category: MovieCategory by lazy {
        requireArguments().getString(ARG_CATEGORY)?.let { MovieCategory.valueOf(it) }
            ?: MovieCategory.POPULAR
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
        binding.recyclerView.adapter = adapter
        binding.retryButton.setOnClickListener { viewModel.retry() }

        viewModel.load(category)
        viewModel.uiState.collectOnStarted(this) { state -> render(state) }
    }

    private fun render(state: Resource<List<Movie>>) {
        binding.progressBar.setVisible(state is Resource.Loading)
        binding.errorStateLayout.setVisible(state is Resource.Error)
        binding.recyclerView.setVisible(state is Resource.Success && state.data.isNotEmpty())
        binding.emptyStateLayout.setVisible(state is Resource.Success && state.data.isEmpty())

        if (state is Resource.Success) {
            adapter.submitList(state.data)
        }
    }

    private fun openMovieDetail(movie: Movie) {
        findNavController().navigate(CategoriesFragmentDirections.actionCategoriesToMovieDetail(movie.id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CATEGORY = "arg_category"
        private const val SPAN_COUNT = 3

        fun newInstance(category: MovieCategory): CategoryMoviesFragment = CategoryMoviesFragment().apply {
            arguments = bundleOf(ARG_CATEGORY to category.name)
        }
    }
}
