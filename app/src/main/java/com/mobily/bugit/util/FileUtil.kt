package com.mobily.bugit.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtil {
    private const val AUTHORITY_SUFFIX = ".provider"
    private const val DATE_FORMAT = "yyyyMMdd_HHmmss"
    private const val JPEG_EXTENSION = ".jpg"
    private const val IMAGE_PREFIX = "IMG_"

    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        val imageFileName = "$IMAGE_PREFIX${timeStamp}"
        return File(context.cacheDir, "$imageFileName$JPEG_EXTENSION")
    }

    fun getUriForFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}$AUTHORITY_SUFFIX",
            file
        )
    }

    fun createTempImageFile(context: Context): Pair<File, Uri> {
        val imageFile = createImageFile(context)
        val imageUri = getUriForFile(context, imageFile)
        return imageFile to imageUri
    }

    fun copyUriToFile(context: Context, uri: Uri): File {
        val destinationFile = createImageFile(context)
        context.contentResolver.openInputStream(uri)?.use { input ->
            destinationFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return destinationFile
    }
}