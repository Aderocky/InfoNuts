package com.example.nuts.screens.authentication

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nuts.data.di.SupabaseClient.client
import com.example.nuts.data.model.UserState
import com.example.nuts.data.pref.SharePreferencesUser
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.launch
import io.github.jan.supabase.gotrue.providers.builtin.Email


class authViewModel: ViewModel() {
    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    fun register(
        context: Context,
        email : String,
        username : String,
        password: String,
        confirmPassword: String,
    ){
        viewModelScope.launch {
            try {
                client.auth.signUpWith(Email){
                    this.email = email
                    this.password = password
                }
                //updateProfile()
                saveToken(context)
                _userState.value = UserState.IsSucess("Register successfully!")
            }catch (e: Exception){
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun login (
        context: Context,
        userEmail: String,
        userPassword: String
    ){
        viewModelScope.launch {
            try{
                client.auth.signInWith(Email){
                    email = userEmail
                    password = userPassword
                }
                saveToken(context)
                _userState.value = UserState.IsSucess("Logged in successfully!")
            }catch (e: Exception){
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    private fun saveToken(context: Context){
        viewModelScope.launch {
            val accessToken = client.auth.currentAccessTokenOrNull()
            val sharedPref = SharePreferencesUser(context)
            sharedPref.saveStringData("accessToken", accessToken)
        }
    }

    private fun getToken(context: Context): String? {
        val sharedPref = SharePreferencesUser(context)
        return sharedPref.getStringData("accessToken")
    }

}