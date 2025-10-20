package com.example.nuts.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.nuts.R
import com.example.nuts.data.di.DatabaseSupabaseClient
import com.example.nuts.data.pref.SharePrefModel
import com.example.nuts.data.repository.AuthRepository
import com.example.nuts.navigations.ScreenNuts
import com.example.nuts.screens.authentication.ViewModelFactory
import com.example.nuts.state.AuthState
import com.example.nuts.state.ResultState
import com.example.nuts.ui.theme.beige
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
) {
    val context = LocalContext.current
    val authRepository = remember {
        AuthRepository(
            sbClient = DatabaseSupabaseClient.client(),
            context = context
        )
    }
    val viewModelSplashScreen: SplashScreenViewModel = viewModel(
        factory = ViewModelFactory(authRepository)
    )

    val authState = viewModelSplashScreen.authState.observeAsState()
    val userState = viewModelSplashScreen.userState.observeAsState()

    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModelSplashScreen.refreshPremiumEveryOpen()
    }

    LaunchedEffect(authState.value, userState.value) {
        if (hasNavigated) return@LaunchedEffect

        delay(2500L)

        when (authState.value) {
            AuthState.Authenticated -> {
                if (userState.value is ResultState.Success) {
                    if((userState.value as ResultState.Success<SharePrefModel>).data.isAdmin){
                        navController.navigate(ScreenNuts.Admin.route) {
                            popUpTo(ScreenNuts.SplashScreen.route) { inclusive = true }
                        }
                    }else {
                        navController.navigate(ScreenNuts.Home.route) {
                            popUpTo(ScreenNuts.SplashScreen.route) { inclusive = true }
                        }
                    }
                    hasNavigated = true
                }
            }
            AuthState.Unauthenticated -> {
                navController.navigate(ScreenNuts.Login.route) {
                    popUpTo(ScreenNuts.SplashScreen.route) { inclusive = true }
                }
                hasNavigated = true
            }
            else -> {
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = beige),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .aspectRatio(1f)
                .padding(horizontal = 60.dp),
            painter = painterResource(R.drawable.logo),
            contentDescription = "Splash Screen"
        )
    }
}

