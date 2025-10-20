package com.example.nuts.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.nuts.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class HomeScreenViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    val userState = authRepository.userState.asLiveData()

    private val _imageCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val imageCounts: StateFlow<Map<String, Int>> = _imageCounts

    suspend fun logOut() {
        authRepository.logout()
    }

    suspend fun getCurrentEmail(): String?{
        return authRepository.getCurrentUserEmail()
    }

    fun loadImageCounts(context: Context, email: String, nutsData: List<String>) {
        val counts = nutsData.associateWith { folderName ->
            val folder = File(context.getExternalFilesDir(null), "$email/$folderName")
            if (!folder.exists()) 0 else folder.listFiles { file ->
                file.extension.lowercase() in listOf("jpg", "jpeg")
            }?.size ?: 0
        }
        _imageCounts.value = counts
    }

    fun updateImageCount(context: Context, email: String, folderName: String) {
        val folder = File(context.getExternalFilesDir(null), "$email/$folderName")
        val count = if (!folder.exists()) 0 else folder.listFiles { file ->
            file.extension.lowercase() in listOf("jpg", "jpeg")
        }?.size ?: 0

        _imageCounts.value = _imageCounts.value.toMutableMap().apply {
            this[folderName] = count
        }
    }
}