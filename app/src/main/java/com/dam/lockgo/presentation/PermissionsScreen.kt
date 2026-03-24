package com.dam.lockgo.presentation

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
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun PermissionsScreen(
    viewModel: PermissionsViewModel, // Recibimos el ViewModel
    onAllPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current
    val state = viewModel.uiState // Observamos el estado del ViewModel

    // Launcher para el permiso de actividad física
    val activityPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Cuando responde el pop-up, le decimos al ViewModel que refresque
        viewModel.checkPermissions()
    }

    // Observador para detectar cuando el usuario vuelve de Ajustes
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // El usuario ha vuelto de la pantalla de ajustes de Android
                viewModel.checkPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Si el estado del ViewModel dice que todo está OK, navegamos
    if (state.isAllGranted) {
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
            isGranted = state.activityRecognition, // Usamos el estado del ViewModel
            onClick = { activityPermissionLauncher.launch(android.Manifest.permission.ACTIVITY_RECOGNITION) }
        )

        PermissionItem(
            title = "Superposición de Apps",
            isGranted = state.overlay, // Usamos el estado del ViewModel
            onClick = {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                context.startActivity(intent)
            }
        )

        PermissionItem(
            title = "Servicio de Accesibilidad",
            isGranted = state.accessibility, // Usamos el estado del ViewModel
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        ) {
            Text(if (isGranted) "✓ Concedido" else "Conceder")
        }
    }
}