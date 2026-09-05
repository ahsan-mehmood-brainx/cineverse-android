package com.example.cineverse.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cineverse.databinding.ActivityMainBinding
import com.example.cineverse.ui.navigation.setupAppNavigation
import com.example.cineverse.util.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

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

        requestNotificationPermissionIfNeeded()
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
