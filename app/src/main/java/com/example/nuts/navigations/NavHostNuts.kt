package com.example.nuts.navigations

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nuts.screens.authentication.login.LoginScreen
import com.example.nuts.screens.authentication.register.RegisterScreen
import com.example.nuts.screens.hasil.Hasil
import com.example.nuts.screens.history.History
import com.example.nuts.screens.home.Home
import com.example.nuts.screens.klasifikasi.Klasifikasi


@Composable
fun NavHostNuts(){
    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxWidth(),
        navController = navController,
        startDestination = ScreenNuts.Home.route
    ) {
        composable(route = ScreenNuts.Login.route){
            LoginScreen {  }
        }
        composable(route = ScreenNuts.Register.route){
            RegisterScreen {  }
        }
        composable(route = ScreenNuts.Home.route){
            Home(navController)
        }
        composable(route = ScreenNuts.Klasifikasi.route){
            Klasifikasi(navController)
        }
        composable(
            route = "history?folderName={folderName}",
            arguments = listOf(
                navArgument("folderName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val folderName = backStackEntry.arguments?.getString("folderName")
            History(
                navController = navController,
                folderName = folderName
            )
        }

        composable(
            route = ScreenNuts.Hasil.route,
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("confidence") { type = NavType.FloatType },
                navArgument("timeCost") { type = NavType.LongType },
            ),
        ) {
            val name = it.arguments?.getString("name") ?: ""
            val confidence = it.arguments?.getFloat("confidence") ?: 0F
            val timeCost = it.arguments?.getLong("timeCost") ?: 0L
            Hasil(
                name = name,
                confidence = confidence,
                timeCost = timeCost,
                navController = navController,
            )
        }
    }
}