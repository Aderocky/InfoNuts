package com.example.nuts.screens.klasifikasi

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nuts.clasifications.ClassificationResult
import com.example.nuts.clasifications.Classifier
import com.example.nuts.state.ResultState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class KlasifikasiViewModel(
    private val classifier: Classifier
) : ViewModel() {

    private val _result = MutableSharedFlow<ResultState<ClassificationResult>>()
    val result = _result.asSharedFlow()

    fun classify(bitmap: Bitmap, start: Long) {
        viewModelScope.launch {
            _result.emit(ResultState.Loading)
            try {
                val recognition = classifier.classify(bitmap, start)
                if (recognition != null) {
                    _result.emit(ResultState.Success(recognition))
                } else {
                    _result.emit(ResultState.Error("Tidak terdapat kacang pada gambar"))
                }
            } catch (e: Exception) {
                _result.emit(ResultState.Error("Error during classification: ${e.message}"))
            }
        }
    }
    fun resetResultState(){
        viewModelScope.launch {
            _result.emit(ResultState.Loading)
        }
    }
}

class KlasifikasiViewModelFactory(
    private val classifier: Classifier
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KlasifikasiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KlasifikasiViewModel(classifier) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
