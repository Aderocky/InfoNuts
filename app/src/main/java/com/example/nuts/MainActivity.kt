package com.example.nuts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nuts.data.di.DatabaseSupabaseClient
import com.example.nuts.data.repository.AuthRepository
import com.example.nuts.navigations.NavHostNuts
import com.example.nuts.screens.authentication.ViewModelFactory
import com.example.nuts.ui.theme.NutsTheme

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(
                    this,
                    "Kamera dibutuhkan untuk fitur ini. Aktifkan izin di pengaturan.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NutsTheme {
                val context = this
                val supabaseClient = DatabaseSupabaseClient.client()

                val authRepository = AuthRepository(
                    sbClient = supabaseClient,
                    context = context
                )

                val viewModelRealtime: RealtimeViewModel = viewModel(
                    factory = ViewModelFactory(authRepository)
                )

                LaunchedEffect(Unit) {
                    viewModelRealtime.realtimeDb(this)
                }
                NavHostNuts()
            }
        }
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {}
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}

