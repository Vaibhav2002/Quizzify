package dev.vaibhav.quizzify.ui.screens.main

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.ActivityMainBinding
import dev.vaibhav.quizzify.ui.screens.errorDialog.ErrorDialogFragment
import dev.vaibhav.quizzify.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var navController: NavController
    private var isErrorDialogVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        makeStatusBarTransparent()
        navController = findNavController(R.id.main_nav_host)
        setUpBottomNav()
        collectUiState()
        collectEvents()

        val fragmentsWithBottomNav = listOf(
            R.id.homeFragment,
            R.id.communityFragment,
            R.id.profileFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.visibility =
                if (destination.id in fragmentsWithBottomNav) VISIBLE else GONE
        }
    }

    private fun setUpBottomNav() {
        val popUpMenu = PopupMenu(this, null)
        popUpMenu.inflate(R.menu.bottom_nav_menu)
        binding.bottomNav.setupWithNavController(popUpMenu.menu, navController)
        binding.bottomNav.onTabSelected = {
            vibrate()
        }
    }

    private fun collectEvents() = viewModel.events.launchAndCollectLatest(this) {
        when (it) {
            is MainScreenEvents.ShowErrorDialog -> if (!isErrorDialogVisible) showErrorDialog(it.errorType)
            is MainScreenEvents.ShowToast -> showToast(it.message)
        }
    }

    private fun showErrorDialog(errorType: ErrorType) {
        isErrorDialogVisible = true
        ErrorDialogFragment(errorType) {
            isErrorDialogVisible = false
        }.apply {
            show(supportFragmentManager, ERROR_DIALOG)
        }
    }

    private fun collectUiState() = viewModel.isLoading.launchAndCollectLatest(this) {
        binding.progressContainer.isVisible = it
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return true
    }
}