package com.example.nuts.screens.history

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.nuts.components.CurvedTopBar
import com.example.nuts.data.di.DatabaseSupabaseClient
import com.example.nuts.data.repository.AuthRepository
import com.example.nuts.navigations.ScreenNuts
import com.example.nuts.screens.authentication.ViewModelFactory
import com.example.nuts.state.ResultState
import com.example.nuts.ui.theme.BrownCustom
import com.example.nuts.ui.theme.BrownGold
import com.example.nuts.ui.theme.CreamMain
import com.example.nuts.ui.theme.NutPrimaryLight
import com.example.nuts.ui.theme.NutTextPrimary
import com.example.nuts.ui.theme.beige
import com.example.nuts.ui.theme.brown
import com.example.nuts.ui.theme.krem
import com.example.nuts.utils.HistoryItem
import com.example.nuts.utils.latinNameNuts
import com.example.nuts.utils.loadAllSavedImages
import com.example.nuts.utils.loadImagesWithNamesFromFolder

@Composable
fun History(
    navController: NavHostController,
    email: String,
    folderName: String? = null
){
    val context = LocalContext.current

    var historyItems by remember { mutableStateOf<List<HistoryItem>>(emptyList()) }

    LaunchedEffect(folderName) {
        historyItems = if (folderName == null) {
            loadAllSavedImages(context, email)
        } else {
            loadImagesWithNamesFromFolder(context, folderName, email)
        }
    }

    HistoryScreen(
        navController = navController,
        onBackClick = {navController.popBackStack()},
        historyItems = historyItems,
        folderName = folderName,
        email = email,
        title = if (folderName == null) "Riwayat Pengenalan" else "Riwayat $folderName",
    )
}

@Composable
fun HistoryScreen(
    navController: NavHostController,
    onBackClick: () -> Unit,
    historyItems: List<HistoryItem>,
    folderName: String?,
    email: String,
    title: String,
){
    Scaffold (
        topBar = {
            CurvedTopBar(
                title = title,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            BottomNavBar(navController, email)
        },
        containerColor = NutPrimaryLight
    ){ innerPadding ->
        if (historyItems.isEmpty()){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                val message = if (folderName.isNullOrEmpty()) {
                    "Anda belum menyimpan gambar kacang pada perangkat ini"
                } else {
                    "Anda belum menyimpan gambar kacang $folderName pada perangkat ini"
                }
                    Text(
                        text = message,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NutTextPrimary,
                        textAlign = TextAlign.Center
                    )
            }
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()
                    .padding(horizontal = 7.dp) ,
                contentPadding = PaddingValues(bottom = 20.dp)
            ){
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 27.dp, bottom = 20.dp),
                        text = if (folderName.isNullOrEmpty()) "Saved Nuts" else "Saved $folderName",
                        fontWeight = FontWeight.Bold,
                        color = NutTextPrimary,
                        fontSize = 36.sp,
                        textAlign = TextAlign.Center
                    )
                }
                items(
                    historyItems,
                    key = { it.fileName }
                ){ item ->
                        Card(
                            shape = RoundedCornerShape(15.dp),
                            border = BorderStroke(3.dp, BrownCustom),
                            colors = CardDefaults.cardColors(containerColor = BrownGold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 0.dp, vertical = 4.dp)
                                .clickable {
                                    navController.navigate(
                                        ScreenNuts.Hasil.createRoute(
                                            name = item.folderName,
                                            fileName = item.fileName
                                        )
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                item.bitmap?.let {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(1.dp, NutTextPrimary, RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.width(13.dp))

                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = item.folderName,
                                        fontSize = 26.sp,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = latinNameNuts[item.folderName].toString(),
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Light,
                                        fontStyle = FontStyle.Italic,
                                        color = CreamMain
                                    )
                                    Text(
                                        text = item.dateSaved,
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
}