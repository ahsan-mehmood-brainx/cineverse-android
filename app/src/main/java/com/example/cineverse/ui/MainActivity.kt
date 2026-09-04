package com.example.cineverse.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cineverse.databinding.ActivityMainBinding
import com.example.cineverse.ui.navigation.setupAppNavigation
import com.example.cineverse.util.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
        val navController = navHostFragment.navController

        val appBarConfiguration = setupAppNavigation(
            navController = navController,
            drawerLayout = binding.drawerLayout,
            navigationView = binding.navigationView,
            bottomNavigationView = binding.bottomNavigationView
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        requestNotificationPermissionIfNeeded()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (PermissionUtils.isGranted(this, Manifest.permission.POST_NOTIFICATIONS)) return
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
