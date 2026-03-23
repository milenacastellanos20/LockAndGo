package com.dam.lockgo.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun PermissionsScreen(onAllPermissionsGranted: () -> Unit) {
    val context = LocalContext.current

    // Estados para controlar si los permisos están activos
    var hasActivityPermission by remember { mutableStateOf(checkActivityPermission(context)) }
    var hasOverlayPermission by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var hasAccessibilityPermission by remember { mutableStateOf(isAccessibilityServiceEnabled(context)) }

    // Launcher para el permiso de actividad física
    val activityPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasActivityPermission = isGranted }

    // Observador para detectar cuando el usuario vuelve de Ajustes
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Re-comprobar permisos cuando el usuario regresa a la app
                hasOverlayPermission = Settings.canDrawOverlays(context)
                hasAccessibilityPermission = isAccessibilityServiceEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Si todos están concedidos, podemos avanzar
    if (hasActivityPermission && hasOverlayPermission && hasAccessibilityPermission) {
        LaunchedEffect(Unit) { onAllPermissionsGranted() }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Configuración de Lock&Go", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        PermissionItem(
            title = "Actividad Física",
            isGranted = hasActivityPermission,
            onClick = { activityPermissionLauncher.launch(android.Manifest.permission.ACTIVITY_RECOGNITION) }
        )

        PermissionItem(
            title = "Superposición de Apps",
            isGranted = hasOverlayPermission,
            onClick = {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                context.startActivity(intent)
            }
        )

        PermissionItem(
            title = "Servicio de Accesibilidad",
            isGranted = hasAccessibilityPermission,
            onClick = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun PermissionItem(title: String, isGranted: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        ) {
            Text(if (isGranted) "✓" else "Conceder")
        }
    }
}

// Funciones auxiliares de comprobación
fun checkActivityPermission(context: Context): Boolean {
    return androidx.core.content.ContextCompat.checkSelfPermission(
        context, android.Manifest.permission.ACTIVITY_RECOGNITION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val expectedServiceName = "${context.packageName}/${context.packageName}.data.service.AppBlockingService"
    val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
    return enabledServices?.contains(expectedServiceName) == true
}