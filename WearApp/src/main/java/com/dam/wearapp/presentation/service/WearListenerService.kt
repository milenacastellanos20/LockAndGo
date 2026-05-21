package com.dam.wearapp.presentation.service

import android.app.Application
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

class WearListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {

        Log.d("Mensaje recibido", "Path recibido: ${messageEvent.path}")

        if (messageEvent.path == "/ack_end") {

            // Limpiamos las SharedPreferences locales del reloj
            val viewModel = DatosViewModel(application)

            viewModel.reiniciarDatos()

            android.os.Handler(android.os.Looper.getMainLooper()).post {
                Toast.makeText(
                    this,
                    "Actividad finalizada",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        if (messageEvent.path == "/start_activity") {

            Toast.makeText(
                this, "Meta de pasos recibida",
                Toast.LENGTH_SHORT
            ).show()

            // Si el mensaje recibido contiene el path que le hemos
            // pasado desde la app móvil, entonces decodificamos
            // los datos del mensaje (el array de bytes que contienen la meta de pasos)
            val metaPasos = String(messageEvent.data).toInt()

            // Al tener la meta de pasos, lanzamos la actividad, que registra los pasos
            // para que empiece a contar desde ese mismo momento (el objetivo comienza)
            val intent = Intent(this, StepCounterManager::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("META_PASOS", metaPasos)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val servicio = startForegroundService(intent)
                val iniciadoServicio = servicio != null
                Log.d("Servicio iniciado?", iniciadoServicio.toString())
            } else {
                val servicio = startService(intent)
                val iniciadoServicio = servicio != null
                Log.d("Servicio iniciado?", iniciadoServicio.toString())
            }

            val messageClient = Wearable.getMessageClient(this)

            // ¡Aquí es donde van enganchados los listeners! Justo al terminar el sendMessage
            messageClient.sendMessage(
                messageEvent.sourceNodeId,
                "/ack_start",
                ByteArray(0)
            ).addOnSuccessListener {
                Log.d("WearListener", "Confirmación enviada al móvil con éxito.")
            }.addOnFailureListener {
                // Si el móvil no recibe la confirmación (ej. corte de Bluetooth),
                // el reloj detiene su propio servicio para evitar desincronizaciones raras.
                Log.e("WearListener", "Error al enviar confirmación al móvil. Abortando servicio.")

                val stopIntent = Intent(this, StepCounterManager::class.java)
                stopService(stopIntent)

                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    Toast.makeText(
                        this,
                        "Error de conexión con el móvil. Reinténtalo.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                val viewModel = DatosViewModel(application)
                viewModel.reiniciarDatos()
            }

        }

    }

}