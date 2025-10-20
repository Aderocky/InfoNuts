package com.example.nuts.navigations

import android.net.Uri
sealed class ScreenNuts(val route: String) {
    object Login : ScreenNuts("login")
    object Register : ScreenNuts("register")
    object SplashScreen : ScreenNuts("splash")
    object Home : ScreenNuts("home")
    object Admin : ScreenNuts("admin")
    object Klasifikasi : ScreenNuts("klasifikasi")
    object History : ScreenNuts("history?email={email}&folderName={folderName}") {
        fun createRoute(email: String, folderName: String? = null): String {
            return if (folderName != null) {
                "history?email=$email&folderName=$folderName"
            } else {
                "history?email=$email"
            }
        }
    }
    object Hasil : ScreenNuts("hasil/{name}/{confidence}/{timeCost}/{fileName}") {
        fun createRoute(
            name: String,
            confidence: Float = 0f,
            timeCost: Long = 0L,
            fileName: String = ""
        ): String {
            val encodedName = Uri.encode(name)
            val encodedFile = Uri.encode(fileName)
            return "hasil/$encodedName/$confidence/$timeCost/$encodedFile"
        }
    }
}