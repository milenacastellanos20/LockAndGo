package com.dam.wearapp.presentation.screens

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dam.wearapp.presentation.service.DatosViewModel

/**
 * Método que pintará todos los elementos que conformarán la interfaz de la App en el reloj Wear OS
 * @author Hugo Garrido Rojo
 */
@Composable
fun WearDashboardScreen() {

    val application = LocalContext.current.applicationContext as Application

    val viewModel: DatosViewModel =
                            viewModel(factory = ViewModelProvider
                                                .AndroidViewModelFactory
                                                .getInstance(application))

    Log.d("meta", "Meta de pasos antes de iniciar: ${viewModel.meta}")
    if (viewModel.hayObjetivo) {
        ObjetivoScreen(viewModel)
    } else {
        SinObjetivoScreen()
    }

}