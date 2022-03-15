package dev.vaibhav.quizzify.ui.screens.main.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentHomeBinding
import dev.vaibhav.quizzify.ui.adapters.CategoryAdapter
import dev.vaibhav.quizzify.ui.adapters.QuizAdapter
import dev.vaibhav.quizzify.ui.screens.gameScreens.inviteCode.InviteCodeDialogFragment
import dev.vaibhav.quizzify.ui.screens.quizScreens.questionCount.QuestionCountFragment
import dev.vaibhav.quizzify.util.*
import dev.vaibhav.quizzify.util.Constants.INVITE_CODE_DIALOG
import dev.vaibhav.quizzify.util.Constants.QUESTION_COUNT_DIALOG
import dev.vaibhav.quizzify.util.Constants.TITLE_TOP_MARGIN

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel by viewModels<HomeViewModel>()
    private var isErrorDialogVisible = false

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var quizAdapter: QuizAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setUpForContainerTransform()
        initViews()
        initListeners()
        collectUiState()
        collectEvents()
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        binding.apply {
            quizAdapter.submitList(it.quizzes)
            categoryAdapter.submitList(it.categories)
            progressContainer.isVisible = it.isLoading
            swipeRefresh.isRefreshing = it.isRefreshing
            popularQuizRv.scrollToPosition(0)
        }
    }

    private fun collectEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            is HomeScreenEvents.NavigateToQuizDetails -> {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToQuizDetailsFragment(it.quiz)
                findNavController().navigate(action)
            }
            is HomeScreenEvents.NavigateToWaitingForPlayersScreen -> {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToWaitingForPlayersFragment(it.gameId)
                findNavController().navigate(action)
            }
            HomeScreenEvents.OpenInviteCodeDialog -> {
                InviteCodeDialogFragment(viewModel::onInviteCodeSuccess).show(
                    childFragmentManager,
                    INVITE_CODE_DIALOG
                )
            }
            is HomeScreenEvents.OpenQuestionCountDialog -> {
                QuestionCountFragment(it.onCountSelected).show(
                    childFragmentManager,
                    QUESTION_COUNT_DIALOG
                )
            }
            is HomeScreenEvents.ShowToast -> requireContext().showToast(it.message)
            is HomeScreenEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@HomeFragment::isErrorDialogVisible
            )
        }
    }

    private fun initListeners() = binding.apply {
        joinGameBtn.setOnClickListener { viewModel.onJoinGameButtonPressed() }
        swipeRefresh.setOnRefreshListener { viewModel.onRefreshed() }
    }

    private fun initViews() = binding.apply {
        header.setMarginTop(TITLE_TOP_MARGIN)
        categoryAdapter = CategoryAdapter(viewModel::onCategoryPressed)
        quizAdapter = QuizAdapter { quizItem, quiz ->
            viewModel.onQuizItemPressed(quiz)
//            val extras = FragmentNavigatorExtras(quizItem.root to quiz.id)
//            val action =
//                HomeFragmentDirections.actionHomeFragmentToQuizDetailsFragment(quiz)
//            findNavController().navigate(action, extras)
        }
        categoriesRv.apply {
            setHasFixedSize(true)
            adapter = categoryAdapter
        }
        popularQuizRv.apply {
            setHasFixedSize(false)
            adapter = quizAdapter
        }
    }
}