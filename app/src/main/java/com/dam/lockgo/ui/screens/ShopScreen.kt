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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dam.lockgo.service.RewardViewModel

@Composable
fun ShopScreen(
    onBack: () -> Unit = {},
    viewModel: RewardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshWallet()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Tienda")
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Monedas disponibles: ${uiState.coins}")
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.buyBadge("Bronce", 50) }) {
            Text("Emblema Bronce - 50")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.buyBadge("Plata", 100) }) {
            Text("Emblema Plata - 100")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.buyBadge("Oro", 150) }) {
            Text("Emblema Oro - 150")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.buyBadge("Diamante", 200) }) {
            Text("Emblema Diamante - 200")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.message.isNotEmpty()) {
            Text(text = uiState.message)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}