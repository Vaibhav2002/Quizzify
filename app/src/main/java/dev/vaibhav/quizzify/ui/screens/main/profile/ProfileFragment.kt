package dev.vaibhav.quizzify.ui.screens.main.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentProfileBinding
import dev.vaibhav.quizzify.ui.screens.auth.AuthActivity
import dev.vaibhav.quizzify.util.*

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel by viewModels<ProfileViewModel>()
    private var isErrorDialogVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        collectUiState()
        collectEvents()
    }

    private fun collectEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            ProfileScreenEvents.NavigateToAboutScreen -> findNavController().navigate(R.id.action_profileFragment_to_aboutFragment)
            ProfileScreenEvents.NavigateToAuthScreen -> navigateToAuthScreen()
            is ProfileScreenEvents.ShowToast -> requireContext().showToast(it.message)
            is ProfileScreenEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@ProfileFragment::isErrorDialogVisible
            )
        }
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        binding.apply {
            usernameTv.text = it.userName
            profilePic.setImageUrl(it.profilePic)
            wonCount.text = it.quizzesWon.toString()
            playedCount.text = it.quizzedPlayed.toString()
            expCount.text = it.exp.toString()
            swipeRefresh.isRefreshing = it.isRefreshing
            progressContainer.isVisible = it.isLoading
        }
    }

    private fun initListeners() = binding.apply {
        aboutButton.setOnClickListener { viewModel.onAboutButtonPressed() }
        logoutButton.setOnClickListener { openLogoutDialog() }
        swipeRefresh.setOnRefreshListener { viewModel.onRefreshed() }
    }

    private fun initViews() = binding.apply {
        imageView2.setMarginTop(32)
    }

    private fun openLogoutDialog() {
        requireContext().showAlertDialog(
            title = "Confirm Logout",
            description = "Are you sure that you want to logout?",
            positiveButtonText = "Logout",
            onConfirmClick = viewModel::onLogoutConfirmed
        )
    }

    private fun navigateToAuthScreen() {
        Intent(requireContext(), AuthActivity::class.java).also {
            startActivity(it)
            requireActivity().finish()
        }
    }
}