package com.example.cineverse.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import coil.load
import com.example.cineverse.R
import com.example.cineverse.databinding.ActivityMainBinding
import com.example.cineverse.databinding.NavHeaderBinding
import com.example.cineverse.domain.model.Profile
import com.example.cineverse.ui.common.extension.setVisible
import com.example.cineverse.ui.navigation.setupAppNavigation
import com.example.cineverse.ui.profile.ProfileViewModel
import com.example.cineverse.util.PermissionUtils
import com.example.cineverse.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val profileViewModel: ProfileViewModel by viewModels()

    /** Result is intentionally ignored: MovieSyncWorker checks before every notification anyway. */
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = setupAppNavigation(
            navController = navController,
            drawerLayout = binding.drawerLayout,
            navigationView = binding.navigationView,
            bottomNavigationView = binding.bottomNavigationView
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Destination labels still drive talkback/back-stack titles; the toolbar itself
        // stays a clean icon bar (drawer/back + actions) with no title text.
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupDrawerHeader()
        requestNotificationPermissionIfNeeded()
    }

    private fun setupDrawerHeader() {
        val headerBinding = NavHeaderBinding.bind(binding.navigationView.getHeaderView(0))
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.uiState.collectLatest { state ->
                    val profile = (state as? Resource.Success)?.data ?: Profile.EMPTY
                    bindDrawerHeader(headerBinding, profile)
                }
            }
        }
    }

    private fun bindDrawerHeader(headerBinding: NavHeaderBinding, profile: Profile) {
        val hasName = profile.displayName.isNotBlank()
        val hasImage = !profile.profileImageUri.isNullOrBlank()

        headerBinding.avatarImage.setVisible(hasImage)
        headerBinding.avatarInitialText.setVisible(!hasImage)
        if (hasImage) {
            headerBinding.avatarImage.load(profile.profileImageUri)
        } else {
            headerBinding.avatarInitialText.text = if (hasName) {
                profile.displayName.trim().first().uppercase(Locale.getDefault())
            } else {
                "?"
            }
        }

        headerBinding.displayNameText.text = profile.displayName.ifBlank {
            getString(R.string.profile_name_placeholder)
        }
        headerBinding.emailText.text = profile.email
        headerBinding.emailText.setVisible(profile.email.isNotBlank())
    }

    // Required by NavigationUI: setupActionBarWithNavController only makes the Up indicator
    // show a back arrow on non-top-level destinations, it doesn't make that arrow do anything.
    override fun onSupportNavigateUp(): Boolean =
        navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (PermissionUtils.isGranted(this, Manifest.permission.POST_NOTIFICATIONS)) return
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
