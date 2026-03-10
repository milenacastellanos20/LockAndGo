package com.dam.lockgo.services

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.content.Intent
import android.util.Log
import kotlin.jvm.java

class AppBlockingService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("LockAndGo", "Servicio de accesibilidad conectado y listo.")
        // TODO: Cargar desde Room las apps a bloquear
        // y el estado actual de los pasos para comprobarlo
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        //solo cuando se cambia de ventana (se abre la app)
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            Log.d("LockAndGo", "App en primer plano: $packageName")

        }
    }

    override fun onInterrupt() {
        //llamado cuando el servicio de accesibilidad es interrumpido
        Log.d("LockAndGo", "Servicio de accesibilidad interrumpido.")
    }





}