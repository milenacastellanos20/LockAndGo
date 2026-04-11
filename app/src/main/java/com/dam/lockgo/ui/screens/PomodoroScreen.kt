package com.dam.lockgo.presentation.pomodoro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel = PomodoroViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Pomodoro")
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Tiempo: ${uiState.minutesLeft}:${uiState.secondsLeft.toString().padStart(2, '0')}")
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Estado: ${uiState.pomodoroState}")
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Pomodoros completados: ${uiState.completedPomodoros}")
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { viewModel.startPomodoro() }) {
            Text("Iniciar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.pausePomodoro() }) {
            Text("Pausar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.resetPomodoro() }) {
            Text("Resetear")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.completePomodoro() }) {
            Text("Completar Pomodoro")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.finishBreak() }) {
            Text("Finalizar descanso")
        }
    }
}
//Añadir