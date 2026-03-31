package com.dam.wearapp.presentation.service

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DatosViewModel (application: Application): AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("pasos_prefs",
        Context.MODE_PRIVATE)

    //Obtención de variables
    var meta by mutableStateOf(prefs.getInt("meta_pasos", 0))
    val hayObjetivo: Boolean get() = meta > 0
    var pasosActuales by mutableStateOf(prefs.getInt("ultimos_pasos_calculados_registrados", 0))
        private set
    var yaAvisado by mutableStateOf(prefs.getBoolean("notificacion_enviada", false))
        private set
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        when (key) {
            "meta_pasos" -> {
                meta = sharedPreferences.getInt(key, 0)
            }
            "ultimos_pasos_calculados_registrados" -> {
                pasosActuales = sharedPreferences.getInt(key, 0)
            }
            "notificacion_enviada" -> {
                yaAvisado = sharedPreferences.getBoolean(key, false)
            }
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onCleared() {
        super.onCleared()
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

}