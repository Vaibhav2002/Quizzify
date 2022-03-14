package dev.vaibhav.quizzify.ui.screens.gameScreens.waitingForFinish

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentWaitingForFinishBinding
import dev.vaibhav.quizzify.util.*

@AndroidEntryPoint
class WaitingForFinishFragment : Fragment(R.layout.fragment_waiting_for_finish) {

    private val binding by viewBinding(FragmentWaitingForFinishBinding::bind)
    private val viewModel by viewModels<WaitingForFinishViewModel>()
    private val args by navArgs<WaitingForFinishFragmentArgs>()
    private var isErrorDialogVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleTv.setMarginTop(Constants.TITLE_TOP_MARGIN)
        collectUiState()
        collectUiEvents()
        viewModel.setGameId(args.gameId)
        handleOnGameBackPress(
            viewModel::isUserTheHost,
            viewModel::onHostExitDialogConfirmPressed,
            viewModel::onLeaveDialogConfirmPressed
        )
    }

    private fun collectUiEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            WaitingForFinishEvents.NavigateBack -> findNavController().popBackStack()
            is WaitingForFinishEvents.NavigateToGameCompleteScreen -> {
                val action =
                    WaitingForFinishFragmentDirections.actionWaitingForFinishFragmentToWinningFragment(
                        it.game
                    )
                findNavController().navigate(action)
            }
            is WaitingForFinishEvents.ShowToast -> requireContext().showToast(it.message)
            is WaitingForFinishEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@WaitingForFinishFragment::isErrorDialogVisible
            )
        }
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        binding.profilePic.setProfilePic(it.userImage)
        binding.progressContainer.isVisible = it.isLoading
    }
}