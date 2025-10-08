package com.example.nuts.screens.hasil

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.nuts.components.BottomNavBar
import com.example.nuts.components.CurvedTopBar
import com.example.nuts.navigations.ScreenNuts
import com.example.nuts.ui.theme.brown
import com.example.nuts.ui.theme.krem
import com.example.nuts.utils.latinNameNuts
import com.example.nuts.utils.saveImageToFolderTemp
import java.io.File

@Composable
fun Hasil(
    navController: NavHostController,
    name: String,
    confidence: Float = 0f,
    timeCost: Long = 0L,
    fileName: String? = null
){
    val context = LocalContext.current
    val image: Bitmap?
    val imagePath: String

    if (fileName == null) {
        val path = context.getExternalFilesDir(null)!!.absolutePath
        imagePath = "$path/tempFileName.jpg"
        image = remember { BitmapFactory.decodeFile(imagePath) }
    } else {
        val folder = File(context.getExternalFilesDir(null), name)
        val file = File(folder, fileName)
        imagePath = file.absolutePath
        image = remember { BitmapFactory.decodeFile(imagePath) }
    }

    HasilScreen(
        navController = navController,
        onBackClick = { navController.popBackStack()},
        name = name,
        confidence = confidence,
        timeCost = timeCost,
        image = image,
        imagePath = imagePath,
    )
}

@Composable
fun HasilScreen(
    navController: NavHostController,
    onBackClick: () -> Unit,
    name: String,
    confidence: Float = 0f,
    timeCost: Long = 0L,
    image: Bitmap?,
    imagePath: String,
){
    val context = LocalContext.current

    Scaffold (
        topBar = {
            CurvedTopBar(
                title = "Hasil",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            BottomNavBar(navController)
        },
        containerColor = krem
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column (
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ){
                    if (image != null){
                        Image(
                            bitmap = image.asImageBitmap(),
                            contentDescription = "Hasil Gambar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "PlaceHolderImage",
                            tint = Color.Gray,
                            modifier = Modifier.size(220.dp)
                        )
                    }

                }
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = name,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = latinNameNuts[name].toString(),
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = """
                    Kacang tanah adalah salah satu jenis kacang yang banyak dikonsumsi...
                    
                    Informasi bisa sangat panjang di sini, menjelaskan manfaat, kandungan gizi, habitat, sejarah, dan lain-lain.
                    
                    Karena teks ini dibungkus dalam Column + verticalScroll, maka jika panjang, user bisa menggulir ke bawah untuk membaca semuanya.
                """.trimIndent(),
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            }
            if(confidence != 0f && timeCost != 0L){
                Text(
                    text = "Waktu prediksi: ${timeCost}ms",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Button(
                        onClick = {
                            saveImageToFolderTemp(
                                context = context,
                                bitmap = image,
                                folderName = name
                            )
                            navController.navigate(ScreenNuts.Home.route) {
                                popUpTo(ScreenNuts.Home.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = brown)
                    ) {
                        Text("Simpan", color = Color.White)
                    }
                    Button(
                        onClick = {
                            val file = File(imagePath)
                            if (file.exists()) {
                                val deleted = file.delete()
                                if (!deleted) {
                                    Log.e("HasilScreen", "Gagal menghapus file: $imagePath")
                                }
                            }
                            onBackClick()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = brown),
                    ) {
                        Text("Kembali", color = Color.White)
                    }
                }
            }
        }
    }
}