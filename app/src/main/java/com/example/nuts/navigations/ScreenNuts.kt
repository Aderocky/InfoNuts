package com.example.nuts.navigations

import android.graphics.Bitmap
import android.net.Uri

sealed class ScreenNuts(val route: String) {
    object Login : ScreenNuts("login")
    object Register : ScreenNuts("register")
    object Home : ScreenNuts("home")
    object Klasifikasi : ScreenNuts("klasifikasi")
    object History : ScreenNuts("history?folderName={folderName}") {
        fun createRoute(folderName: String? = null): String {
            return if (folderName != null) {
                "history?folderName=$folderName"
            } else {
                "history"
            }
        }
    }
    object Hasil : ScreenNuts("hasil/{name}/{confidence}/{timeCost}/{fileName}") {
        fun createRoute(name: String, confidence: Float = 0f, timeCost: Long = 0L, fileName: String = "") =
            "hasil/$name/$confidence/$timeCost/$fileName"
    }
}