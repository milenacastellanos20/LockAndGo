package com.dam.lockgo.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

//Clase que solamente funcionará para métodos comunes que utilizarán más de una pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent(
    onBackClick: (() -> Unit)? = null
) {

    CenterAlignedTopAppBar(
        title = {
            // Damos formato al título para que encaje con la paleta de la app
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, color = Color(0xFFE53935))) {
                        append("Lock")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, color = Color.White)) { // Rojo
                        append("&")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, color = Color(0xFF43A047))) { // Verde
                        append("Go")
                    }
                },
                fontSize = 22.sp,
                letterSpacing = 1.sp
            )
        },
        navigationIcon = {

            if (onBackClick != null) {

                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

            }

        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            // El fondo se funde con el inicio de nuestro degradado oscuro
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White // Aseguramos contraste
        )
    )

}