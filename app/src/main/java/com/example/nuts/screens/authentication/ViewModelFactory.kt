package com.example.nuts.screens.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nuts.RealtimeViewModel
import com.example.nuts.clasifications.Classifier
import com.example.nuts.data.repository.AuthRepository
import com.example.nuts.screens.NutsViewModel
import com.example.nuts.screens.admin.AdminViewModel
import com.example.nuts.screens.hasil.HasilViewModel
import com.example.nuts.screens.history.HistoryViewModel
import com.example.nuts.screens.home.HomeScreenViewModel
import com.example.nuts.screens.klasifikasi.KlasifikasiViewModel
import com.example.nuts.screens.splash.SplashScreenViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val classifier: Classifier? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(HomeScreenViewModel::class.java) -> {
                HomeScreenViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(HasilViewModel::class.java) -> {
                HasilViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                AdminViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(SplashScreenViewModel::class.java) -> {
                SplashScreenViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(RealtimeViewModel::class.java) -> {
                RealtimeViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(KlasifikasiViewModel::class.java) -> {
                if (classifier == null) {
                    throw IllegalArgumentException("Classifier is required for KlasifikasiViewModel")
                }
                KlasifikasiViewModel(classifier, authRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

