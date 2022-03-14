package dev.vaibhav.quizzify.ui.screens.auth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.ActivityAuthBinding
import dev.vaibhav.quizzify.util.makeStatusBarTransparent
import dev.vaibhav.quizzify.util.viewBinding

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityAuthBinding::inflate)
    private val viewModel by viewModels<AuthViewModel>()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navController = findNavController(R.id.auth_fragment_container)
        makeStatusBarTransparent()
    }

//    override fun onStart() {
//        super.onStart()
//        if (viewModel.isUserSignedIn) {
//            navigateToMainActivity(true)
//        }
//    }

//    fun showErrorDialog(errorTYpe: ErrorTYpe) {
//        ErrorDialogFragment(errorTYpe).show(supportFragmentManager, SHOW_ERROR_DIALOG)
//    }
}
