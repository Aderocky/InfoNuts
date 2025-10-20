package com.example.nuts.state

sealed class AuthState {
    object Loading : AuthState()
    class Error(val message: String) : AuthState()
    object Unauthenticated: AuthState()
    object Authenticated: AuthState()
    class Success(val message: String) : AuthState()
}