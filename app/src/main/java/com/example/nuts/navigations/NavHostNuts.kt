package com.example.nuts.navigations

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nuts.screens.admin.Admin
import com.example.nuts.screens.authentication.login.Login
import com.example.nuts.screens.authentication.register.Register
import com.example.nuts.screens.hasil.Hasil
import com.example.nuts.screens.history.History
import com.example.nuts.screens.home.Home
import com.example.nuts.screens.klasifikasi.Klasifikasi
import com.example.nuts.screens.splash.SplashScreen
@Composable
fun NavHostNuts(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        modifier = Modifier.fillMaxWidth(),
        startDestination = ScreenNuts.SplashScreen.route,
    ) {
        composable(route = ScreenNuts.SplashScreen.route){
            SplashScreen(navController)
        }
        composable(route = ScreenNuts.Login.route){
            Login(navController)
        }
        composable(route = ScreenNuts.Register.route){
            Register(navController)
        }
        composable(route = ScreenNuts.Home.route){
            Home(navController)
        }
        composable(route = ScreenNuts.Admin.route){
            Admin(navController)
        }
        composable(route = ScreenNuts.Klasifikasi.route){
            Klasifikasi(navController)
        }
        composable(
            route = "history?email={email}&folderName={folderName}",
            arguments = listOf(
                navArgument("email"){
                    type = NavType.StringType
                },
                navArgument("folderName") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            val folderName = backStackEntry.arguments?.getString("folderName")
            if (email != null) {
                History(
                    navController = navController,
                    email = email,
                    folderName = folderName
                )
            }
        }
        composable(
            route = ScreenNuts.Hasil.route,
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("confidence") { type = NavType.FloatType },
                navArgument("timeCost") { type = NavType.LongType },
                navArgument("fileName") { type = NavType.StringType }
            ),
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val confidence = backStackEntry.arguments?.getFloat("confidence") ?: 0f
            val timeCost = backStackEntry.arguments?.getLong("timeCost") ?: 0L
            val fileName = backStackEntry.arguments?.getString("fileName")

            Hasil(
                navController = navController,
                name = name,
                confidence = confidence,
                timeCost = timeCost,
                fileName = fileName
            )
        }
    }
}