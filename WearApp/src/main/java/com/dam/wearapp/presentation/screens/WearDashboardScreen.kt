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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dam.wearapp.presentation.service.PasosViewModel

/**
 * Método que pintará todos los elementos que conformarán la interfaz de la App en el reloj Wear OS
 * @author Hugo Garrido Rojo
 */
@Composable
fun WearDashboardScreen() {

    val application = LocalContext.current.applicationContext as Application

    val viewModel: PasosViewModel =
                            viewModel(factory = ViewModelProvider
                                                .AndroidViewModelFactory
                                                .getInstance(application))


    val yaNotificado = viewModel.yaAvisado

    if (!yaNotificado) {
        ObjetivoScreen(viewModel)
    } else {
        SinObjetivoScreen()
    }

}