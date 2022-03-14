package dev.vaibhav.quizzify.ui.screens.onBoarding

sealed class OnBoardingScreenEvents {
    object NavigateToLoginScreen : OnBoardingScreenEvents()
    class ShowNextPage(val pageNo: Int) : OnBoardingScreenEvents()
}
