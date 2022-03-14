package dev.vaibhav.quizzify.ui.screens.quizScreens.questionCount

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentQuestionCountBinding
import dev.vaibhav.quizzify.util.Constants.questionCount
import dev.vaibhav.quizzify.util.viewBinding

@AndroidEntryPoint
class QuestionCountFragment(
    private val onCountSelected: (Int) -> Unit
) : DialogFragment(R.layout.fragment_question_count) {

    private val binding by viewBinding(FragmentQuestionCountBinding::bind)
    private lateinit var countAdapter: ArrayAdapter<String>

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
        initViews()
        initListeners()
    }

    private fun initListeners() = binding.apply {
        continueBtn.setOnClickListener {
            countSpinner.text.toString().toIntOrNull()?.let {
                onCountSelected(it)
                dismiss()
            }
        }
    }

    private fun initViews() = binding.apply {
        countAdapter = ArrayAdapter(
            requireContext(),
            R.layout.custom_spinner_item_small,
            questionCount.map { it.toString() }
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        countSpinner.setAdapter(countAdapter)
    }
}