package com.example.nuts.screens

import androidx.lifecycle.ViewModel
import com.example.nuts.utils.nuts

class NutsViewModel : ViewModel() {
    val nutsData: List<String> = nuts.shuffled()
}
