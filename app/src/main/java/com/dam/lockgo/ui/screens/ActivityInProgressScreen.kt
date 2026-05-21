package com.dam.lockgo.ui.screens

import android.content.BroadcastReceiver
import android.content.Context
import com.dam.lockgo.R
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.dam.lockgo.ui.theme.LockGoTheme
import androidx.activity.OnBackPressedCallback

class ActivityInProgressScreen : ComponentActivity() {

    private val stopActivityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finalizarActividad()
        }
    }

    fun finalizarActividad() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ContextCompat.registerReceiver(
            this,
            stopActivityReceiver,
            IntentFilter("end_activity_in_progress"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)

            }
        })

        setContent {
            LockGoTheme {

                val isDarkMode = isSystemInDarkTheme()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2E7D32), // Mantenemos tu Surface original intacta
                    contentColor = if (isDarkMode) Color.White else Color.Black
                ) {
                    ActivityInProgressScreen(
                        onExitClick = { volverAlInicio() }
                    )
                }
            }
        }
    }

    private fun volverAlInicio() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}

@Composable
fun ActivityInProgressScreen(onExitClick: () -> Unit) {
    Log.d("Screen 2", "Screen 2 ejecutada")
    BackHandler {
        onExitClick()
    }

    // Fondo degradado verde oscuro deportivo/tecnológico
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1F10), // Verde bosque muy oscuro
                        Color(0xFF1B4D22)  // Verde deportivo profundo
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Tarjeta central con la misma estética premium oscura para dar cohesión a la app
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)) // Fondo oscuro
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icono de actividad envuelto en un círculo verde neón traslúcido
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(Color(0xFF00E676).copy(alpha = 0.15f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icono_actividad),
                        contentDescription = "Actividad en curso",
                        modifier = Modifier.size(44.dp),
                        tint = Color(0xFF00E676) // Verde neón vibrante
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¡EN MARCHA!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ya tienes una actividad en marcha en tu reloj. Complétala antes de iniciar una nueva.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.LightGray,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Mensaje motivacional en Rojo brillante para balancear el contraste de la app
                Text(
                    text = "¡Sigue caminando!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5252), // Rojo vibrante
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Botón principal estilizado en verde
                Button(
                    onClick = onExitClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32), // Verde sólido corporativo
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "ENTENDIDO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}