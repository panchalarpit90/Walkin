package com.task.walkin.ui.dashboard.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.task.walkin.R
import com.task.walkin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var isNavigatingProgrammatically = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.main_nav)

        setupBottomNav()
        setupNavListener()


        if (savedInstanceState == null) {
            binding.bottomNavBar.selectItem(2)
        }
    }
    private fun setupBottomNav() {
        binding.bottomNavBar.onItemSelected = listener@{ index ->

            if (isNavigatingProgrammatically) {
                return@listener
            }

            val destinationId = destinationFor(index) ?: return@listener

            navigateTo(destinationId)
        }
    }

    private fun setupNavListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val index = indexFor(destination.id) ?: return@addOnDestinationChangedListener
            if (binding.bottomNavBar.getSelectedIndex() != index) {
                isNavigatingProgrammatically = true
                binding.bottomNavBar.selectItemWithoutCallback(index)
                isNavigatingProgrammatically = false
            }
        }
    }

    private fun navigateTo(destinationId: Int) {

        if (navController.currentDestination?.id == destinationId) {
            return
        }

        val currentNode = navController.currentDestination ?: return

        val action = currentNode.getAction(destinationId)

        try {
            navController.navigate(destinationId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun destinationFor(index: Int): Int? = when (index) {
        0 -> R.id.my_booking_fragment
        1 -> R.id.scan_qr_fragment
        2 -> R.id.home_fragment
        3 -> R.id.my_qr_fragment
        4 -> R.id.profile_fragment
        else -> null
    }

    private fun indexFor(destinationId: Int): Int? = when (destinationId) {
        R.id.my_booking_fragment -> 0
        R.id.scan_qr_fragment   -> 1
        R.id.home_fragment      -> 2
        R.id.my_qr_fragment     -> 3
        R.id.profile_fragment   -> 4
        else                    -> null
    }
}
