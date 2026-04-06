package com.dam.lockgo.service

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.dam.lockgo.data.service.AppBlockingService
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class MobileListenerService: WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {

        if (messageEvent.path == "/end_activity") {
            Toast.makeText(this,
                "Meta de pasos completada",
                Toast.LENGTH_SHORT)
                .show()
            val prefs = getSharedPreferences("LockAndGoPrefs", MODE_PRIVATE)
            prefs.edit().putBoolean("actividad_finalizada", true).apply()

            detenerServicioBloqueo()
        }

    }

    fun detenerServicioBloqueo() {
        val intent = Intent("finalizar_actividad").apply {
            setPackage(packageName)
        }

        sendBroadcast(intent)
    }

}