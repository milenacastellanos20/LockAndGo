package com.dam.wearapp.presentation.screens

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import androidx.compose.foundation.Image
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.dam.wearapp.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dam.wearapp.presentation.service.PasosViewModel

/**
 * Método que pintará todos los elementos que conformarán la interfaz de la App en el reloj Wear OS
 * @author Hugo Garrido Rojo
 */

@Composable
fun ObjetivoScreen(viewModel: PasosViewModel) {

    val applicaction = LocalContext.current.applicationContext as Application

    val steps = viewModel.pasosActuales
    val goal = viewModel.meta

    val listState = rememberScalingLazyListState()

    Scaffold(
        Modifier.background(Color.Black),
        timeText = {
            TimeText(
                modifier = Modifier.scrollAway(listState)
            )
        }
    ) {

        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
        ) {

            item {
                Spacer(modifier = Modifier.size(25.dp))
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                    content = {
                        CircularProgressIndicatorComponent(steps, goal)
                        StepsInformationComponent(steps, goal)
                    }

                )
            }

            item {
                Spacer(modifier = Modifier.size(50.dp))
            }

            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    SyncButtonComponent()
                }
            }

        }
    }
}

/**
 * Método que pintará la barra circular que indicará el progreso del usuario hasta completar el objetivo de pasos propuesto
 * @author Hugo Garrido Rojo
 */
@Composable
fun CircularProgressIndicatorComponent(steps: Int, goal: Int) {
    CircularProgressIndicator(
        modifier = Modifier
            .size(180.dp)
            .graphicsLayer(-1f),
        progress = steps.toFloat() / goal.toFloat(),
        startAngle = 270f,
        indicatorColor = Color.Green,
        strokeWidth = 15.dp
    )
}

/**
 * Método que pintará la cantidad de pasos que lleva el usuario
 * @author Hugo Garrido Rojo
 */
@Composable
fun StepsInformationComponent(steps: Int, goal: Int) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.icono_pasos),
            contentDescription = "Icono de pasos",
            modifier = Modifier.size(25.dp)
        )
        Text(text = "$steps/", fontSize = 20.sp)
        Text(text = "$goal pasos", fontWeight = androidx.compose.ui.
        text.font.FontWeight.Bold)
    }


}

/**
 * Método que pintará el botón de sincronizar para que el usuario sincronice su progreso con la app móvil al instante
 * @author Hugo Garrido Rojo
 */
@Composable
fun SyncButtonComponent() {

    Button(onClick = {
        //TODO
    },
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Sincronizar ahora")
    }

}