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
import com.dam.lockgo.presentation.PermissionsScreen
import com.dam.lockgo.presentation.PermissionsViewModel

class MainActivity : ComponentActivity() {

    // Instanciamos el ViewModel
    private val permissionsViewModel: PermissionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Obtenemos el estado directamente del ViewModel
                    val permissionState = permissionsViewModel.uiState

                    // Estado local para controlar la navegación tras conceder permisos
                    var allPermissionsGranted by remember {
                        mutableStateOf(permissionState.isAllGranted)
                    }

                    if (!allPermissionsGranted) {
                        PermissionsScreen(
                            viewModel = permissionsViewModel,
                            onAllPermissionsGranted = {
                                allPermissionsGranted = true
                            }
                        )
                    } else {
                        MainDashboard()
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