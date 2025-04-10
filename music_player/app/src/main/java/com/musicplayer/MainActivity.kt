package com.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up bottom navigation with navigation controller
        binding.bottomNavigation.setupWithNavController(navController)

        // Hide bottom navigation when in player fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_player -> {
                    binding.bottomNavigation.visibility = android.view.View.GONE
                    binding.miniPlayer.visibility = android.view.View.GONE
                }
                else -> {
                    binding.bottomNavigation.visibility = android.view.View.VISIBLE
                    binding.miniPlayer.visibility = android.view.View.VISIBLE
                }
            }
        }

        // Set up mini player click to open full player
        binding.miniPlayer.setOnClickListener {
            navController.navigate(R.id.navigation_player)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
