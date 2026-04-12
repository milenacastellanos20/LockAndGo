package com.dam.lockgo.ui.screens

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dam.lockgo.data.service.AppBlockingService
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

        Box(modifier = Modifier.fillMaxSize()
                                .padding(innerPadding),
                        contentAlignment = Alignment.Center)
        {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TextFieldComponent(text = text, maxChars = maxChars, onValueChange = { text = it })

                Spacer(modifier = Modifier.size(20.dp))

                StartActivityButton(text, context, selectedApps, prefs)

            }
        }


    }

}
@Composable
fun TextFieldComponent(text: String, maxChars: Int, onValueChange: (String) -> Unit) {

    TextField(
        value = text,
        onValueChange = { newText ->

            if (newText.length <= maxChars && newText.all { it.isDigit() }) {
                onValueChange(newText)
            }

        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text("Introduce la cantidad de pasos deseada") },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    )
}

@Composable
fun StartActivityButton(pasos: String, context: Context, selectedApps: List<String>,
                        prefs: SharedPreferences) {

    Button(
        onClick = {
            iniciarActividad(pasos, context = context, selectedApps, prefs = prefs)
        }
    ) {
        Text(text = "Comenzar actividad")
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

        Wearable.getNodeClient(context).connectedNodes.addOnSuccessListener { nodes ->

            Log.d("Función enviar datos ejecutada", "Nodos conectados: ${nodes.size}")

            for (node in nodes) {
                messageClient.sendMessage(node.id,
                    "/start_activity",
                    pasos.toByteArray())
            }

            prefs.edit().putBoolean("actividad_finalizada", false).putBoolean("actividad_en_curso", true).apply()

            Toast.makeText(context, "Actividad iniciada en el reloj",
                Toast.LENGTH_SHORT).show()

            //Sólamente se inicia el servicio de bloqueo si se han enviado los datos al reloj
            iniciarBloqueo(context, selectedApps)
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