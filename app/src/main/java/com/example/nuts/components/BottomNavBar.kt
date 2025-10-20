package com.example.nuts.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nuts.navigations.ScreenNuts
import com.example.nuts.ui.theme.BrownGold
import com.example.nuts.ui.theme.NutPrimaryDark
import com.example.nuts.ui.theme.beige
import com.example.nuts.ui.theme.brown
import com.example.nuts.ui.theme.semiWhite
@Composable
fun BottomNavBar(
    navController: NavController,
    email: String
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar(
        containerColor = NutPrimaryDark,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        val isHomeSelected = currentDestination?.route == ScreenNuts.Home.route
        NavigationBarItem(
            selected = isHomeSelected,
            onClick = {
                if (!isHomeSelected){
                    navController.navigate(ScreenNuts.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = ScreenNuts.Home.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White,
                indicatorColor = BrownGold
            )
        )

        val isKlasifikasiSelected = currentDestination?.route == ScreenNuts.Klasifikasi.route
        NavigationBarItem(
            selected = isKlasifikasiSelected,
            onClick = {
                if(!isKlasifikasiSelected) {
                    navController.navigate(ScreenNuts.Klasifikasi.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            },
            icon = { Icon(Icons.Default.Search, contentDescription = ScreenNuts.Klasifikasi.route) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White,
                indicatorColor = BrownGold
            )
        )

        val isHistorySelected = currentDestination?.route?.startsWith("history") == true
        NavigationBarItem(
            selected = isHistorySelected,
            onClick = {
                if (!isHistorySelected) {
                    navController.navigate(ScreenNuts.History.createRoute(email)) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            },
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White,
                indicatorColor = BrownGold
            )
        )
    }
}
