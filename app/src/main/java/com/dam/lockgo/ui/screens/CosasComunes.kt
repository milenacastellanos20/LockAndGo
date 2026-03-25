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

//Clase que solamente funcionará para métodos comunes que utilizarán más de una pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComponent(
    onBackClick: (() -> Unit)? = null
) {

    CenterAlignedTopAppBar(
        title = { Text(text = "Lock&Go") },
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
            containerColor = Color(0xFF01579B),
            titleContentColor = Color.White
        )

    )

}