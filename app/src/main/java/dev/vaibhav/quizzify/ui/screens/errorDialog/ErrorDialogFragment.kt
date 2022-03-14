package dev.vaibhav.quizzify.ui.screens.errorDialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentErrorDialogBinding
import dev.vaibhav.quizzify.util.ErrorType

class ErrorDialogFragment(
    private val errorType: ErrorType,
    private val onDismiss: () -> Unit
) : DialogFragment(R.layout.fragment_error_dialog) {

    private lateinit var binding: FragmentErrorDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErrorDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
    }

    private fun initViews() = binding.apply {
        animation.setAnimation(errorType.lottieAnim)
        animation.animate()
        titleText.text = errorType.title
        descriptionText.text = errorType.errorMessage
    }

    private fun initListeners() = binding.apply {
        closeBtn.setOnClickListener { dismiss() }
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismiss()
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) {
        onDismiss()
        super.onCancel(dialog)
    }
}