package com.example.nuts.screens.home

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.navigation.NavHostController
import com.example.nuts.components.BottomNavBar
import com.example.nuts.components.CurvedTopBarActions
import com.example.nuts.components.LogoutConfirmDialog
import com.example.nuts.components.PremiumDialog
import com.example.nuts.navigations.ScreenNuts
import com.example.nuts.screens.NutsViewModel
import com.example.nuts.ui.theme.CreamMain
import com.example.nuts.ui.theme.beige
import com.example.nuts.ui.theme.brown
import com.example.nuts.ui.theme.krem
import com.example.nuts.utils.drawableImageNuts
import com.example.nuts.utils.latinNameNuts

@Composable
fun Home(
    navController: NavHostController,
){
    var isPremium by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPremiumDialog by remember { mutableStateOf(false) }
    val viewModel: NutsViewModel = viewModel(LocalContext.current as ComponentActivity)
    val context = LocalContext.current
    val nutsData = viewModel.nutsData

    if (showPremiumDialog) {
        PremiumDialog(
            onOpenWhatsApp = {
                showPremiumDialog = false
                val phoneNumber = "+6282176095404"
                val url = "https://wa.me/$phoneNumber"
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                }
                context.startActivity(intent)
            },
            onDismiss = {
                showPremiumDialog = false
            }
        )
    }

    if (showLogoutDialog) {
        LogoutConfirmDialog(
            onConfirm = {
                showLogoutDialog = false
                // Tambahkan logika logout di sini
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }


    HomeScreen(
        navController = navController,
        onLogoutClick = { showLogoutDialog = true },
        onPremiumClick = { showPremiumDialog = true },
        isPremium = isPremium,
        nutsData = nutsData
    )
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    onLogoutClick: () -> Unit,
    onPremiumClick: () -> Unit,
    isPremium: Boolean,
    nutsData: List<String>
    ) {
    Scaffold(
        topBar = {
            CurvedTopBarActions(
                title = "Welcome, User!",
                actions = {
                        if (!isPremium) {
                            Icon(
                                imageVector = Icons.Outlined.Star,
                                contentDescription = "Premium Icon",
                                modifier = Modifier
                                    .size(25.dp)
                                    .clickable(onClick = onPremiumClick),
                                tint = CreamMain
                            )
                        }
                        Spacer(Modifier.width(12.dp))
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
        bottomBar = { BottomNavBar(navController) },
        containerColor = krem
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .padding(horizontal = 7.dp) ,
            contentPadding = PaddingValues(bottom = 20.dp)
        ){
            item {
                Column {
                    Text(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 27.dp, bottom = 20.dp),
                        text = "Collections Nuts",
                        fontWeight = FontWeight.Bold,
                        color = brown,
                        fontSize = 36.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(
                nutsData,
                key = { it }
            ){ item->
                Card(
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(3.dp, Color.Black),
                    colors = CardDefaults.cardColors(containerColor = beige),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 4.dp)
                        .clickable {
                            navController.navigate(ScreenNuts.History.createRoute(folderName = item))
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val drawableId = drawableImageNuts[item]

                        if (drawableId != null) {
                            Image(
                                painter = painterResource(id = drawableId),
                                contentDescription = item,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
                            )
                        }

                        Spacer(modifier = Modifier.width(13.dp))

                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = item,
                                fontSize = 26.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = latinNameNuts[item].toString(),
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Light,
                                fontStyle = FontStyle.Italic,
                                color = CreamMain
                            )
                        }
                    }
                }
            }

        }

    }
}



