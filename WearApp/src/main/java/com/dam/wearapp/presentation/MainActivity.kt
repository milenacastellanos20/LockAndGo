package com.dam.wearapp.presentation

import android.content.Context
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
import com.dam.wearapp.presentation.screens.WearDashboardScreen
import com.dam.wearapp.presentation.theme.LockGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LockGoTheme {

                //Se podría usar el this también
                //pero para los permisos es mejor usar
                //el contexto actual definido en una variable
                //por si en un futuro se quiere migrar la lógica de
                //los permisos a una clase Kotlin aparte
                val context = LocalContext.current

                var tienePermiso by
                remember { mutableStateOf(
                    tienePermiso(context)
                ) }

                val permissionsLauncher =
                    rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->

                        if (isGranted) {
                            tienePermiso = true
                        } else {
                            android.widget.Toast.makeText(
                                this,
                                "La aplicación va a cerrarse " +
                                        "dado a que no puede funcionar" +
                                        "sin los permisos necesarios",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()

                        }

                    }

                LaunchedEffect(tienePermiso) {
                    if (!tienePermiso) {
                        permissionsLauncher.launch(
                            android.Manifest.permission.ACTIVITY_RECOGNITION
                        )
                    }
                }

                if (tienePermiso) {
                    WearDashboardScreen()
                }

            }
        }
    }

    fun tienePermiso(context: Context): Boolean {
        return androidx.core.content.
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission
                .ACTIVITY_RECOGNITION
        ) == android.content.pm
            .PackageManager.PERMISSION_GRANTED
    }
}