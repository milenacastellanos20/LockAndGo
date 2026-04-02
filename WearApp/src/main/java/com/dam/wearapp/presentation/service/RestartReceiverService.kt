package com.dam.wearapp.presentation.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


//Esta clase la dejo por si se reutiliza en un futuro (no he conseguido que el servicio
//en segundo plano se reactive)
class RestartReceiverService: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val restartIntent = Intent(context, StepCounterManager::class.java)

        context.startForegroundService(restartIntent)

        Toast.makeText(context,
            "Servicio reiniciado. Todavía no has completado tu meta de pasos",
            Toast.LENGTH_SHORT)
            .show()

    }
}