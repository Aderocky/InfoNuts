package com.example.nuts.screens.hasil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.nuts.data.repository.AuthRepository

class HasilViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    val userState = authRepository.userState.asLiveData()

}