package com.dam.wearapp.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class StepCounterManager: Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private val CHANNEL_ID = "step_counter_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Registramos el sensor para que escuche siempre
        start()

        // Lanzamos la notificación para que el servicio sea "inmortal"
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Contando pasos...")
            .setContentText("Tu objetivo está en marcha")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()

        startForeground(1, notification)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        //Para que sólamente cuente los pasos
        //y no tenga en cuenta otras medidas
        //que puedan resultar en más pasos

        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {

            val totalSteps = event.values[0].toInt()

            //Creamos el archivo de las SharedPreferences
            val prefs = getSharedPreferences("pasos_prefs", Context.MODE_PRIVATE)

            //Guardamos el último valor
            prefs.edit().putInt("ultimo_valor_registrado", totalSteps).apply()

            //Lógica de notificación
            val meta = prefs.getInt("meta_pasos", 10000)

            val yaAvisado = prefs.getBoolean("notificacion_enviada", false)

            if (totalSteps >= meta && !yaAvisado) {

                val notificationManager = getSystemService(NOTIFICATION_SERVICE)
                                                            as NotificationManager


                val congratsNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("¡Felicidades!")
                    .setContentText("Has alcanzado la meta de $meta pasos")
                    .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .build()

                notificationManager.notify (2, congratsNotification)

                prefs.edit().putBoolean("notificacion_enviada", true).apply()

                //Para que deje de escuchar los pasos
                //del sensor (ya no es necesario)
                //dado a que se ha alcanzado la meta
                stop()

                //Para matar el servicio por completo
                //y que la notificación de que el objetivo
                //está en curso desaparezca, además de
                //ahorrar batería
                stopSelf()
            }


        }
    }

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

    fun start() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }
    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

}