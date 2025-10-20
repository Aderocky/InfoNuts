package com.example.nuts.screens.klasifikasi

import android.graphics.Bitmap
import android.net.Uri
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.nuts.clasifications.ClassificationResult
import com.example.nuts.clasifications.Classifier
import com.example.nuts.components.BottomNavBar
import com.example.nuts.components.CurvedTopBar
import com.example.nuts.data.di.DatabaseSupabaseClient
import com.example.nuts.data.repository.AuthRepository
import com.example.nuts.navigations.ScreenNuts
import com.example.nuts.screens.authentication.ViewModelFactory
import com.example.nuts.state.ResultState
import com.example.nuts.ui.theme.BrownGold
import com.example.nuts.ui.theme.NutPrimaryLight
import com.example.nuts.ui.theme.NutTextPrimary
import com.example.nuts.ui.theme.brown
import com.example.nuts.ui.theme.krem
import java.io.File
import java.io.FileOutputStream

@Composable
fun Klasifikasi (
    navController: NavHostController,
){
    val context = LocalContext.current

    val classifier = remember { Classifier(context) }
    val authRepository = remember {
        AuthRepository(
            sbClient = DatabaseSupabaseClient.client(),
            context = context
        )
    }
    val viewModelKlasifikasi: KlasifikasiViewModel = viewModel(
        factory = ViewModelFactory(authRepository, classifier)
    )
    val userState = viewModelKlasifikasi.userState.observeAsState()
    var isPremium by remember { mutableStateOf(false) }
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
    val resultState by viewModelKlasifikasi.result.collectAsState(initial = ResultState.Loading)
    var email by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val savedEmail = viewModelKlasifikasi.getCurrentEmail()
        if (!savedEmail.isNullOrEmpty()) {
            email = savedEmail
        }
    }
    userState.value.let {
        LaunchedEffect(it) {
            when (it) {
                is ResultState.Success -> {
                    isPremium = it.data.isPremium
                    Log.d("ade", "premium $isPremium")
                }
                else -> {
                }
            }
        }
    }

    KlasifikasiScreen(
        navController = navController ,
        onBackClick = { navController.popBackStack() },
        isPremium = isPremium,
        bitmapState = bitmapState,
        onClassify =  { bitmap ->
            val start = SystemClock.uptimeMillis()
            viewModelKlasifikasi.classify(bitmap,start)
        },
        result = resultState,
        email = email,
        resetResultState = {viewModelKlasifikasi.resetResultState()},
    )

}

@Composable
fun KlasifikasiScreen(
    navController: NavHostController,
    onBackClick: () -> Unit,
    isPremium: Boolean,
    bitmapState: MutableState<Bitmap?>,
    onClassify: (Bitmap) -> Unit,
    result: ResultState<ClassificationResult>,
    email: String,
    resetResultState: () -> Unit
){

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { capturedBitmap ->
        bitmapState.value = capturedBitmap
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val source = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            bitmapState.value = source
        }
    }

    LaunchedEffect(result) {
        if (result is ResultState.Success) {
            val data = result.data

            val emailFolder = File(context.getExternalFilesDir(null), email)

            if (!emailFolder.exists()) {
                emailFolder.mkdirs()
            }
            val imagePath = File(emailFolder, "tempFileName.jpg").absolutePath

            FileOutputStream(imagePath).use { output ->
                bitmapState.value?.compress(Bitmap.CompressFormat.JPEG, 100, output)
            }

            navController.navigate(
                ScreenNuts.Hasil.createRoute(
                    data.label,
                    data.confidence,
                    data.timeMs
                )
            )
            resetResultState()
        }
    }

    Scaffold(
        topBar = {
            CurvedTopBar(
                title = "Klasifikasi",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            BottomNavBar(navController,email)
        },
        containerColor = NutPrimaryLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(290.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .border(2.dp, NutTextPrimary, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (bitmapState.value != null) {
                    Image(
                        bitmap = bitmapState.value!!.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, NutTextPrimary, RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Placeholder",
                        modifier = Modifier.size(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(67.dp)
            ) {
                Button(
                    onClick = {
                        launcher.launch()
                    },
                    modifier = Modifier
                        .width(110.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPremium) BrownGold else Color.Gray,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                ),
                enabled = isPremium
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera",
                         modifier = Modifier.size(30.dp),
                        tint = Color.White
                    )
                }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = BrownGold),
                    modifier = Modifier
                        .width(110.dp)
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery"
                        , modifier = Modifier.size(30.dp),
                        tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    val bitmap = bitmapState.value ?: bitmapState.value
                    if (bitmap != null) {
                        bitmapState.value?.let { bitmap ->
                            onClassify(bitmap)
                        }
                    }
                },
                enabled = bitmapState.value != null,
                modifier = Modifier
                    .fillMaxWidth(0.83f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrownGold,
                    disabledContainerColor = Color.Gray)
            ) {
                Text(
                    text = "Klasifikasi",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
    }
}