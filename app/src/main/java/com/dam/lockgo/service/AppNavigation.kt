package com.dam.lockgo.service

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dam.lockgo.ui.screens.MainScreen
import com.dam.lockgo.ui.screens.StartActivityScreen
import com.dam.lockgo.ui.screens.TopBarComponent

@Composable
fun AppNavigation() {

    var navController = rememberNavController()

    NavHost(
    navController = navController,
    startDestination = "main",
    // 1. Ida: Cómo entra la pantalla nueva (desde derecha)
    enterTransition = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(300) // 300ms es la duración estándar nativa
        )
    },

    // 2. Ida: Cómo SALE la pantalla vieja (hacia izquierda - ESTO FALTA)
    exitTransition = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = tween(300)
        )
    },

    // 3. Vuelta: Cómo entra la pantalla anterior (desde izquierda)
    popEnterTransition = {
        slideIntoContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(300)
        )
    },

    // 4. Vuelta: Cómo SALE la pantalla actual (hacia derecha)
    popExitTransition = {
        slideOutOfContainer(
            towards = AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = tween(300)
        )
    }

) {

    composable("main") {

        MainScreen(
            onNavigateStartActivity = { navController.navigate("start_activity") }

        )

    }

    composable("start_activity") {

        StartActivityScreen(
            onBack = { navController.popBackStack() }
        )

    }

    }

}



