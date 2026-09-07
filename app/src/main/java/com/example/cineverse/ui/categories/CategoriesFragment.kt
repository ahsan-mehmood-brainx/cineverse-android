package com.example.cineverse.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import coil.load
import com.example.cineverse.databinding.FragmentCategoriesBinding
import com.example.cineverse.domain.model.Profile
import com.example.cineverse.ui.common.extension.setVisible
import com.example.cineverse.ui.profile.ProfileViewModel
import com.example.cineverse.util.Resource
import com.example.cineverse.util.collectOnStarted
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

/** Movie category tabs shown in [CategoriesFragment]'s ViewPager2. */
enum class MovieCategory(val label: String) {
    POPULAR("Popular"),
    TOP_RATED("Top Rated"),
    UPCOMING("Upcoming"),
    NOW_PLAYING("Now Playing")
}

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = CategoryPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = MovieCategory.entries[position].label
        }.attach()

        profileViewModel.uiState.collectOnStarted(this) { state -> renderProfileHeader(state) }
    }

    private fun renderProfileHeader(state: Resource<Profile>) {
        val profile = (state as? Resource.Success)?.data ?: return
        val hasName = profile.displayName.isNotBlank()
        val hasImage = !profile.profileImageUri.isNullOrBlank()

        binding.avatarImage.setVisible(hasImage)
        binding.avatarInitialText.setVisible(!hasImage)
        if (hasImage) {
            binding.avatarImage.load(profile.profileImageUri)
        } else {
            binding.avatarInitialText.text = if (hasName) {
                profile.displayName.trim().first().uppercase(Locale.getDefault())
            } else {
                "?"
            }
        }

        binding.usernameText.text = if (profile.username.isNotBlank()) {
            "@${profile.username}"
        } else {
            profile.displayName
        }
        binding.emailText.text = profile.email
        binding.emailText.setVisible(profile.email.isNotBlank())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class CategoryPagerAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int = MovieCategory.entries.size

        override fun createFragment(position: Int): Fragment =
            CategoryMoviesFragment.newInstance(MovieCategory.entries[position])
    }
}
