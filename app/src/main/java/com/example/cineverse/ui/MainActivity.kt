package com.example.cineverse.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cineverse.databinding.ActivityMainBinding
import com.example.cineverse.ui.navigation.setupAppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
    }
}
