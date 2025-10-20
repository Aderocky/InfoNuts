package com.example.nuts.screens.home

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
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
import androidx.core.net.toUri
import com.example.nuts.data.di.DatabaseSupabaseClient
import com.example.nuts.data.repository.AuthRepository
import com.example.nuts.screens.authentication.ViewModelFactory
import com.example.nuts.state.ResultState
import com.example.nuts.ui.theme.BrownCustom
import com.example.nuts.ui.theme.BrownGold
import com.example.nuts.ui.theme.NutPrimaryLight
import com.example.nuts.ui.theme.NutTextPrimary
import kotlinx.coroutines.launch

@Composable
fun Home(
    navController: NavHostController,
){
    val context = LocalContext.current

    val authRepository = remember {
        AuthRepository(
            sbClient = DatabaseSupabaseClient.client(),
            context = context
        )
    }
    val viewModelHome : HomeScreenViewModel = viewModel (
        factory = ViewModelFactory(authRepository)
    )

    val userState = viewModelHome.userState.observeAsState(ResultState.Loading)
    val email by produceState(initialValue = "") {
        value = viewModelHome.getCurrentEmail().orEmpty()
    }

    when (val state = userState.value) {
        is ResultState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = brown)
            }
        }

        is ResultState.Success -> {
            var showLogoutDialog by remember { mutableStateOf(false) }
            var showPremiumDialog by remember { mutableStateOf(false) }
            val activity = LocalContext.current as ComponentActivity
            val viewModel: NutsViewModel = viewModel(activity)

            val coroutineScope = rememberCoroutineScope()
            val nutsData = viewModel.nutsData

            val imageCounts by viewModelHome.imageCounts.collectAsState()
            LaunchedEffect(Unit) {
                viewModelHome.loadImageCounts(context,email,nutsData)
            }

            if (showPremiumDialog) {
                PremiumDialog(
                    onOpenWhatsApp = {
                        showPremiumDialog = false
                        val phoneNumber = "+6282176095404"
                        val url = "https://wa.me/$phoneNumber"
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = url.toUri()
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
                        coroutineScope.launch {
                            viewModelHome.logOut()
                            showLogoutDialog = false
                            navController.navigate(ScreenNuts.Login.route) {
                                popUpTo(0)
                            }
                        }
                    },
                    onDismiss = { showLogoutDialog = false }
                )
            }
            HomeScreen(
                navController = navController,
                onLogoutClick = { showLogoutDialog = true },
                onPremiumClick = { showPremiumDialog = true },
                isPremium = state.data.isPremium,
                name = state.data.name,
                email = email,
                nutsData = nutsData,
                imageCounts = imageCounts,
            )
        }

        is ResultState.Error -> {
            Text(
                text = "Gagal memuat data: ${state.message}",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    onLogoutClick: () -> Unit,
    onPremiumClick: () -> Unit,
    isPremium: Boolean,
    name: String ,
    email: String,
    nutsData: List<String>,
    imageCounts: Map<String,Int>
    ) {
    Log.d("ade", "ganteng $email")
    Scaffold(
        topBar = {
            CurvedTopBarActions(

                title = "Welcome, $name!",
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
        bottomBar = { BottomNavBar(navController, email) },
        containerColor = NutPrimaryLight
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
                        color = NutTextPrimary,
                        fontSize = 36.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(
                nutsData,
                key = { it }
            ){ item->
                val count = imageCounts[item] ?: 0
                Card(
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(3.dp, BrownCustom),
                    colors = CardDefaults.cardColors(containerColor = BrownGold),
                    elevation = CardDefaults.cardElevation(3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 4.dp)
                        .clickable {
                            navController.navigate(ScreenNuts.History.createRoute(email = email ,folderName = item))
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
                                    .border(1.dp, NutTextPrimary, RoundedCornerShape(10.dp)),
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
                            Text(
                                text = "$count gambar tersimpan",
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Light,
                                color = CreamMain
                            )
                        }
                    }
                }
            }

        }

    }
}



