package com.example.nuts.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.scale

fun saveImageToFolderTemp(
    context: Context,
    bitmap: Bitmap?,
    folderName: String,
    email: String,
) {
    if (bitmap == null) {
        Toast.makeText(context, "Bitmap tidak valid", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val emailFolder = File(context.getExternalFilesDir(null), email)
        if (!emailFolder.exists()) emailFolder.mkdirs()

        val folder = File(emailFolder, folderName)
        if (!folder.exists()) folder.mkdirs()

        val timeStamp = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault()).format(Date())
        val fileName = "image_$timeStamp.jpg"

        val file = File(folder, fileName)

        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
        }

        Log.d("SAVE_IMAGE", "File saved at: ${file.absolutePath}")
        Toast.makeText(context, "Gambar berhasil disimpan", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Gagal menyimpan gambar", Toast.LENGTH_LONG).show()
    }
}

fun loadImagesWithNamesFromFolder(
    context: Context,
    folderName: String,
    email: String
): List<HistoryItem> {
    val imageList = mutableListOf<HistoryItem>()
    val folder = File(File(context.getExternalFilesDir(null), email), folderName)

    Log.d("LOAD_FOLDER", "Looking in: ${folder.absolutePath}")

    if (!folder.exists() || !folder.isDirectory) return emptyList()

    val files = folder.listFiles { file ->
        file.extension.equals("jpg", ignoreCase = true)
    }?.sortedByDescending { it.lastModified() } ?: return emptyList()

    for (file in files) {
        try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val dateSaved = file.nameWithoutExtension
                .removePrefix("image_")

            imageList.add(
                HistoryItem(
                    folderName = folderName,
                    fileName = file.name,
                    bitmap = bitmap,
                    dateSaved = dateSaved
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return imageList
}


data class HistoryItem(
    val folderName: String,
    val fileName: String,
    val bitmap: Bitmap?,
    val dateSaved: String
)
fun loadAllSavedImages(context: Context, email: String): List<HistoryItem> {
    val emailFolder = File(context.getExternalFilesDir(null), email)
    Log.d("LOAD_ALL", "Looking in email folder: ${emailFolder.absolutePath}")

    if (!emailFolder.exists() || !emailFolder.isDirectory) {
        Log.d("LOAD_ALL", "Email folder not found or empty")
        return emptyList()
    }

    val list = mutableListOf<HistoryItem>()

    emailFolder.listFiles()?.filter { it.isDirectory }?.forEach { folder ->
        folder.listFiles { file ->
            file.extension.equals("jpg", ignoreCase = true)
        }?.sortedByDescending { it.lastModified() }?.forEach { file ->
            try {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val dateSaved = file.nameWithoutExtension
                    .removePrefix("image_")

                list.add(
                    HistoryItem(
                        folderName = folder.name,
                        fileName = file.name,
                        bitmap = bitmap,
                        dateSaved = dateSaved
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    return list.sortedByDescending { it.dateSaved }
}

fun compressBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    if (width <= maxSize && height <= maxSize) return bitmap

    val scale = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height)
    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    return bitmap.scale(newWidth, newHeight)
}


