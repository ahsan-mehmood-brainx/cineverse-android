package com.example.cineverse.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.example.cineverse.R
import com.example.cineverse.databinding.FragmentEditProfileBinding
import com.example.cineverse.domain.model.Profile
import com.example.cineverse.util.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Shares [ProfileViewModel] with the [ProfileFragment] that hosts it (rather than owning its
 * own), so a successful save is reflected on the profile screen the moment this sheet closes.
 */
@AndroidEntryPoint
class EditProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentProfile = (viewModel.uiState.value as? Resource.Success)?.data ?: Profile.EMPTY
        binding.nameEditText.setText(currentProfile.displayName)
        binding.bioEditText.setText(currentProfile.bio)

        binding.nameEditText.doOnTextChanged { _, _, _, _ -> binding.nameInputLayout.error = null }
        binding.bioEditText.doOnTextChanged { _, _, _, _ -> binding.bioInputLayout.error = null }

        binding.saveButton.setOnClickListener { onSaveClicked() }
    }

    private fun onSaveClicked() {
        val name = binding.nameEditText.text?.toString().orEmpty()
        val bio = binding.bioEditText.text?.toString().orEmpty()

        when (viewModel.validate(name, bio)) {
            ProfileFormError.BlankName -> {
                binding.nameInputLayout.error = getString(R.string.profile_error_blank_name)
                return
            }
            ProfileFormError.NameTooLong -> {
                binding.nameInputLayout.error = getString(
                    R.string.profile_error_name_too_long,
                    ProfileViewModel.MAX_NAME_LENGTH
                )
                return
            }
            ProfileFormError.BioTooLong -> {
                binding.bioInputLayout.error = getString(
                    R.string.profile_error_bio_too_long,
                    ProfileViewModel.MAX_BIO_LENGTH
                )
                return
            }
            null -> Unit
        }

        viewModel.save(name, bio)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "EditProfileBottomSheet"
    }
}
