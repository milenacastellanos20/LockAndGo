package com.dam.lockgo.service

import android.content.Intent
import android.widget.Toast
import com.dam.lockgo.data.CompletedActivity
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.dam.lockgo.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import com.dam.lockgo.ui.screens.iniciarBloqueo
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.first


class MobileListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {

        val prefs = getSharedPreferences("LockAndGoPrefs", MODE_PRIVATE)

        if (messageEvent.path == "/ack_start") {

            Log.d("ack", "Mensaje para iniciar el servicio de bloqueo recibido")

            val selectedApps = prefs.getStringSet("apps_temporales", emptySet())?.toList() ?: emptyList()

            iniciarBloqueo(this, selectedApps)

            prefs.edit().putBoolean("actividad_finalizada", false).apply();

            // Mostramos el Toast (necesita el Main Looper porque estamos en un servicio de fondo)
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                Toast.makeText(
                    this,
                    "Actividad iniciada en el reloj. Bloqueo activado.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


        if (messageEvent.path == "/end_activity") {

            android.os.Handler(android.os.Looper.getMainLooper()).post {
                Toast.makeText(
                    this,
                    "Meta de pasos completada",
                    Toast.LENGTH_SHORT
                ).show()
            }

            prefs.edit()
                .putBoolean("actividad_finalizada", true)
                .putBoolean("recompensa_pendiente", true)
                .apply()

            detenerServicioBloqueo()

            val goal = String(messageEvent.data).toInt()

            guardarActividadCompletada(goal)

            imprimirHistorial()

            val messageClient = Wearable.getMessageClient(this)
            messageClient.sendMessage(
                messageEvent.sourceNodeId,
                "/ack_end",
                ByteArray(0)
            ).addOnSuccessListener {
                Log.d("MobileListener", "ACK de fin enviado al reloj.")
            }
        }

    }

    fun detenerServicioBloqueo() {
        val intent = Intent("finalizar_actividad").apply {
            setPackage(packageName)
        }

        sendBroadcast(intent)
    }

    fun guardarActividadCompletada(goal: Int) {

        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val fechaActual = formatoFecha.format(Date())

        val nuevaActividadCompletada = CompletedActivity(
            date = fechaActual,
            stepGoal = goal
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                db.lockAndGoDao().saveCompletedActivity(nuevaActividadCompletada)

                Log.d("RoomOK", "Se ha guardado en el historial: $fechaActual - Meta: $goal")
            } catch (e: Exception) {
                Log.e("RoomError", "No se pudo guardar la actividad: ${e.message}")
            }
        }
    }

    private fun imprimirHistorial() {
        // Usamos Dispatchers.IO porque leer de la DB es una operación de entrada/salida
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                // .first() toma la lista actual del Flow y deja de escuchar
                val lista = db.lockAndGoDao().getAllCompletedActivities().first()

                if (lista.isEmpty()) {
                    Log.d("ConsultaRoom", "La base de datos está vacía.")
                } else {
                    lista.forEach { actividad ->
                        Log.d(
                            "ConsultaRoom",
                            "ID: ${actividad.id} | Fecha: ${actividad.date} | Meta: ${actividad.stepGoal}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("RoomError", "Error al consultar: ${e.message}")
            }
        }
    }

}