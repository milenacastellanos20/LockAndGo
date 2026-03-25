package com.dam.wearapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Text


@Composable
fun SinObjetivoScreen() {

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Ningún objetivo asignado")
    }

}
