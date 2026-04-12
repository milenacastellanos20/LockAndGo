package com.dam.lockgo.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun MainScreen(
    onNavigateAppSelection: () -> Unit = {},
    onNavigatePomodoro: () -> Unit = {},
    onNavigateRewards: () -> Unit = {}

) {
    Log.d("Screen 1", "Screen 1 ejecutada")

    Scaffold(
        topBar = { TopBarComponent() },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Botón pasos
                StartActivityScreenButtonComponent(onNavigateAppSelection)

                Spacer(modifier = Modifier.height(16.dp))

                // Boton pomodoro
                Button(
                    onClick = {
                        onNavigatePomodoro()
                    }
                ) {
                    Text("Modo Pomodoro")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Boton rewards
                Button(
                    onClick = {
                        onNavigateRewards()
                    }
                ) {
                    Text("Recompensas")
                }

            }
        }
    }
}

@Composable
fun StartActivityScreenButtonComponent(onNavigate: () -> Unit) {

    Button(
        onClick = {
            onNavigate()
        }

    ) {
        Text(text = "Comenzar actividad")
    }

}


