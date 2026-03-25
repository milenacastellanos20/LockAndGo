package com.dam.lockgo.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.wearable.Wearable

@Preview
@Composable
fun StartActivityScreen(
    onBack: () -> Unit = {}
) {

    val context = LocalContext.current
    //Variables para el TextField
    var text by remember { mutableStateOf("") }
    val maxChars = 5

    //Variable para el botón
    var isPasos by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { TopBarComponent(onBackClick = { onBack() }) },
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()
                                .padding(innerPadding),
                        contentAlignment = Alignment.Center)
        {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TextFieldComponent(text = text, maxChars = maxChars, onValueChange = { text = it })

                Spacer(modifier = Modifier.size(20.dp))

                StartActivityButton(text, hayPasos = { isPasos = it }, context)

                if (!isPasos) {
                    SinPasosAviso()
                }

            }
        }


    }

}
@Composable
fun TextFieldComponent(text: String, maxChars: Int,onValueChange: (String) -> Unit) {

    TextField(
        value = text,
        onValueChange = { newText ->

            if (newText.length <= maxChars && newText.all { it.isDigit() }) {
                onValueChange(newText)
            }

        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text("Introduce la cantidad de pasos deseada") },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    )
}

@Composable
fun StartActivityButton(pasos: String, hayPasos: (Boolean) -> Unit, context: Context) {

    Button(
        onClick = {
            iniciarActividad(pasos, hayPasos, context = context)
        }
    ) {
        Text(text = "Comenzar actividad")
    }

}

@Composable
fun SinPasosAviso() {

    Text(
        text = "¡No has introducido la meta de pasos!",
        color = Color.Red
    )

}

fun iniciarActividad(pasos: String, hayPasos: (Boolean) -> Unit, context: Context) {

    if (pasos.isEmpty()) {
        hayPasos(false)
        return
    }

    hayPasos(true)

    try {
        //Lógica de enviar al reloj la meta de pasos para comenzar la actividad
        val messageClient = Wearable.getMessageClient(context)

        Wearable.getNodeClient(context).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                messageClient.sendMessage(node.id,
                    "/start_activity",
                    pasos.toByteArray())
            }
        }


    }catch (e: Exception) {
        e.printStackTrace()
    }

}