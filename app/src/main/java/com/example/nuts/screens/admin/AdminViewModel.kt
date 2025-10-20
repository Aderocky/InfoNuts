package com.example.nuts.screens.admin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.nuts.data.entity.UserEntity
import com.example.nuts.data.repository.AuthRepository
import com.example.nuts.state.ResultState

class AdminViewModel (
    private val authRepository: AuthRepository
) : ViewModel() {

    val userState = authRepository.userState.asLiveData()
    val users = mutableStateOf<List<UserEntity>>(emptyList())

    suspend fun logOut() {
        authRepository.logout()
    }
    suspend fun fetchUser (){
        users.value = authRepository.fetchUsers()
    }
    suspend fun updateUser(email: String, name: String, isPremium: Boolean, expDate: String) {
        val result = authRepository.updateUser(email, name, isPremium, expDate)

        if (result is ResultState.Success) {
            users.value = authRepository.fetchUsers()
        }
    }
}