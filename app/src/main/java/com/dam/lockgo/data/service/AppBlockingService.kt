package com.dam.lockgo.data.service

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.dam.lockgo.ui.screens.BlockingScreen
import kotlin.jvm.java

class AppBlockingService : AccessibilityService() {

    private lateinit var appsBloqqueadas: ArrayList<String>
    private lateinit var notificationManager: NotificationManager

    private val endActivityReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("LockAndGo", "Recibido intent de finalizar actividad")
            finalizarActividad()
        }
    }
    private val CHANNEL_ID = "app_blocking_channel"
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Servicio de bloqueo",
                NotificationManager.IMPORTANCE_LOW
            )
            serviceChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onCreate() {
        super.onCreate()

        ContextCompat.registerReceiver(
            this,
            endActivityReciever,
            IntentFilter("finalizar_actividad"),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        //Inicializo la variable que contendrá el package name de las apps bloqueadas a una lista vacía
        //por el momento para que no crashee la app, y ya después que reciba el valor correspondiente
        //al iniciar el servicio. Esto se tiene que hacer así dado a que cómo es un servicio de accesibilidad,
        //el servicio se crea nada más dar permisos de accesibilidad. No es cómo el servicio de los pasos, por ejemplo
        //que es un servicio común. El servicio de pasos se crea en el mismo momento en el que se inicia el mismo servicio
        appsBloqqueadas = ArrayList()
        notificationManager = getSystemService(NotificationManager::class.java)
        createNotificationChannel()
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
                Log.d("LockAndGo", "App bloqueada: $packageName")
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
        var bloqueoLanzado = startActivity(intent)

        Log.d("LockAndGo", "Pantalla de bloqueo lanzada: $bloqueoLanzado")

    }

    fun finalizarActividad() {
        val serviceFinishedNotification =
            NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Objetivo finalizado")
            .setContentText("Lock&Go ha desbloqueado las aplicaciones")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
        Log.d("LockAndGo", "Paso 1")

        notificationManager.notify(1, serviceFinishedNotification)
        Log.d("LockAndGo", "Paso 2")

        appsBloqqueadas = ArrayList()
        Log.d("LockAndGo", "Actividad finalizada")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            appsBloqqueadas = intent.getStringArrayListExtra("apps_bloqueadas") as ArrayList<String>
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Objetivo en marcha")
            .setContentText("Lock&Go ahora está bloqueando las apps seleccionadas")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        startForeground(1, notification)

        return START_STICKY
    }
}