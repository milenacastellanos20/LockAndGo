package com.dam.lockgo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.WorkspacePremium
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
fun AchievementsScreen(
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título principal
                Text(
                    text = "Tus Emblemas",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "Colección de logros desbloqueados",
                    fontSize = 14.sp,
                    color = Color(0xFFAAAAAA),
                    modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
                )

                // Lógica visual: ¿Hay emblemas o está vacío?
                if (uiState.ownedBadges.isEmpty()) {
                    // --- ESTADO VACÍO ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2A2A2A))
                            .border(1.dp, Color(0xFF444444), RoundedCornerShape(16.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SentimentDissatisfied,
                                contentDescription = "Sin emblemas",
                                tint = Color(0xFFAAAAAA),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Aún no tienes emblemas",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pásate por la tienda cuando consigas más monedas.",
                                color = Color(0xFFAAAAAA),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // --- LISTA DE EMBLEMAS ---
                    uiState.ownedBadges.forEach { badge ->
                        OwnedBadgeCard(badgeName = badge)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1f)) // Empuja el botón al fondo si la lista es corta

                Spacer(modifier = Modifier.height(32.dp))

                // Botón Volver inferior (Mantenido de tu diseño original)
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFAAAAAA))
                ) {
                    Text("VOLVER", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}

// Componente visual para mostrar cada emblema de forma elegante
@Composable
fun OwnedBadgeCard(badgeName: String) {
    // Detectamos el color basándonos en el nombre del string (solo visual)
    val badgeColor = when {
        badgeName.contains("Bronce", ignoreCase = true) -> Color(0xFFCD7F32)
        badgeName.contains("Plata", ignoreCase = true) -> Color(0xFFC0C0C0)
        badgeName.contains("Oro", ignoreCase = true) -> Color(0xFFFFD700)
        badgeName.contains("Diamante", ignoreCase = true) -> Color(0xFF00E5FF)
        else -> Color(0xFF43A047) // Verde por defecto si no coincide
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2A2A2A))
            .border(1.dp, badgeColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = badgeName,
                tint = badgeColor,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = badgeName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Icono de "Conseguido" a la derecha
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Desbloqueado",
            tint = Color(0xFF43A047), // Verde
            modifier = Modifier.size(24.dp)
        )
    }
}