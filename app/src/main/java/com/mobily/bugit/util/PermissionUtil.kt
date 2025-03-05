package com.mobily.bugit.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionUtil {
    fun requiredPermissions(context: Context): List<String> {
        val permissions = mutableListOf<String>()
        
        // Camera permission is always required
        if (!hasCameraPermission(context)) {
            permissions.add(Manifest.permission.CAMERA)
        }

        // Storage permissions based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasMediaImagesPermission(context)) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (!hasStoragePermission(context)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        return permissions
    }

    private fun hasCameraPermission(context: Context): Boolean {
        return hasPermission(context, Manifest.permission.CAMERA)
    }

    private fun hasMediaImagesPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            true
        }
    }

    private fun hasStoragePermission(context: Context): Boolean {
        return hasPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
} 