package com.dam.wearapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingParams
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import java.sql.Time

/**
 * Método que pintará todos los elementos que conformarán la interfaz de la App en el reloj Wear OS
 * @author Hugo Garrido Rojo
 */
@Composable
fun WearDashboardScreen(pasos: Int, goal: Int) {

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
                        circularProgressIndicatorComponent(pasos, goal)
                        stepsInformationComponent(pasos, goal)
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
fun circularProgressIndicatorComponent(pasos: Int, goal: Int) {
    CircularProgressIndicator(
        modifier = Modifier.size(180.dp),
        progress = pasos.toFloat() / goal.toFloat(),
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
fun stepsInformationComponent(pasos: Int, goal: Int) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Pasos")
        Spacer(Modifier.size(10.dp))
        Text(text = pasos.toString())
        Spacer(Modifier.size(10.dp))
        Text(text = "Objetivo")
        Spacer(Modifier.size(10.dp))
        Text(text = goal.toString())
    }


}

/**
 * Método que pintará el botón de sincronizar para que el usuario sincronice su progreso con la app móvil al instante
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

