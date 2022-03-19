package dev.vaibhav.quizzify.ui.screens.main.favourites

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.data.models.remote.QuizDto
import dev.vaibhav.quizzify.databinding.FragmentFavouritesBinding
import dev.vaibhav.quizzify.ui.adapters.QuizAdapter
import dev.vaibhav.quizzify.util.*
import dev.vaibhav.quizzify.util.Constants.TITLE_TOP_MARGIN

@AndroidEntryPoint
class FavouritesFragment : Fragment(R.layout.fragment_favourites) {

    private val viewModel by viewModels<FavouritesViewModel>()
    private val binding by viewBinding(FragmentFavouritesBinding::bind)
    private lateinit var quizAdapter: QuizAdapter

    private var isErrorDialogVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        collectUiState()
        collectUiEvents()
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        quizAdapter.submitList(it.quizzes)
        binding.progressContainer.isVisible = it.isLoading
        binding.emptyStateContainer.isVisible = it.isEmptyStateVisible
        binding.emptyState.emptyStateText.text = getString(R.string.favourites_empty_state)
    }

    private fun collectUiEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            is FavouritesScreenEvents.NavigateToQuizDetailScreen -> {
                val action =
                    FavouritesFragmentDirections.actionFavouritesFragmentToQuizDetailsFragment(it.quizDto)
                findNavController().navigate(action)
            }
            is FavouritesScreenEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@FavouritesFragment::isErrorDialogVisible
            )
            is FavouritesScreenEvents.ShowToast -> requireContext().showToast(it.message)
        }
    }

    private fun initListeners() = binding.apply {
    }

    private fun initViews() = binding.apply {
        header.setMarginTop(TITLE_TOP_MARGIN)
        quizAdapter = QuizAdapter(this@FavouritesFragment::showRemoveFavouriteDialog) { _, quiz ->
            viewModel.onQuizPressed(quiz)
        }
        quizRv.apply {
            setHasFixedSize(false)
            adapter = quizAdapter
        }
    }

    private fun showRemoveFavouriteDialog(quiz: QuizDto) {
        requireContext().showAlertDialog(
            title = "Remove from Favourites",
            description = "Are you sure that you want to remove this quiz from favourites",
            positiveButtonText = "Remove"
        ) {
            viewModel.onQuizLongPressed(quiz)
        }
    }
}