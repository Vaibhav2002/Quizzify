package dev.vaibhav.quizzify.ui.screens.onBoarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vaibhav.quizzify.data.local.dataStore.LocalDataStore
import dev.vaibhav.quizzify.util.Constants
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(private val localDataStore: LocalDataStore) :
    ViewModel() {

    val onBoardingList = Constants.onBoardingList

    private val _uiState = MutableStateFlow(OnBoardingScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<OnBoardingScreenEvents>()
    val events = _events.asSharedFlow()

    private val page = MutableStateFlow(0)

    init {
        collectPage()
    }

    private fun collectPage() = viewModelScope.launch {
        page.collect {
            val onBoarding = onBoardingList[it]
            _uiState.emit(
                OnBoardingScreenState(
                    title = onBoarding.title,
                    subtitle = onBoarding.subtitle,
                    isSkipButtonVisible = it != onBoardingList.size - 1
                )
            )
        }
    }

    fun onNextButtonPressed() = viewModelScope.launch {
        val newPage = page.value + 1
        if (newPage == Constants.onBoardingList.size) {
            saveOnBoardingComplete()
            _events.emit(OnBoardingScreenEvents.NavigateToLoginScreen)
        } else {
            _events.emit(OnBoardingScreenEvents.ShowNextPage(newPage))
            page.emit(newPage)
        }
    }

    fun onPageChanged(pageNo: Int) = viewModelScope.launch {
        page.emit(pageNo)
    }

    fun onSKipButtonPressed() = viewModelScope.launch {
        saveOnBoardingComplete()
        _events.emit(OnBoardingScreenEvents.NavigateToLoginScreen)
    }

    private suspend fun saveOnBoardingComplete() {
        localDataStore.setOnBoardingComplete()
    }
}
