package com.dam.wearapp.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dam.wearapp.presentation.screens.WearDashboardScreen
import com.dam.wearapp.presentation.theme.LockGoTheme

val PERMISOS_REQUERIDOS = arrayOf(
    Manifest.permission.ACTIVITY_RECOGNITION,
    Manifest.permission.POST_NOTIFICATIONS
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            LockGoTheme {
                val context = LocalContext.current
                var permisosConcedidos by remember { mutableStateOf(tienePermisos(context)) }

                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { resultados ->
                    val todosOk = resultados.all { it.value }
                    if (todosOk) {
                        permisosConcedidos = true
                    } else {
                        // Si el usuario rechazó alguno, cerramos
                        cerrarApp()
                    }
                }

                // Disparamos la petición solo una vez al iniciar
                LaunchedEffect(Unit) {
                    if (!permisosConcedidos) {
                        launcher.launch(PERMISOS_REQUERIDOS)
                    }
                }

                if (permisosConcedidos) {
                    WearDashboardScreen()
                }
            }
        }
    }

    private fun tienePermisos(context: Context): Boolean {
        return PERMISOS_REQUERIDOS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun cerrarApp() {
        Toast.makeText(this,
            "Aplicación cerrada por falta de permisos",
            Toast.LENGTH_SHORT).show()
        finish()
    }

}
