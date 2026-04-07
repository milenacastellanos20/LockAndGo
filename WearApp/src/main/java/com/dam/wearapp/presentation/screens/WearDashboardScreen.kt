package com.dam.wearapp.presentation.screens

import android.app.Application
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
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

    var estadoActual = viewModel.hayObjetivo

    AnimatedContent(
        targetState = estadoActual,
        transitionSpec = {

            //Si la transición es de la pantalla de "Sin Objetivo"
            // a la pantalla de cuando hay objetivo, entonces deslizaremos
            //de izquierda a derecha y viceversa
            if (estadoActual && !initialState) {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) togetherWith slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            } else {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                ) togetherWith slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }

        },
        label = "AnimatedContent",
    ) { estado ->

        if (estado) {
            ObjetivoScreen(viewModel)
        } else {
            SinObjetivoScreen()
        }

    }

}


