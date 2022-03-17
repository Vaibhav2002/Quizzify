package dev.vaibhav.quizzify.ui.screens.gameScreens.waitingForPlayers

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentWaitingForPlayersBinding
import dev.vaibhav.quizzify.ui.adapters.PlayerAdapter
import dev.vaibhav.quizzify.util.*
import dev.vaibhav.quizzify.util.Constants.TITLE_TOP_MARGIN

@AndroidEntryPoint
class WaitingForPlayersFragment : Fragment(R.layout.fragment_waiting_for_players) {

    private val binding by viewBinding(FragmentWaitingForPlayersBinding::bind)
    private val viewModel by viewModels<WaitingForPlayerViewModel>()
    private val args by navArgs<WaitingForPlayersFragmentArgs>()
    private var isErrorDialogVisible = false

    private lateinit var playerAdapter: PlayerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        collectUiState()
        collectUIEvents()
        viewModel.setGameCode(args.gameId)
        handleOnGameBackPress(
            viewModel::isUserTheHost,
            viewModel::onHostExitDialogConfirmPressed,
            viewModel::onLeaveDialogConfirmPressed
        )
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        binding.apply {
            inviteCodeTv.text = it.inviteCode
            playerAdapter.submitList(it.players)
            progressContainer.isVisible = it.isLoading
            startGameBtn.showOrGone(it.isStartGameButtonVisible)
            startGameBtn.isEnabled = it.isStartGameButtonEnabled
        }
    }

    private fun collectUIEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            is WaitingForPlayerScreenEvents.NavigateToGameScreen -> {
                val action =
                    WaitingForPlayersFragmentDirections.actionWaitingForPlayersFragmentToGameFragment(
                        it.gameId
                    )
                findNavController().navigate(action)
            }
            is WaitingForPlayerScreenEvents.ShowToast -> requireContext().showToast(it.message)
            WaitingForPlayerScreenEvents.NavigateBack -> findNavController().popBackStack()
            is WaitingForPlayerScreenEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@WaitingForPlayersFragment::isErrorDialogVisible
            )
        }
    }

    private fun initListeners() = binding.apply {
        startGameBtn.setOnClickListener { viewModel.onStartGamePressed() }
        copyBtn.setOnClickListener {
            requireContext().copyToClipboard(inviteCodeTv.text.toString().trim())
            requireContext().showToast("Copied to clipboard")
        }
    }

    private fun initViews() = binding.apply {
        playerAdapter = PlayerAdapter()
        playerRv.apply {
            setHasFixedSize(false)
            adapter = playerAdapter
        }
        titleTv.setMarginTop(TITLE_TOP_MARGIN)
    }
}