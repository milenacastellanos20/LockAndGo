package com.dam.wearapp.presentation.screens

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.dam.lockgo.R
import com.dam.wearapp.presentation.service.DatosViewModel
import com.google.android.gms.wearable.CapabilityClient
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

        if (steps < goal) {
            Toast.makeText(context,
                "¡Todavía no has completado tu meta de pasos!",
                Toast.LENGTH_SHORT)
                .show()
            return
        }

        try {

            val messageClient = Wearable.getMessageClient(context)
            val capabilityClient = Wearable.getCapabilityClient(context)

            capabilityClient.getCapability(
                "lockgo_mobile_app",
                CapabilityClient.FILTER_REACHABLE
            ).addOnSuccessListener { capabilityInfo ->

                val nodes = capabilityInfo.nodes

                if (nodes.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Error. No se ha detectado ningún móvil con Lock&Go instalado",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                for (node in nodes) {
                    messageClient.sendMessage(node.id, "/end_activity", goal.toString().toByteArray())
                        .addOnSuccessListener {
                            // SOLO reiniciamos los datos si el mensaje se ha enviado con éxito al móvil
                            Toast.makeText(context,
                                "Sincronizando con el móvil...",
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context,
                                "Error al enviar datos al móvil",
                                Toast.LENGTH_SHORT).show()
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "Error al comprobar conexión",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }catch (e: Exception) {
            e.printStackTrace()
        }


}