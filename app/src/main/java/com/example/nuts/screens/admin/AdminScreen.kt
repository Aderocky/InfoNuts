package com.example.nuts.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.nuts.components.CurvedTopBarActions
import com.example.nuts.components.LogoutConfirmDialog
import com.example.nuts.data.di.DatabaseSupabaseClient
import com.example.nuts.data.entity.UserEntity
import com.example.nuts.data.repository.AuthRepository
import com.example.nuts.navigations.ScreenNuts
import com.example.nuts.screens.authentication.ViewModelFactory
import com.example.nuts.screens.home.HomeScreenViewModel
import com.example.nuts.ui.theme.BrownCustom
import com.example.nuts.ui.theme.BrownGold
import com.example.nuts.ui.theme.CreamMain
import com.example.nuts.ui.theme.NutPrimaryLight
import com.example.nuts.ui.theme.NutTextPrimary
import com.example.nuts.utils.drawableImageNuts
import com.example.nuts.utils.latinNameNuts
import kotlinx.coroutines.launch

@Composable
fun Admin(
    navController: NavHostController
){
    val context = LocalContext.current

    val authRepository = remember {
        AuthRepository(
            sbClient = DatabaseSupabaseClient.client(),
            context = context
        )
    }
    val viewModelAdmin : AdminViewModel = viewModel (
        factory = ViewModelFactory(authRepository)
    )

    LaunchedEffect(Unit) {
        viewModelAdmin.fetchUser()
    }

    val user = viewModelAdmin.users
    var showLogoutDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (showLogoutDialog) {
        LogoutConfirmDialog(
            onConfirm = {
                coroutineScope.launch {
                    viewModelAdmin.logOut()
                    showLogoutDialog = false
                    navController.navigate(ScreenNuts.Login.route) {
                        popUpTo(0)
                    }
                }
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
    AdminScreen(
        navController = navController,
        onLogoutClick = {showLogoutDialog = true},
        user = user.value
    )
}

@Composable
fun AdminScreen(
    navController: NavHostController,
    onLogoutClick: () -> Unit,
    user: List<UserEntity>,
){
    Scaffold(
        topBar = {
            CurvedTopBarActions(
                title = "Welcome, Admin!",
                actions = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout Icon",
                        modifier = Modifier
                            .size(25.dp)
                            .clickable(onClick = onLogoutClick),
                        tint = CreamMain
                    )
                }
            )
        },
        containerColor = NutPrimaryLight
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .padding(horizontal = 7.dp) ,
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                Column {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 27.dp, bottom = 20.dp),
                        text = "Admin",
                        fontWeight = FontWeight.Bold,
                        color = NutTextPrimary,
                        fontSize = 36.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(user) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = BrownGold),
                    border = BorderStroke(3.dp, BrownCustom),
                    shape = RoundedCornerShape(15.dp),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.email,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NutTextPrimary
                            )

                            if (user.isPremium) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color(0xFF00C853),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Premium",
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "s.d ${user.expDate}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(0xFFD50000),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Non-Premium",
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = { /* TODO: Aksi edit */ },
                            modifier = Modifier
                                .background(Color(0xFFD50000), shape = CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit User",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}