package com.dam.lockgo.presentation

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.dam.lockgo.domain.usecase.CheckPermissionsUseCase

class PermissionsViewModel(application: Application) : AndroidViewModel(application) {

    // Instanciamos el UseCase pasándole el contexto de la aplicación
    private val checkPermissionsUseCase = CheckPermissionsUseCase(application)

    // El estado que la UI (Compose) estará observando
    var uiState by mutableStateOf(checkPermissionsUseCase())
        private set

    /**
     * Se llama cada vez que el usuario vuelve de la pantalla de Ajustes
     * o pulsa un botón de permiso.
     */
    fun checkPermissions() {
        uiState = checkPermissionsUseCase()
    }

    /**
     * Función de conveniencia para saber si podemos navegar a la Home
     */
    fun areAllPermissionsGranted(): Boolean {
        return uiState.isAllGranted
    }
}