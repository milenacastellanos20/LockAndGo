package com.dam.lockgo.domain.usecase

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat

class CheckPermissionsUseCase(private val context: Context) {

    operator fun invoke(): PermissionStatus {
        return PermissionStatus(
            activityRecognition = hasActivityPermission(),
            overlay = Settings.canDrawOverlays(context),
            accessibility = isAccessibilityServiceEnabled()
        )
    }

    private fun hasActivityPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedServiceName =
            "${context.packageName}/${context.packageName}.data.service.AppBlockingService"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(expectedServiceName) == true
    }
}

// Clase de datos para agrupar el estado
data class PermissionStatus(
    val activityRecognition: Boolean,
    val overlay: Boolean,
    val accessibility: Boolean
) {
    val isAllGranted: Boolean get() = activityRecognition && overlay && accessibility
}