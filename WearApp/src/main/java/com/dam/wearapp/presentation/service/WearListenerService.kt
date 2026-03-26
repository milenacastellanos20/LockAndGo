package com.dam.wearapp.presentation.service

import android.content.Intent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearListenerService: WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {

        if (messageEvent.path == "/start_activity") {

            //Si el mensaje recibido contiene el path que le hemos
            //pasado desde la app móvil, entonces decodificamos
            //los datos del mensaje (el array de bytes que contienen la meta de pasos)
            val metaPasos = String(messageEvent.data).toInt()

            //Al tener la meta de pasos, lanzamos la actividad, que registra los pasos
            //para que empiece a contar desde ese mismo momento (el objetivo comienza)
            val intent = Intent(this, StepCounterManager::class.java).apply {

                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("META_PASOS", metaPasos)
                putExtra("RESET_STEPS", true)

            }

            startActivity(intent)

        }

    }




}