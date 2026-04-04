package com.dam.lockgo.service

import android.widget.Toast
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class MobileListenerService: WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {

        if (messageEvent.path == "/end_activity") {
            Toast.makeText(this,
                "Meta de pasos completada: ${String(messageEvent.data)}",
                Toast.LENGTH_SHORT)
                .show()
        }

    }

}