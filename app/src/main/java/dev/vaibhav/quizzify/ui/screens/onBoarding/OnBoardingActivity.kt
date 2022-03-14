package dev.vaibhav.quizzify.ui.screens.onBoarding

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import dev.vaibhav.quizzify.R
import dev.vaibhav.quizzify.databinding.ActivityOnBoardingBinding
import dev.vaibhav.quizzify.ui.adapters.OnboardingAdapter
import dev.vaibhav.quizzify.ui.screens.auth.AuthActivity
import dev.vaibhav.quizzify.util.launchAndCollect
import dev.vaibhav.quizzify.util.launchAndCollectLatest
import dev.vaibhav.quizzify.util.makeStatusBarTransparent
import dev.vaibhav.quizzify.util.viewBinding

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityOnBoardingBinding::inflate)
    private val viewModel by viewModels<OnBoardingViewModel>()
    private lateinit var onBoardingAdapter: OnboardingAdapter
    private lateinit var animation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        makeStatusBarTransparent()
        onBoardingAdapter = OnboardingAdapter(viewModel.onBoardingList)
        animation = AnimationUtils.loadAnimation(this, R.anim.scale_from_center)
        initViews()
        initListeners()
        collectUiState()
        collectUiEvents()
    }

    private fun initListeners() = binding.apply {
        skipButton.setOnClickListener { viewModel.onSKipButtonPressed() }
        nextButton.setOnClickListener { viewModel.onNextButtonPressed() }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.onPageChanged(position)
            }
        })
    }

    private fun initViews() = binding.apply {
        viewPager.apply {
            isUserInputEnabled = true
            adapter = onBoardingAdapter
        }
        dotsIndicator.dotsClickable = true
        dotsIndicator.setViewPager2(binding.viewPager)
    }

    private fun collectUiState() = viewModel.uiState.launchAndCollectLatest(this) {
        binding.apply {
            title.text = it.title
            title.startAnimation(animation)
            description.text = it.subtitle
            description.startAnimation(animation)
            skipButton.isVisible = it.isSkipButtonVisible
        }
    }

    private fun collectUiEvents() = viewModel.events.launchAndCollect(this) {
        when (it) {
            OnBoardingScreenEvents.NavigateToLoginScreen -> navigateToLoginScreen()
            is OnBoardingScreenEvents.ShowNextPage -> showNextPage(it.pageNo)
        }
    }

    private fun showNextPage(pageNo: Int) {
        binding.viewPager.setCurrentItem(pageNo, true)
    }

    private fun navigateToLoginScreen() {
        Intent(this, AuthActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}
