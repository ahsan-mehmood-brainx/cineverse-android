package com.example.cineverse.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.cineverse.R
import com.example.cineverse.databinding.FragmentEditProfileBinding
import com.example.cineverse.domain.model.Profile
import com.example.cineverse.ui.common.extension.setVisible
import com.example.cineverse.util.ImageStorageUtil
import com.example.cineverse.util.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Shares [ProfileViewModel] with the [ProfileFragment] that hosts it (rather than owning its
 * own), so a successful save is reflected on the profile screen the moment this sheet closes.
 */
@AndroidEntryPoint
class EditProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels({ requireParentFragment() })

    private var pendingImageUri: String? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) onImagePicked(uri)
        }

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
        binding.usernameEditText.setText(currentProfile.username)
        binding.emailEditText.setText(currentProfile.email)
        binding.bioEditText.setText(currentProfile.bio)
        pendingImageUri = currentProfile.profileImageUri
        bindAvatarPreview(currentProfile.profileImageUri, currentProfile.displayName)

        binding.changePhotoButton.setOnClickListener {
            pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.nameEditText.doOnTextChanged { _, _, _, _ ->
            binding.nameInputLayout.error = null
            if (pendingImageUri == null) {
                bindAvatarPreview(null, binding.nameEditText.text?.toString().orEmpty())
            }
        }
        binding.usernameEditText.doOnTextChanged { _, _, _, _ -> binding.usernameInputLayout.error = null }
        binding.emailEditText.doOnTextChanged { _, _, _, _ -> binding.emailInputLayout.error = null }
        binding.bioEditText.doOnTextChanged { _, _, _, _ -> binding.bioInputLayout.error = null }

        binding.saveButton.setOnClickListener { onSaveClicked() }
    }

    private fun onImagePicked(uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            val storedUri = withContext(Dispatchers.IO) {
                ImageStorageUtil.copyToInternalStorage(requireContext(), uri)
            }
            if (storedUri != null) {
                pendingImageUri = storedUri
                bindAvatarPreview(storedUri, binding.nameEditText.text?.toString().orEmpty())
            }
        }
    }

    private fun bindAvatarPreview(imageUri: String?, name: String) {
        val hasImage = !imageUri.isNullOrBlank()
        binding.avatarPreviewImage.setVisible(hasImage)
        binding.avatarPreviewInitialText.setVisible(!hasImage)
        if (hasImage) {
            binding.avatarPreviewImage.load(imageUri)
        } else {
            binding.avatarPreviewInitialText.text = if (name.isNotBlank()) {
                name.trim().first().uppercase(Locale.getDefault())
            } else {
                "?"
            }
        }
    }

    private fun onSaveClicked() {
        val name = binding.nameEditText.text?.toString().orEmpty()
        val username = binding.usernameEditText.text?.toString().orEmpty()
        val email = binding.emailEditText.text?.toString().orEmpty()
        val bio = binding.bioEditText.text?.toString().orEmpty()

        when (viewModel.validate(name, username, email, bio)) {
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
            ProfileFormError.BlankUsername -> {
                binding.usernameInputLayout.error = getString(R.string.profile_error_blank_username)
                return
            }
            ProfileFormError.UsernameTooLong -> {
                binding.usernameInputLayout.error = getString(
                    R.string.profile_error_username_too_long,
                    ProfileViewModel.MAX_USERNAME_LENGTH
                )
                return
            }
            ProfileFormError.BlankEmail -> {
                binding.emailInputLayout.error = getString(R.string.profile_error_blank_email)
                return
            }
            ProfileFormError.InvalidEmail -> {
                binding.emailInputLayout.error = getString(R.string.profile_error_invalid_email)
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

        viewModel.save(name, username, email, bio, pendingImageUri)
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
