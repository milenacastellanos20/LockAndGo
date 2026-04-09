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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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

    //Variable para el botón
    var isPasos by remember { mutableStateOf(true) }

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

                StartActivityButton(text, hayPasos = { isPasos = it }, context, selectedApps, prefs)

                if (selectedApps.isNotEmpty()) {

                    Text(selectedApps.toString())

                }

                if (!isPasos) {
                    SinPasosAviso()
                }

            }
        }


    }

}
@Composable
fun TextFieldComponent(text: String, maxChars: Int,onValueChange: (String) -> Unit) {

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
fun StartActivityButton(pasos: String, hayPasos: (Boolean) -> Unit, context: Context, selectedApps: List<String>,
                        prefs: SharedPreferences) {

    Button(
        onClick = {
            iniciarActividad(pasos, hayPasos, context = context, selectedApps, prefs = prefs)
        }
    ) {
        Text(text = "Comenzar actividad")
    }

}

@Composable
fun SinPasosAviso() {

    Text(
        text = "¡No has introducido la meta de pasos o la meta es demasiado pequeña (menos de 20)!",
        style = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        color = Color.Red
    )

}

fun iniciarActividad(pasos: String, hayPasos: (Boolean) -> Unit, context: Context, selectedApps: List<String>,
                     prefs: SharedPreferences) {

    if (pasos.isEmpty() || pasos.toInt() < 20)  {
        hayPasos(false)
        return
    }

    hayPasos(true)

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

            prefs.edit().putBoolean("actividad_finalizada", false).apply()

            Toast.makeText(context, "Datos enviados correctamente",
                Toast.LENGTH_SHORT).show()

        }

        iniciarBloqueo(context, selectedApps)

    }catch (e: Exception) {
        e.printStackTrace()
    }

}

fun iniciarBloqueo(context: Context, selectedApps: List<String>) {

    val intent = Intent(context, AppBlockingService::class.java)

    intent.putStringArrayListExtra("apps_bloqueadas",
        selectedApps.toCollection(ArrayList()))

    val notificationManager = context.getSystemService(NotificationManager::class.java)

    //Antes de iniciar la nueva actividad, cancelo cualquier notificación residual que pueda haber
    notificationManager.cancelAll()

    val servicioBloqueoIniciado = Toast.makeText(context,
        "Servicio de bloqueo iniciado",
        Toast.LENGTH_SHORT)


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        var servicio = context.startForegroundService(intent)

        var iniciadoServicio = servicio != null

        if (iniciadoServicio) {
            servicioBloqueoIniciado.show()
        }

        Log.d("Servicio bloqueo iniciado?", iniciadoServicio.toString())
    } else {
        var servicio = context.startService(intent)

        var iniciadoServicio = servicio != null

        if (iniciadoServicio) {
            servicioBloqueoIniciado.show()
        }

        Log.d("Servicio bloqueo iniciado?", iniciadoServicio.toString())
    }

}