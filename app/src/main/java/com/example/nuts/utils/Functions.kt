package com.example.nuts.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun saveImageToFolder(
    context: Context,
    bitmap: Bitmap?,
    folderName: String,
    //email: String
) {
    try {

//        val emailFolder = File(context.getExternalFilesDir(null), email)
//        if (!emailFolder.exists()) emailFolder.mkdirs()


//        val folder = File(emailFolder, folderName)
//        if (!folder.exists()) folder.mkdirs()

        val folder = File(context.getExternalFilesDir(null), folderName)
        if (!folder.exists()) folder.mkdirs()

        // Format waktu agar nama file mudah dibaca
        val timeStamp = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault()).format(Date())
        val fileName = "image_$timeStamp.jpg"

        val file = File(folder, fileName)

        FileOutputStream(file).use { output ->
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, output)
        }

        Toast.makeText(context, "Gambar disimpan: $fileName", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Gagal menyimpan gambar", Toast.LENGTH_LONG).show()
    }
}

fun saveImageToFolderTemp(
    context: Context,
    bitmap: Bitmap?,
    folderName: String
) {
    if (bitmap == null) {
        Toast.makeText(context, "Bitmap tidak valid", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val folder = File(context.getExternalFilesDir(null), folderName)
        if (!folder.exists() && !folder.mkdirs()) {
            Toast.makeText(context, "Gagal membuat folder $folderName", Toast.LENGTH_SHORT).show()
            return
        }

        val timeStamp = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault()).format(Date())
        val fileName = "image_$timeStamp.jpg"

        val file = File(folder, fileName)

        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
        }

        Toast.makeText(context, "Gambar berhasil disimpan", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Gagal menyimpan gambar", Toast.LENGTH_LONG).show()
    }
}

fun loadImagesWithNamesFromFolder(context: Context, folderName: String): List<HistoryItem> {
    val imageList = mutableListOf<HistoryItem>()
    val folder = File(context.getExternalFilesDir(null), folderName)

    if (!folder.exists() || !folder.isDirectory) return emptyList()
    val files = folder.listFiles { file -> file.extension.equals("jpg", ignoreCase = true) }
        ?.sortedByDescending { it.lastModified() }
        ?: return emptyList()

    for (file in files) {
        try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            if (bitmap != null) {
                val dateSaved = file.nameWithoutExtension
                    .removePrefix("image_")
                    .replace('_', ' ')
                    .replace('-', '/')

                imageList.add(
                    HistoryItem(
                        folderName = folderName,
                        fileName = file.name,
                        bitmap = bitmap,
                        dateSaved = dateSaved
                    )
                )
            }
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

fun loadAllSavedImages(context: Context): List<HistoryItem> {
    val baseDir = context.getExternalFilesDir(null) ?: return emptyList()
    val list = mutableListOf<HistoryItem>()

    baseDir.listFiles()?.filter { it.isDirectory }?.forEach { folder ->
        val files = folder.listFiles { file ->
            file.extension.equals("jpg", ignoreCase = true)
        }?.sortedByDescending { it.lastModified() }

        files?.forEach { file ->
            try {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val dateSaved = file.nameWithoutExtension
                    .removePrefix("image_")
                    .replace('_', ' ')
                    .replace('-', '/')

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


