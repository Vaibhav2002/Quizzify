package dev.vaibhav.quizzify.ui.screens.gameScreens.game

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentGameBinding
import dev.vaibhav.quizzify.ui.adapters.OptionAdapter
import dev.vaibhav.quizzify.util.*
import timber.log.Timber

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {

    private val binding by viewBinding(FragmentGameBinding::bind)
    private val viewModel by viewModels<GameViewModel>()
    private lateinit var optionAdapter: OptionAdapter
    private var isErrorDialogVisible = false

    private val args by navArgs<GameFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListener()
        collectUiState()
        collectUiEvents()
        viewModel.setGameId(args.gameId)
        handleOnGameBackPress(
            viewModel::isUserTheHost,
            viewModel::onHostExitDialogConfirmPressed,
            viewModel::onLeaveDialogConfirmPressed
        )
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        binding.apply {
            Timber.d(it.options.toString())
            optionAdapter.submitList(it.options)
            titleTv.text = it.title
            timeLeftTV.text = it.timeLeftText
            roundedProgressBar.setProgressPercentage(it.progress, true)
            questionCount.text = it.questionCountText
            questionTv.text = it.question.decodeHtml()
            rankTv.text = it.rankText
            submitBtn.isEnabled = it.isButtonEnabled
            progressContainer.isVisible = it.isLoading
        }
    }

    private fun collectUiEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            is GameScreenEvents.NavigateToGameCompleteScreen -> {
                val action =
                    GameFragmentDirections.actionGameFragmentToWinningFragment(it.game)
                findNavController().navigate(action)
            }
            is GameScreenEvents.NavigateToWaitingScreen -> {
                val action =
                    GameFragmentDirections.actionGameFragmentToWaitingForFinishFragment(it.gameId)
                findNavController().navigate(action)
            }
            is GameScreenEvents.ShowToast -> requireContext().showToast(it.message)
            GameScreenEvents.NavigateBack -> findNavController().popBackStack()
            is GameScreenEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@GameFragment::isErrorDialogVisible
            )
            GameScreenEvents.Vibrate -> requireContext().vibrate()
        }
    }

    private fun initViews() = binding.apply {
        optionAdapter = OptionAdapter(viewModel::onOptionPressed)
        optionRv.apply {
            setHasFixedSize(false)
            adapter = optionAdapter
        }
    }

    private fun initListener() = binding.apply {
        titleTv.setMarginTop(Constants.TITLE_TOP_MARGIN)
        submitBtn.setOnClickListener { viewModel.onSubmitButtonPressed() }
    }
}