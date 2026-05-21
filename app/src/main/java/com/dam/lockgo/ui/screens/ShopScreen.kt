package com.dam.lockgo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(
        topBar = { TopBarComponent(onBackClick = onBack) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E1E1E), // Gris muy oscuro
                            Color(0xFF121212)  // Casi negro
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Añadido scroll por si la pantalla es pequeña
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título principal
                Text(
                    text = "Tienda de Emblemas",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Píldora de Monedas Disponibles
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF43A047).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFF43A047).copy(alpha = 0.5f), RoundedCornerShape(50))
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Stars, contentDescription = null, tint = Color(0xFF43A047))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tienes ${uiState.coins} monedas",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- ESCAPARATE DE PRODUCTOS ---

                // Emblema Bronce
                StoreItemCard(
                    name = "Emblema Bronce",
                    price = 50,
                    badgeColor = Color(0xFFCD7F32), // Color bronce real
                    onClick = { viewModel.buyBadge("Bronce", 50) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Emblema Plata
                StoreItemCard(
                    name = "Emblema Plata",
                    price = 100,
                    badgeColor = Color(0xFFC0C0C0), // Color plata real
                    onClick = { viewModel.buyBadge("Plata", 100) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Emblema Oro
                StoreItemCard(
                    name = "Emblema Oro",
                    price = 150,
                    badgeColor = Color(0xFFFFD700), // Color oro real
                    onClick = { viewModel.buyBadge("Oro", 150) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Emblema Diamante
                StoreItemCard(
                    name = "Emblema Diamante",
                    price = 200,
                    badgeColor = Color(0xFF00E5FF), // Color diamante neón
                    onClick = { viewModel.buyBadge("Diamante", 200) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- ZONA DE MENSAJES (Ej: Compra exitosa o sin saldo) ---
                if (uiState.message.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF43A047).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFF43A047), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.message,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Spacer(modifier = Modifier.weight(1f)) // Empuja el botón de volver al fondo si hay espacio

                // Botón Volver inferior
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFAAAAAA))
                ) {
                    Text("VOLVER AL MENÚ", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}

// Componente reutilizable para las tarjetas de la tienda
@Composable
fun StoreItemCard(name: String, price: Int, badgeColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2A2A2A)) // Fondo de la tarjeta
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Icono de medalla
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = name,
                tint = badgeColor,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Botón de precio
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)), // Botón de compra en verde
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "$price 🪙",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}