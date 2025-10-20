package com.example.nuts.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.nuts.data.repository.AuthRepository

class HistoryViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    suspend fun getCurrentEmail(): String?{
        return authRepository.getCurrentUserEmail()
    }
}