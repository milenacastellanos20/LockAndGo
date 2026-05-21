package com.dam.lockgo.ui.screens

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dam.lockgo.data.service.AppBlockingService
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable

@Composable
fun StartActivityScreen(
    onBack: () -> Unit = {},
    selectedApps: List<String>
) {
    val context = LocalContext.current
    //Variables para el TextField
    var text by remember { mutableStateOf("") }
    val maxChars = 5
    val prefs = context.getSharedPreferences("LockAndGoPrefs", Context.MODE_PRIVATE)

    Scaffold(
        topBar = { TopBarComponent(onBackClick = { onBack() }) },
    ) { innerPadding ->
        // Fondo con un sutil degradado oscuro para dar aspecto premium y hacer brillar los colores
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
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                // Títulos decorativos (Estético, no afecta lógica)
                Text(
                    text = "Define tu Meta",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "Fija los pasos y bloquea las distracciones",
                    fontSize = 14.sp,
                    color = Color(0xFFAAAAAA),
                    modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
                )

                TextFieldComponent(text = text, maxChars = maxChars, onValueChange = { text = it })

                Spacer(modifier = Modifier.size(40.dp))

                StartActivityButton(text, context, selectedApps, prefs)
            }
        }
    }
}

@Composable
fun TextFieldComponent(text: String, maxChars: Int, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            if (newText.length <= maxChars && newText.all { it.isDigit() }) {
                onValueChange(newText)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text("Introduce la cantidad de pasos deseada") },
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        // Colores personalizados: Rojo en reposo, Verde al escribir
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF43A047), // Verde
            unfocusedBorderColor = Color(0xFFE53935).copy(alpha = 0.7f), // Rojo suave
            focusedLabelColor = Color(0xFF43A047),
            unfocusedLabelColor = Color(0xFFAAAAAA),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color(0xFF43A047)
        )
    )
}

@Composable
fun StartActivityButton(pasos: String, context: Context, selectedApps: List<String>, prefs: SharedPreferences) {
    Button(
        onClick = {
            iniciarActividad(pasos, context = context, selectedApps, prefs = prefs)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp), // Quitamos el padding por defecto para que el degradado ocupe todo
        shape = RoundedCornerShape(16.dp)
    ) {
        // Caja interior con el degradado Rojo -> Verde
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFE53935), // Rojo
                            Color(0xFF43A047)  // Verde
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "COMENZAR ACTIVIDAD",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.5.sp
            )
        }
    }
}
fun iniciarActividad(pasos: String, context: Context, selectedApps: List<String>,
                     prefs: SharedPreferences) {

    if (pasos.isEmpty() || pasos.toInt() < 20)  {
        Toast.makeText(context,
            "Meta de pasos vacía o demasiado pequeña (menor de 20)",
            Toast.LENGTH_SHORT)
            .show()
        return
    }

    try {

        //Lógica de enviar al reloj la meta de pasos para comenzar la actividad
        val messageClient = Wearable.getMessageClient(context)
        val capabilityClient  = Wearable.getCapabilityClient(context)


        capabilityClient.getCapability(
            "lockgo_wear_app",
            CapabilityClient.FILTER_REACHABLE
        ).addOnSuccessListener { capabilityInfo ->

           val nodes = capabilityInfo.nodes

           if (nodes.isEmpty()) {
               Toast.makeText(
                   context,
                   "Error. No se ha detectado ningún SmartWatch con Lock&Go instalado",
                   Toast.LENGTH_SHORT
               ).show()
               return@addOnSuccessListener
           }

            // Guardamos las apps a bloquear temporalmente en SharedPreferences
            // para que el servicio pueda leerlas cuando el reloj responda
            prefs.edit().putStringSet("apps_temporales", selectedApps.toSet()).apply()

            for (node in nodes) {
                messageClient.sendMessage(node.id, "/start_activity", pasos.toByteArray())
                .addOnSuccessListener {
                    // NO iniciamos el bloqueo aquí. Solo avisamos que se ha enviado.
                    Toast.makeText(context, "Enviando meta al reloj...", Toast.LENGTH_SHORT).show()
                    Log.d("start", "Mensaje para iniciar el servicio de conteo de pasos enviado")
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al comunicar con el reloj.", Toast.LENGTH_SHORT).show()
                }
            }

        }.addOnFailureListener {
            Toast.makeText(
                context,
                "Error al comprobar conexión",
                Toast.LENGTH_SHORT
            ).show()
        }


    }catch (e: Exception) {
        e.printStackTrace()
    }

}

fun iniciarBloqueo(context: Context, selectedApps: List<String>) {

    val intent = Intent(context, AppBlockingService::class.java)

    intent.putStringArrayListExtra("apps_bloqueadas",
        selectedApps.toCollection(ArrayList()))

    val notificationManager = context.getSystemService(NotificationManager::class.java)

    //Tras iniciar la nueva actividad, cancelo cualquier notificación residual que pueda haber de la antigua
    notificationManager.cancelAll()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }

}