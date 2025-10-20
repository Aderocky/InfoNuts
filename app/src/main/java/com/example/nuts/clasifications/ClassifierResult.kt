package com.example.nuts.clasifications

data class ClassificationResult(
    val label: String,
    val confidence: Float,
    val timeMs: Long
)