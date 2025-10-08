package com.example.nuts.data.model

sealed class UserState {
    object Loading: UserState()
    data class IsSucess (val message: String): UserState()
    data class Error (val message: String): UserState()
}