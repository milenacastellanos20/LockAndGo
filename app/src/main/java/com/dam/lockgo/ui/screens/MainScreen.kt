package com.dam.lockgo.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun MainScreen(
    onNavigateAppSelection: () -> Unit = {}
) {
    Log.d("Screen 1", "Screen 1 ejecutada")
    Scaffold(
        topBar = { TopBarComponent() },
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()
                                .padding(innerPadding),
                        contentAlignment = Alignment.Center)
        {
            StartActivityScreenButtonComponent(onNavigateAppSelection)
        }

    }

}

@Composable
fun StartActivityScreenButtonComponent(onNavigate: () -> Unit) {

    Button(
        onClick = {
            onNavigate()
        }

    ) {
        Text(text = "Comenzar actividad")
    }

}


