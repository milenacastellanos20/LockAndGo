package com.dam.lockgo.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dam.lockgo.ui.theme.LockGoTheme

class BlockingScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recogemos el nombre del paquete que nos manda el AppBlockingService
        val blockedPackage = intent.getStringExtra("BLOCKED_PACKAGE")

        val appName = if (blockedPackage != null) {

            try {

                val pm = packageManager
                val appInfo = pm.getApplicationInfo(blockedPackage, 0)
                pm.getApplicationLabel(appInfo).toString();

            } catch (e: Exception) {
                "esta aplicación"
            }

        } else {
            "esta aplicación"
        }

        setContent {
            LockGoTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.errorContainer // Mantenemos tu Surface original intacta
                ) {
                    BlockingScreen(
                        appName = appName,
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
fun BlockingScreen(appName: String, onExitClick: () -> Unit) {
    BackHandler {
        onExitClick()
    }

    // Fondo degradado rojo oscuro para dar sensación de "Alerta/Bloqueo"
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2C0B0E), // Un rojo casi negro
                        Color(0xFF8F1822)  // Rojo intenso
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Tarjeta central flotante estilo Premium
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)) // Fondo oscuro para la tarjeta
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icono del candado con círculo de fondo translúcido
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(Color(0xFFD32F2F).copy(alpha = 0.2f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Candado",
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFFFF5252) // Rojo brillante
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¡BLOQUEADO!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No puedes usar $appName porque aún no has cumplido tu meta de pasos.",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.LightGray,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Mensaje motivacional en Verde vibrante
                Text(
                    text = "¡Necesitas salir a caminar!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50), // Verde vibrante
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Botón principal
                Button(
                    onClick = onExitClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F), // Rojo sólido
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