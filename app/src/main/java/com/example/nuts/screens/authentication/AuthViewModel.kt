package com.example.nuts.screens.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.nuts.data.repository.AuthRepository
import com.example.nuts.state.AuthState
import androidx.lifecycle.asLiveData

class AuthViewModel (
    private val authRepository: AuthRepository
): ViewModel() {

    val authState: LiveData<AuthState> = authRepository.authState.asLiveData()
    val userState = authRepository.userState.asLiveData()

    fun register(
        email : String,
        name : String,
        password: String,
        confirmPassword: String,
    ){
        authRepository.register(email,name,password,confirmPassword)
    }
    fun resetState() {
        authRepository.resetState()
    }
    fun login (
        email: String,
        password: String
    ){
        authRepository.login(email,password)
    }
}