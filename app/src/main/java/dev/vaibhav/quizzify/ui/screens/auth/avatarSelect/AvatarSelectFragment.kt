package dev.vaibhav.quizzify.ui.screens.auth.avatarSelect

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentAvatarSelectBinding
import dev.vaibhav.quizzify.ui.adapters.AvatarAdapter
import dev.vaibhav.quizzify.ui.screens.main.MainActivity
import dev.vaibhav.quizzify.util.*
import dev.vaibhav.quizzify.util.Constants.THREE_ITEM_PERCENTAGE
import dev.vaibhav.quizzify.util.Constants.TITLE_TOP_MARGIN

@AndroidEntryPoint
class AvatarSelectFragment : Fragment(R.layout.fragment_avatar_select) {

    private val binding by viewBinding(FragmentAvatarSelectBinding::bind)
    private val viewModel by viewModels<AvatarSelectViewModel>()
    private lateinit var avatarAdapter: AvatarAdapter
    private var isErrorDialogVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        collectUiState()
        collectUiEvents()
    }

    private fun collectUiEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            AvatarSelectScreenEvents.NavigateToHomeScreen -> navigateToHomeScreen()
            is AvatarSelectScreenEvents.ShowToast -> requireContext().showToast(it.message)
            is AvatarSelectScreenEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@AvatarSelectFragment::isErrorDialogVisible
            )
        }
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        avatarAdapter.submitList(it.avatars)
        binding.continueBtn.isEnabled = it.isButtonEnabled
        binding.progressContainer.isVisible = it.isLoading
    }

    private fun initListeners() = binding.apply {
        continueBtn.setOnClickListener { viewModel.onContinueButtonPressed() }
    }

    private fun initViews() = binding.apply {
        avatarAdapter = AvatarAdapter(viewModel::onAvatarSelected)
        avatarRv.apply {
            setHasFixedSize(false)
            layoutManager = PercentageGridLayoutManager(requireContext(), 3, THREE_ITEM_PERCENTAGE)
            adapter = avatarAdapter
            isNestedScrollingEnabled = false
        }
        header.setMarginTop(TITLE_TOP_MARGIN)
    }

    private fun navigateToHomeScreen() {
        Intent(requireContext(), MainActivity::class.java).also {
            startActivity(it)
            requireActivity().finish()
        }
    }
}