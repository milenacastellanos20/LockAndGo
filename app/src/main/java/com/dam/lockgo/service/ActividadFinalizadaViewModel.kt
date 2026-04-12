package com.dam.lockgo.service

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class ActividadFinalizadaViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = application.getSharedPreferences("LockAndGoPrefs", MODE_PRIVATE)

    var actividadFinalizada by mutableStateOf(prefs.getBoolean("actividad_finalizada", true))

    private val listener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

            when (key) {
                "actividad_finalizada" -> {
                    actividadFinalizada = sharedPreferences.getBoolean(key, true)
                    Log.d("Estado actividad cambiado", "Estado actual: $actividadFinalizada")
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


