package com.example.cineverse.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cineverse.R
import com.example.cineverse.databinding.FragmentProfileBinding
import com.example.cineverse.domain.model.Profile
import com.example.cineverse.ui.common.extension.setVisible
import com.example.cineverse.util.Resource
import com.example.cineverse.util.collectOnStarted
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editProfileButton.setOnClickListener {
            EditProfileBottomSheetFragment().show(childFragmentManager, EditProfileBottomSheetFragment.TAG)
        }

        viewModel.uiState.collectOnStarted(this) { state -> render(state) }
    }

    private fun render(state: Resource<Profile>) {
        binding.progressBar.setVisible(state is Resource.Loading)
        binding.errorStateLayout.setVisible(state is Resource.Error)
        binding.contentScrollView.setVisible(state is Resource.Success)

        if (state is Resource.Success) {
            bindProfile(state.data)
        }
    }

    private fun bindProfile(profile: Profile) {
        val hasName = profile.displayName.isNotBlank()
        binding.avatarInitialText.text = if (hasName) {
            profile.displayName.trim().first().uppercase(Locale.getDefault())
        } else {
            "?"
        }
        binding.displayNameText.text = if (hasName) profile.displayName else getString(R.string.profile_name_placeholder)
        binding.bioText.text = profile.bio.ifBlank { getString(R.string.profile_bio_placeholder) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
