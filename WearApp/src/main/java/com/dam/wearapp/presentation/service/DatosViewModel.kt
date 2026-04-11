package com.dam.wearapp.presentation.service

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DatosViewModel (application: Application): AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("pasos_prefs",
        Context.MODE_PRIVATE)

    //Obtención de variables
    var meta by mutableStateOf(prefs.getInt("meta_pasos", 0))
    val hayObjetivo: Boolean get() = meta > 0
    var pasosActuales by mutableStateOf(prefs.getInt("ultimos_pasos_calculados_registrados", 0))
        private set

    //Hago una variable de tipo NotificationManager para poder quitar todas las notificaciones
    //de la aplicación al finalizar la actividad y que no se solapen con las de una futura
    private val notificationManager = application.getSystemService(NotificationManager::class.java)
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        when (key) {
            "meta_pasos" -> {
                meta = sharedPreferences.getInt(key, 0)
            }
            "ultimos_pasos_calculados_registrados" -> {
                pasosActuales = sharedPreferences.getInt(key, 0)

                Log.d("Pasos", "Pasos actualizados: $pasosActuales")

                //Hago esta comprobación para que en el caso de que
                //el servicio no se haya matado a tiempo y haya contado
                //algún paso de más, entonces los pasos que se muestren
                //en la UI se ajusten a la meta
                if (pasosActuales > meta) {

                    //Vuelvo a guardar en las SharedPreferences el valor igualado a la meta.
                    //Si no hago esto, a la hora de cerrar y volver a abrir la aplicación, se cogerá
                    //el último valor guardado en las SharedPreferences y se mostrará en la UI
                    //(pudiendo ser mayor de la meta propuesta)
                    prefs.edit().putInt("ultimos_pasos_calculados_registrados", meta).apply()

                    //Pongo que la meta está cumplida y así el servicio no se vuelva a activar
                    prefs.edit().putBoolean("meta_cumplida", true).apply()

                    Log.d("Meta", "Los pasos se han igualado a la meta. Pasos actuales: $pasosActuales")

                }
            }
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun reiniciarDatos() {

        viewModelScope.launch {

            //Pauso el hilo durante un segundo antes de eliminar
            //las SharedPreferences para que el cambio de pantalla
            //no se sienta tan abrupto

            delay(1000)

            //Actualizo la shared preference que determina si hay meta o no
            //para que Compose cambie inmediatamente a la pestaña de
            //"Ningún objetivo asignado"

            //Pongo el valor de la meta en 0 para que así la variable booleana
            //que comprueba si hay objetivo o no vuelva a ser false
            //y se actualice la pantalla en tiempo real en el caso de que la
            //aplicación esté en primer plano
            prefs.edit().putInt("meta_pasos", 0).apply()

            //Pongo el valor de los pasos en 0 de la UI en 0 para que si se recibe
            //un nuevo objetivo con la app en primer plano, se inicie con el valor en 0
            //y no con el valor de pasos antiguo. En el caso de que la aplicación esté cerrada
            //y se abra después de recibir la actividad, el valor de los pasos iniciará a 0,
            //pues se han creado las SharedPreferences nuevamente
            prefs.edit().putInt("ultimos_pasos_calculados_registrados", 0).apply()

            //Eliminamos todas las SharedPreferences para que la lógica
            //sea correcta en todas las actividades que se inicien
            prefs.edit().clear().apply()

            //Elimino las notificaciones que se sigan mostrando (la de meta cumplida)
            notificationManager.cancelAll()

        }

    }

    override fun onCleared() {
        super.onCleared()
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

}