package com.example.nuts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuts.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RealtimeViewModel (
    private val authRepository: AuthRepository
) : ViewModel() {
    fun realtimeDb(scope: CoroutineScope) {
        viewModelScope.launch {
            authRepository.realtimeDb(viewModelScope)
        }
    }
}