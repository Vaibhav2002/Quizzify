package dev.vaibhav.quizzify.ui.screens.quizScreens.quizDetails

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentQuizDetailsBinding
import dev.vaibhav.quizzify.util.*

@AndroidEntryPoint
class QuizDetailsFragment : Fragment(R.layout.fragment_quiz_details) {

    private val binding by viewBinding(FragmentQuizDetailsBinding::bind)
    private val viewModel by viewModels<QuizDetailViewModel>()
    private val args by navArgs<QuizDetailsFragmentArgs>()
    private var isErrorDialogVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSharedElementTransition()
        initViews()
        initListeners()
        collectUiState()
        collectEvents()
        viewModel.setData(args.quiz)
    }

    private fun setUpSharedElementTransition() {
        sharedElementEnterTransition = MaterialContainerTransform()
    }

    private fun collectEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            is QuizDetailScreenEvents.NavigateToGameScreen -> {
                val action =
                    QuizDetailsFragmentDirections.actionQuizDetailsFragmentToGameFragment(it.gameId)
                findNavController().navigate(action)
            }
            is QuizDetailScreenEvents.NavigateToWaitingForPlayerScreen -> {
                val action =
                    QuizDetailsFragmentDirections.actionQuizDetailsFragmentToWaitingForPlayersFragment(
                        it.gameId
                    )
                findNavController().navigate(action)
            }
            is QuizDetailScreenEvents.ShowToast -> requireContext().showToast(it.message)
            is QuizDetailScreenEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@QuizDetailsFragment::isErrorDialogVisible
            )
        }
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        binding.apply {
            titleTv.text = it.title
            descriptionTv.text = it.description
            progressContainer.isVisible = it.isLoading
            categoryImage.setImageUrl(it.image)
            questionTv.text = "Questions: ${it.questionCount}"
            categoryTv.text = "Category: ${it.categoryName}"
            favIcon.setImageResource(if (it.isFavourite) R.drawable.ic_star_filled else R.drawable.ic_star_outlined)
            playWithFriendsBtn.isEnabled = it.isGameButtonsEnabled
            playSoloBtn.isEnabled = it.isGameButtonsEnabled
            favIcon.isEnabled = it.isFavButtonEnabled
        }
    }

    private fun initListeners() = binding.apply {
        playWithFriendsBtn.setOnClickListener { viewModel.onPlayWithFriendsPressed() }
        playSoloBtn.setOnClickListener { viewModel.onPlaySoloPressed() }
        favIcon.setOnClickListener { viewModel.onFavouriteButtonPressed() }
    }

    private fun initViews() = binding.apply {
        root.transitionName = args.quiz.id
    }
}