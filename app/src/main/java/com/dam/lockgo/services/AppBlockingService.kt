package com.dam.lockgo.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
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

            //esta en la lista negra y no ha cumplido los pasos ? Bloqueo por gordaaaaaaaaaaa ADELGAZAAA
            if (bloquearApp(packageName)) {
                lanzarPantallaBloqueo(packageName)
            }

        }
    }

    override fun onInterrupt() {
        //llamado cuando el servicio de accesibilidad es interrumpido
        Log.d("LockAndGo", "Servicio de accesibilidad interrumpido.")
    }

    private fun bloquearApp(packageName: String): Boolean {

        //habra que pasarle la base de datos( room)
        val appsBloqueadas = listOf("com.whatsapp", "com.instagram.android")
        val pasosCompletados = false; // viene de Datos/sensores

        return appsBloqueadas.contains(packageName) && !pasosCompletados
    }

    private fun lanzarPantallaBloqueo(blockedPackage: String) {
        //lanzamos la pantalla de jetpack compose que bloquea el acceso
        val intent = Intent(this, BlockingActivity::class.java).apply {
            // Estos flags para que la pantalla de bloqueo se ponga por encima
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or //inicia la actividad en una nueva tarea

                        Intent.FLAG_ACTIVITY_CLEAR_TASK or //borra la instancia previa (evita que el usuario vuelva a ella)

                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or //excluye la actividad de las recientes (que no aparezca jiji)

                        Intent.FLAG_ACTIVITY_NO_ANIMATION //quita la animación de inicio
            )
            putExtra("BLOCKED_PACKAGE", blockedPackage)
        }
        startActivity(intent)
    }

}
