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

    //Creamos nuestra variable de pasos que se mostrarán en la UI
    //(los pasos desde que se inició la actividad)
    private var steps = 0 //la inicializamos a 0, por ejemplo
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

            //Pasos totales del sensor (pasos registrados desde que se inició el reloj)
            val totalSteps = event.values[0].toInt()

            //Creamos u obtenemos el archivo de las SharedPreferences
            val prefs = getSharedPreferences("pasos_prefs", Context.MODE_PRIVATE)

            //Obtenemos la meta de pasos que hemos establecido a partir de los datos del Intent
            val meta = prefs.getInt("meta_pasos", 5000)

            //Obtenemos la variable booleana que hemos establecido a partir de los datos del Intent
            //para saber si tenemos que resetear los pasos o no
            val resetSteps = prefs.getBoolean("reset_steps", false)

            if (resetSteps) {
                //A esta variable le restamos el último valor guardado
                //en las SharedPreferences (equivalente al valor actual de la variable),
                //dejándola en 0
                steps = 0

                //Ahora, lo guardamos
                prefs.edit().putInt("ultimos_pasos_calculados_registrados", steps).apply()

                //También guardamos el último valor del sensor registrado
                //Esto lo utilizaremos en el futuro cómo el valor de pasos antiguo
                prefs.edit().putInt("ultimos_pasos_sensor_registrados", totalSteps).apply()

                //Cambiamos el valor de la variable booleana a false para que
                //no se vuelva a ejecutar este bloque de código
                prefs.edit().putBoolean("reset_steps", false).apply()

            } else {

                val valorAnterior = prefs.getInt("ultimos_pasos_sensor_registrados", 0)

                steps += totalSteps - valorAnterior

                //Guardamos el nuevo valor de los pasos a mostrar en la UI en las SharedPreferences
                prefs.edit().putInt("ultimos_pasos_calculados_registrados", steps).apply()

                //Finalmente, guardamos el nuevo valor de pasos totales en las SharedPreferences
                prefs.edit().putInt("ultimos_pasos_sensor_registrados", totalSteps).apply()

            }

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

        intent?.let {

            val metaPasos = intent.getIntExtra("META_PASOS", 0)
            val resetSteps = intent.getBooleanExtra("RESET_STEPS", false)

            //Creamos u obtenemos el archivo de las SharedPreferences
            val prefs = getSharedPreferences("pasos_prefs", Context.MODE_PRIVATE)

            prefs.edit().putInt("meta_pasos", metaPasos).apply()
            prefs.edit().putBoolean("reset_steps", resetSteps).apply()

        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

}