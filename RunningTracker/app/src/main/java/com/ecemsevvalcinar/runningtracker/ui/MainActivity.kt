package com.ecemsevvalcinar.runningtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.runningtracker.R
import com.example.runningtracker.databinding.ActivityMainBinding
import com.ecemsevvalcinar.runningtracker.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        navigateToTrackingFragmentIfNeeded(intent)

        setSupportActionBar(binding.toolbar)

        binding.bottomNavigationView.setupWithNavController(binding.flFragment[0].findNavController())

        binding.bottomNavigationView.setOnItemReselectedListener { /* NO-OP */

        }

        binding.flFragment[0].findNavController()
            .addOnDestinationChangedListener{_, destination, _ ->
                when (destination.id) {
                    R.id.settingsFragment, R.id.runFragment, R.id.statisticsFragment ->
                        binding.bottomNavigationView.visibility = View.VISIBLE
                    else -> binding.bottomNavigationView.visibility = View.GONE
                }
            }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    // clicking the notification in foreground service opens the trackingFragment
    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            binding.flFragment[0].findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }

}