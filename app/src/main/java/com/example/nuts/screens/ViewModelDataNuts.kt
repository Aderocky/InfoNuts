package com.example.nuts.screens

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.nuts.clasifications.ClassificationResult
import com.example.nuts.utils.nuts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class NutsViewModel : ViewModel() {
    val nutsData: List<String> = nuts.shuffled()
    var classifiedBitmap by mutableStateOf<Bitmap?>(null)
    var classifiedResult by mutableStateOf<ClassificationResult?>(null)
}
