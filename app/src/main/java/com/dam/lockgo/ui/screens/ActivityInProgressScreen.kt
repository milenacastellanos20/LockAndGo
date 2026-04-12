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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.dam.lockgo.ui.theme.LockGoTheme

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

        setContent {
            LockGoTheme {

                val isDarkMode = isSystemInDarkTheme()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2E7D32),
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icono_actividad),
            contentDescription = "Actividad en curso",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Actividad en curso!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ya tienes una actividad en marcha en tu reloj. Complétala antes de iniciar una nueva.",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "¡Sigue caminando!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onExitClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Entendido, salir al menú", fontSize = 16.sp, color = Color.White)
        }
    }
}