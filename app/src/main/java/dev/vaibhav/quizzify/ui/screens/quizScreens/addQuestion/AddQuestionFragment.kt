package dev.vaibhav.quizzify.ui.screens.quizScreens.addQuestion

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.data.models.remote.QuestionDto
import dev.vaibhav.quizzify.databinding.FragmentAddQuestionBinding
import dev.vaibhav.quizzify.util.launchAndCollectLatest
import dev.vaibhav.quizzify.util.viewBinding

@AndroidEntryPoint
class AddQuestionFragment(
    private val onQuestionAdded: (QuestionDto) -> Unit
) : DialogFragment(R.layout.fragment_add_question) {

    private val viewModel by viewModels<AddQuestionViewModel>()
    private val binding by viewBinding(FragmentAddQuestionBinding::bind)

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        collectUiState()
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        binding.saveBtn.isEnabled = it.isAddButtonEnabled
    }

    private fun initListeners() = binding.apply {
        saveBtn.setOnClickListener {
            val question = viewModel.getQuestions()
            onQuestionAdded(question)
            dismiss()
        }
        questionTIET.doAfterTextChanged { viewModel.onQuestionChanged(it.toString().trim()) }
        option1TIET.doAfterTextChanged { viewModel.onOptionChanged(0, it.toString().trim()) }
        option2TIET.doAfterTextChanged { viewModel.onOptionChanged(1, it.toString().trim()) }
        option3TIET.doAfterTextChanged { viewModel.onOptionChanged(2, it.toString().trim()) }
        option4TIET.doAfterTextChanged { viewModel.onOptionChanged(3, it.toString().trim()) }
        answerTIET.doAfterTextChanged { viewModel.onAnswerChanged(it.toString().trim()) }
    }
}