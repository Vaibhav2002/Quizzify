package dev.vaibhav.quizzify.ui.screens.auth.register

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.FragmentRegisterBinding
import dev.vaibhav.quizzify.ui.screens.main.MainActivity
import dev.vaibhav.quizzify.util.launchAndCollectLatest
import dev.vaibhav.quizzify.util.showErrorDialog
import dev.vaibhav.quizzify.util.showToast
import dev.vaibhav.quizzify.util.viewBinding

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val binding by viewBinding(FragmentRegisterBinding::bind)
    private val viewModel: RegisterViewModel by viewModels()

    private lateinit var gso: GoogleSignInOptions
    private lateinit var googleLoginLauncher: ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient
    private var isErrorDialogVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListeners()
        collectUiState()
        collectUiEvents()
        initializeSocialMediaSignIn()
        googleLoginLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK)
                    handleGoogleLogin(it.data)
            }
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(viewLifecycleOwner) {
        binding.apply {
            progressContainer.isVisible = it.isLoading
            signInBtn.isEnabled = it.isRegisterButtonEnabled
            emailTIL.error = it.emailError
            passwordTIL.error = it.passwordError
            emailTIET.isEnabled = it.areTextFieldsEnabled
            passwordTIET.isEnabled = it.areTextFieldsEnabled
            usernameTIET.isEnabled = it.areTextFieldsEnabled
        }
    }

    private fun collectUiEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            RegisterScreenEvents.NavigateToAvatarSelectScreen -> findNavController().navigate(R.id.action_registerFragment_to_avatarSelectFragment)
            is RegisterScreenEvents.ShowToast -> requireContext().showToast(it.message)
            RegisterScreenEvents.NavigateToLoginScreen -> findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            RegisterScreenEvents.NavigateToHomeScreen -> {
                Intent(requireContext(), MainActivity::class.java).also { intent ->
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
            is RegisterScreenEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@RegisterFragment::isErrorDialogVisible
            )
        }
    }

    private fun initViews() = Unit

    private fun initListeners() = binding.apply {
        emailTIET.doAfterTextChanged { viewModel.onEmailTextChange(it.toString()) }
        passwordTIET.doAfterTextChanged { viewModel.onPasswordTextChange(it.toString()) }
        usernameTIET.doAfterTextChanged { viewModel.onUsernameChange(it.toString()) }
        signInBtn.setOnClickListener { viewModel.onRegisterButtonPressed() }
        googleButton.setOnClickListener { googleLoginLauncher.launch(googleSignInClient.signInIntent) }
        goToLogin.setOnClickListener { viewModel.onGoToLoginPress() }
    }

    private fun initializeSocialMediaSignIn() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun handleGoogleLogin(data: Intent?) {
        data?.let { viewModel.onGoogleRegisterPressed(it) }
    }
}
