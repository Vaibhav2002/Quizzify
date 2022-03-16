package dev.vaibhav.quizzify.ui.screens.gameScreens.winning

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentWinningBinding
import dev.vaibhav.quizzify.util.*

@AndroidEntryPoint
class WinningFragment : Fragment(R.layout.fragment_winning) {

    private val binding by viewBinding(FragmentWinningBinding::bind)
    private val viewModel by viewModels<WinningViewModel>()
    private val args by navArgs<WinningFragmentArgs>()
    private var isErrorDialogVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        collectUiState()
        collectUiEvents()
        viewModel.setGame(args.game)
        binding.konfettiView.start(KonfettiPresets.rain)
    }

    private fun collectUiEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            WinningScreenEvents.NavigateToHome -> findNavController().popBackStack()
            WinningScreenEvents.SHowConfetti -> binding.konfettiView.start(KonfettiPresets.rain)
            is WinningScreenEvents.ShowToast -> requireContext().showToast(it.message)
            is WinningScreenEvents.OpenRankingBottomSheet -> {
                val action =
                    WinningFragmentDirections.actionWinningFragmentToRankingFragment(it.game)
                findNavController().navigate(action)
            }
            is WinningScreenEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@WinningFragment::isErrorDialogVisible
            )
        }
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        binding.apply {
            profilePic.setProfilePic(it.profilePic)
            rankTv.text = it.rankString
            progressContainer.isVisible = it.isLoading
            upvoteButton.showOrGone(it.isUpvoteButtonVisible)
            upvoteButton.isEnabled = it.isUpvoteButtonEnabled
            upvoteTv.showOrGone(it.isUpvoteButtonVisible)
        }
    }

    private fun initListeners() = binding.apply {
        homeButton.setOnClickListener { viewModel.onHomeButtonPressed() }
        upvoteButton.setOnClickListener { viewModel.onUpvoteButtonPressed() }
        rankingButton.setOnClickListener { viewModel.onRankingButtonPressed() }
    }

    private fun initViews() = Unit
}