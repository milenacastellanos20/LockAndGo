package com.dam.wearapp.presentation.screens

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.dam.wearapp.R
import com.dam.wearapp.presentation.service.DatosViewModel
import com.google.android.gms.wearable.Wearable

/**
 * Método que pintará todos los elementos que conformarán la interfaz de la App en el reloj Wear OS
 * @author Hugo Garrido Rojo
 */

@Composable
fun ObjetivoScreen(viewModel: DatosViewModel) {

    val context = LocalContext.current

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
                    SyncButtonComponent(steps, goal, viewModel, context)
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
fun SyncButtonComponent(steps: Int, goal: Int, viewModel: DatosViewModel,
                        context: Context) {

    Button(onClick = {
        EndActivity(steps, goal, viewModel, context)
    },
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Sincronizar con el móvil")
    }

}

fun EndActivity(steps: Int, goal: Int, viewModel: DatosViewModel, context: Context) {

        if (steps != goal) {
            Toast.makeText(context,
                "¡Todavía no has completado tu meta de pasos!",
                Toast.LENGTH_SHORT)
                .show()
            return
        }

        val goalReached = true

        try {

            val messageClient = Wearable.getMessageClient(context)

            Wearable.getNodeClient(context).connectedNodes.addOnSuccessListener { nodes ->

                for (node in nodes) {

                    messageClient.sendMessage(node.id,
                        "/end_activity",
                        goalReached.toString().toByteArray())

                }

                Toast.makeText(context,
                    "Actividad finalizada",
                    Toast.LENGTH_SHORT)
                    .show()

            }

            viewModel.reiniciarDatos()

        }catch (e: Exception) {
            e.printStackTrace()
        }


}