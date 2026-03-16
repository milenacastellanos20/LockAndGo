package com.dam.lockgo

import com.dam.lockgo.presentation.PermissionsScreen

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Aplicamos el sistema de diseño de Material 3
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Estado que controla si mostramos permisos o la app principal
                    var needsPermissions by remember {
                        mutableStateOf(!areAllPermissionsGranted(this))
                    }

                    if (needsPermissions) {
                        // Pasamos una función lambda que se ejecuta cuando todos los botones están en verde
                        PermissionsScreen(onAllPermissionsGranted = {
                            needsPermissions = false
                        })
                    } else {
                        MainDashboard()
                    }
                }
            }
        }
    }

    /**
     * Comprueba si los tres permisos críticos están activos
     */
    private fun areAllPermissionsGranted(context: Context): Boolean {
        val hasActivity = ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED

        val hasOverlay = Settings.canDrawOverlays(context)

        val hasAccessibility = isAccessibilityServiceEnabled(context)

        return hasActivity && hasOverlay && hasAccessibility
    }

    /**
     * Lógica específica para verificar si nuestro servicio de accesibilidad está ON
     */
    private fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val expectedServiceName =
            "${context.packageName}/${context.packageName}.data.service.AppBlockingService"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(expectedServiceName) == true
    }
}

@Composable
fun MainDashboard() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "¡Lock&Go Activo!\nEl sistema de bloqueo y pasos está funcionando.",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}