package com.example.cineverse.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineverse.R
import com.example.cineverse.databinding.FragmentHomeBinding
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.ui.common.adapter.GenreAdapter
import com.example.cineverse.ui.common.adapter.MovieAdapter
import com.example.cineverse.ui.common.extension.setVisible
import com.example.cineverse.util.Resource
import com.example.cineverse.util.collectOnStarted
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private val trendingAdapter = MovieAdapter(::openMovieDetail)
    private val popularAdapter = MovieAdapter(::openMovieDetail)
    private val topRatedAdapter = MovieAdapter(::openMovieDetail)
    private val genreAdapter = GenreAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarMenu()
        setupSections()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.retry()
            binding.swipeRefreshLayout.isRefreshing = false
        }
        binding.retryButton.setOnClickListener { viewModel.retry() }

        viewModel.uiState.collectOnStarted(this) { state -> render(state) }
    }

    private fun setupToolbarMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.action_search -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeToSearch())
                        true
                    }
                    else -> false
                }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupSections() {
        binding.trendingHeader.sectionTitleText.text = getString(R.string.section_trending)
        binding.popularHeader.sectionTitleText.text = getString(R.string.section_popular)
        binding.topRatedHeader.sectionTitleText.text = getString(R.string.section_top_rated)

        binding.trendingRecyclerView.setUpHorizontal(trendingAdapter)
        binding.popularRecyclerView.setUpHorizontal(popularAdapter)
        binding.topRatedRecyclerView.setUpHorizontal(topRatedAdapter)
        binding.genresRecyclerView.setUpHorizontal(genreAdapter)
    }

    private fun RecyclerView.setUpHorizontal(recyclerAdapter: RecyclerView.Adapter<*>) {
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        adapter = recyclerAdapter
        setHasFixedSize(true)
    }

    private fun render(state: Resource<HomeUiState>) {
        binding.progressBar.setVisible(state is Resource.Loading)
        binding.errorStateLayout.setVisible(state is Resource.Error)
        binding.swipeRefreshLayout.setVisible(state !is Resource.Error)
        binding.emptyStateLayout.setVisible(state is Resource.Success && state.data.isEmpty)

        if (state is Resource.Success) {
            trendingAdapter.submitList(state.data.trendingMovies)
            popularAdapter.submitList(state.data.popularMovies)
            topRatedAdapter.submitList(state.data.topRatedMovies)
            genreAdapter.submitList(state.data.genres)
        }
    }

    private fun openMovieDetail(movie: Movie) {
        findNavController().navigate(HomeFragmentDirections.actionHomeToMovieDetail(movie.id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
