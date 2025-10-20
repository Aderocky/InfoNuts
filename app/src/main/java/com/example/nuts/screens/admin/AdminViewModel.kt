package com.example.nuts.screens.admin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.nuts.data.entity.UserEntity
import com.example.nuts.data.repository.AuthRepository

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
}