package dev.vaibhav.quizzify.ui.screens.auth.login

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
import dev.vaibhav.quizzify.databinding.FragmentLoginBinding
import dev.vaibhav.quizzify.util.*

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)
    private val viewModel: LoginViewModel by viewModels()

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
            signInBtn.isEnabled = it.isLoginButtonEnabled
            emailTIL.error = it.emailError
            passwordTIL.error = it.passwordError
        }
    }

    private fun collectUiEvents() = viewModel.events.launchAndCollectLatest(viewLifecycleOwner) {
        when (it) {
            LoginScreenEvents.NavigateToMainScreen -> requireActivity().navigateToMainActivity()
            is LoginScreenEvents.ShowToast -> requireContext().showToast(it.message)
            LoginScreenEvents.NavigateToRegisterScreen -> findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            is LoginScreenEvents.ShowErrorDialog -> showErrorDialog(
                it.errorType,
                this@LoginFragment::isErrorDialogVisible
            )
        }
    }

    private fun initViews() = Unit

    private fun initListeners() = binding.apply {
        emailTIET.doAfterTextChanged { viewModel.onEmailTextChange(it.toString()) }
        passwordTIET.doAfterTextChanged { viewModel.onPasswordTextChange(it.toString()) }
        signInBtn.setOnClickListener { viewModel.onLoginButtonPressed() }
        googleButton.setOnClickListener { googleLoginLauncher.launch(googleSignInClient.signInIntent) }
        goToRegister.setOnClickListener { viewModel.onGoToRegisterPress() }
    }

    private fun initializeSocialMediaSignIn() {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun handleGoogleLogin(data: Intent?) {
        data?.let { viewModel.onGoogleLoginPressed(it) }
    }
}
