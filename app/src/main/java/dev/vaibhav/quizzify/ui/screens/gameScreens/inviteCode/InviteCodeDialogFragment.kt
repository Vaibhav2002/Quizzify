package dev.vaibhav.quizzify.ui.screens.gameScreens.inviteCode

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.databinding.FragmentInviteCodeDialogBinding
import dev.vaibhav.quizzify.util.Constants.GAME_KEY_LENGTH
import dev.vaibhav.quizzify.util.launchAndCollectLatest
import dev.vaibhav.quizzify.util.showErrorDialog
import dev.vaibhav.quizzify.util.showToast

@AndroidEntryPoint
class InviteCodeDialogFragment(
    private val onInviteCodeSuccess: (String) -> Unit
) : DialogFragment() {

    private lateinit var binding: FragmentInviteCodeDialogBinding
    private val viewModel by viewModels<InviteCodeViewModel>()
    private var isErrorDialogVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInviteCodeDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

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
        collectUiState()
        collectUiEvents()
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        binding.apply {
            connectBtn.isEnabled = it.isButtonEnabled
            progressContainer.isVisible = it.isLoading
            inviteCodeTIL.error = it.inviteCodeError
        }
    }

    private fun collectUiEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            is InviteCodeScreenEvents.NavigateBack -> {
                onInviteCodeSuccess(it.gameId)
                dismiss()
            }
            is InviteCodeScreenEvents.ShowToast -> requireContext().showToast(it.message)
            is InviteCodeScreenEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@InviteCodeDialogFragment::isErrorDialogVisible
            )
        }
    }

    private fun initListeners() = binding.apply {
        inviteCodeTIET.doAfterTextChanged { viewModel.onInviteCodeChanged(it.toString().trim()) }
        connectBtn.setOnClickListener { viewModel.onConnectButtonPressed() }
    }

    private fun initViews() = binding.apply {
        inviteCodeTIET.filters = arrayOf(InputFilter.LengthFilter(GAME_KEY_LENGTH))
    }
}