package com.dam.lockgo.ui.screens

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
import com.dam.lockgo.presentation.rewards.RewardViewModel

@Composable
fun RewardScreen(
    viewModel: RewardViewModel = RewardViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sistema de recompensas")
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Monedas: ${uiState.coins}")
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Objetivos completados: ${uiState.completedObjectives}")
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.message.isNotEmpty()) {
            Text(text = uiState.message)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = { viewModel.addCoins(5) }) {
            Text("Añadir 5 monedas")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.completeObjective(10) }) {
            Text("Completar objetivo (+10)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.spendCoins(8) }) {
            Text("Gastar 8 monedas")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.clearMessage() }) {
            Text("Limpiar mensaje")
        }
    }
}