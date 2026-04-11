package com.dam.wearapp.presentation.service

import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearListenerService: WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {

        Log.d("Mensaje recibido", "Path recibido: ${messageEvent.path}")

        if (messageEvent.path == "/start_activity") {

            Toast.makeText(this, "Meta de pasos recibida",
                Toast.LENGTH_SHORT).show()

            //Si el mensaje recibido contiene el path que le hemos
            //pasado desde la app móvil, entonces decodificamos
            //los datos del mensaje (el array de bytes que contienen la meta de pasos)
            val metaPasos = String(messageEvent.data).toInt()

            //Al tener la meta de pasos, lanzamos la actividad, que registra los pasos
            //para que empiece a contar desde ese mismo momento (el objetivo comienza)
            val intent = Intent(this, StepCounterManager::class.java).apply {

                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("META_PASOS", metaPasos)

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var servicio = startForegroundService(intent)

                var iniciadoServicio = servicio != null

                Log.d("Servicio iniciado?", iniciadoServicio.toString())
            } else {
                var servicio = startService(intent)

                var iniciadoServicio = servicio != null

                Log.d("Servicio iniciado?", iniciadoServicio.toString())
            }

        }

    }

}