package com.dam.lockgo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dam.lockgo.ui.screens.PermissionsScreen
import com.dam.lockgo.service.PermissionsViewModel
import com.dam.lockgo.service.AppNavigation
import com.dam.lockgo.ui.theme.LockGoTheme

class MainActivity : ComponentActivity() {

    private val permissionsViewModel: PermissionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LockGoTheme {

                // Obtenemos el estado del ViewModel
                val permissionState = permissionsViewModel.uiState

                // Estado local para saber si ya se concedieron todos los permisos
                var allPermissionsGranted by remember {
                    mutableStateOf(permissionState.isAllGranted)
                }

                MaterialTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (!allPermissionsGranted) {
                            PermissionsScreen(
                                viewModel = permissionsViewModel,
                                onAllPermissionsGranted = {
                                    allPermissionsGranted = true
                                }
                            )
                        } else {
                            AppNavigation()
                        }
                    }
                }
            }
        }
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