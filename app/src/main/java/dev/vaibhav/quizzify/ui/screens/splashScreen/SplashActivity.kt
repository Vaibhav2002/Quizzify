package dev.vaibhav.quizzify.ui.screens.splashScreen

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.ActivitySplashBinding
import dev.vaibhav.quizzify.ui.screens.auth.AuthActivity
import dev.vaibhav.quizzify.ui.screens.main.MainActivity
import dev.vaibhav.quizzify.ui.screens.onBoarding.OnBoardingActivity
import dev.vaibhav.quizzify.util.makeStatusBarTransparent
import dev.vaibhav.quizzify.util.viewBinding

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModels<SplashViewModel>()
    private val binding by viewBinding(ActivitySplashBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
//        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        collectUiEvents()
//        splashScreen.setKeepOnScreenCondition { true }
        setContentView(binding.root)
        makeStatusBarTransparent()
        animateLogo()
    }

    private fun animateLogo() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.scale_from_center)
        binding.logo.apply {
            startAnimation(animation)
        }
    }

    private fun collectUiEvents() = lifecycleScope.launchWhenStarted {
        viewModel.events.collect {
            when (it) {
                SplashScreenEvents.NavigateToHomeScreen -> navigate(MainActivity::class.java)
                SplashScreenEvents.NavigateToLoginScreen -> navigate(AuthActivity::class.java)
                SplashScreenEvents.NavigateToOnBoarding -> navigate(OnBoardingActivity::class.java)
            }
        }
    }

    private fun navigate(destination: Class<*>) {
        Intent(this, destination).also {
            startActivity(it)
            finish()
        }
    }
}
