package com.example.nuts.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.nuts.data.repository.AuthRepository
import kotlinx.coroutines.launch

class SplashScreenViewModel(
    private val authRepository: AuthRepository
): ViewModel() {
    val authState = authRepository.authState.asLiveData()
    val userState = authRepository.userState.asLiveData()

    suspend fun refreshPremiumEveryOpen(){
        authRepository.refreshPremiumEveryOpen()
    }
}