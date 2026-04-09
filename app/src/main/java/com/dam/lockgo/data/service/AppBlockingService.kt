package com.dam.lockgo.data.service

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dam.lockgo.ui.screens.BlockingScreen
import kotlin.jvm.java

class AppBlockingService : AccessibilityService() {

    private lateinit var appsBloqqueadas: ArrayList<String>

    private val notificationManager = getSystemService(NotificationManager::class.java)
    private val CHANNEL_ID = "app_blocking_channel"
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Servicio de Pasos",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Objetivo en marcha")
            .setContentText("Lock&Go ahora está bloqueando las apps seleccionadas")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
        startForeground(1, notification)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("LockAndGo", "Servicio de accesibilidad conectado y listo.")
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return

            Log.d("LockAndGo", "App en primer plano: $packageName")

            //esta en la lista negra y no ha cumplido los pasos ?
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
        return appsBloqqueadas.contains(packageName)
    }

    private fun lanzarPantallaBloqueo(blockedPackage: String) {
        //lanzamos la pantalla de jetpack compose que bloquea el acceso
        val intent = Intent(this, BlockingScreen::class.java).apply {
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

    fun servicioFinalizado(finalizado: Boolean) {

        if (finalizado) {

            val serviceFinishedNotification =
                NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Objetivo finalizado")
                .setContentText("Lock&Go ha desbloqueado las aplicaciones")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()

            notificationManager.notify(2, serviceFinishedNotification)

            stopSelf()
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            appsBloqqueadas = intent.getStringArrayListExtra("apps_bloqueadas") as ArrayList<String>
        }

        return START_STICKY
    }
}