package com.dam.lockgo.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun StartActivityScreen(
    onBack: () -> Unit = {}
) {

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
                TextFieldComponent()

                Spacer(modifier = Modifier.size(20.dp))

                StartActivityButton()
            }
        }


    }

}


@Composable
fun TextFieldComponent() {
    val maxChars = 5
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { newText ->

            if (newText.length <= maxChars && newText.all { it.isDigit() }) {
                text = newText
            }

        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text("Introduce la cantidad de pasos deseada") },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    )
}

@Composable
fun StartActivityButton() {

    Button(
        onClick = {

        }
    ) {
        Text(text = "Comenzar actividad")
    }


}