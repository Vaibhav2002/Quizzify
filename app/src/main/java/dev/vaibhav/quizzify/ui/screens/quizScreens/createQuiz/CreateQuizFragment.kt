package dev.vaibhav.quizzify.ui.screens.quizScreens.createQuiz

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentCreateQuizBinding
import dev.vaibhav.quizzify.ui.adapters.QuestionAdapter
import dev.vaibhav.quizzify.ui.screens.quizScreens.addQuestion.AddQuestionFragment
import dev.vaibhav.quizzify.util.*
import dev.vaibhav.quizzify.util.Constants.CREATE_QUESTION_DIALOG

@AndroidEntryPoint
class CreateQuizFragment : Fragment(R.layout.fragment_create_quiz) {

    private val binding by viewBinding(FragmentCreateQuizBinding::bind)
    private val viewModel by viewModels<CreateQuizViewModel>()
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private var isErrorDialogVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        collectUiState()
        collectEvents()
        collectCategories()
    }

    private fun collectCategories() =
        viewModel.categories.launchAndCollectLatest(viewLifecycleOwner) {
            categoryAdapter = ArrayAdapter(
                requireContext(),
                R.layout.custom_spinner_item_small,
                it.map { it.name }
            ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            binding.categorySpinner.setAdapter(categoryAdapter)
        }

    private fun collectEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            CreateQuizScreenEvents.NavigateBack -> findNavController().popBackStack()
            is CreateQuizScreenEvents.OpenAddQuestionDialog -> {
                AddQuestionFragment(it.onQuestionAdded).show(
                    childFragmentManager,
                    CREATE_QUESTION_DIALOG
                )
            }
            is CreateQuizScreenEvents.ShowToast -> requireContext().showToast(it.message)
            is CreateQuizScreenEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@CreateQuizFragment::isErrorDialogVisible
            )
        }
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        binding.apply {
            saveQuizBtn.isEnabled = it.isSaveButtonEnabled
            progressContainer.isVisible = it.isLoading
            questionAdapter.submitList(it.questions)
        }
    }

    private fun initListeners() = binding.apply {
        nameTIET.doAfterTextChanged { viewModel.onNameChanged(it.toString().trim()) }
        descriptionTIET.doAfterTextChanged { viewModel.onDescriptionChanged(it.toString().trim()) }
        categorySpinner.setOnItemClickListener { parent, _, position, _ ->
            viewModel.onCategoryChanged(parent.getItemAtPosition(position) as String)
        }
        addQuestionBtn.setOnClickListener { viewModel.onAddButtonPressed() }
        saveQuizBtn.setOnClickListener { viewModel.onSaveButtonPressed() }
    }

    private fun initViews() = binding.apply {
        header.setMarginTop(Constants.TITLE_TOP_MARGIN)
        questionAdapter = QuestionAdapter(viewModel::onRemoveQuestionPressed)
        questionRv.apply {
            setHasFixedSize(false)
            adapter = questionAdapter
        }
    }
}