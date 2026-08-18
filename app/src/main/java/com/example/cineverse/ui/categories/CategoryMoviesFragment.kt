package com.example.cineverse.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.cineverse.databinding.FragmentCategoryMoviesBinding
import dagger.hilt.android.AndroidEntryPoint

/** One ViewPager2 page: the movie list for a single [MovieCategory]. */
@AndroidEntryPoint
class CategoryMoviesFragment : Fragment() {

    private var _binding: FragmentCategoryMoviesBinding? = null
    private val binding get() = _binding!!

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CATEGORY = "arg_category"

        fun newInstance(category: MovieCategory): CategoryMoviesFragment = CategoryMoviesFragment().apply {
            arguments = bundleOf(ARG_CATEGORY to category.name)
        }
    }
}
