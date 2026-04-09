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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dam.lockgo.ui.screens.AppSelectionScreen
import com.dam.lockgo.ui.screens.MainScreen
import com.dam.lockgo.ui.screens.StartActivityScreen
import com.dam.lockgo.ui.screens.TopBarComponent
import com.google.gson.Gson
import android.net.Uri
import com.google.gson.reflect.TypeToken

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
                onNavigateAppSelection = { navController.navigate("app_selection") }
            )

        }

        composable("start_activity/{apps}",
            listOf(navArgument("apps") { type = NavType.StringType }) ) {

            val jsonApps = it.arguments?.getString("apps") ?: ""

            val listApps = object: TypeToken<List<String>>() {}.type

            val selectedApps = Gson().fromJson<List<String>>(jsonApps, listApps) ?: emptyList()

            StartActivityScreen(
                onBack = { navController.popBackStack() },
                selectedApps = selectedApps
            )

        }

        composable("app_selection") {

            AppSelectionScreen(
                onBack = { navController.popBackStack() },
                onNavigateStartActivity = { apps ->
                    val json = Gson().toJson(apps)

                    // 2. IMPORTANTE: Enmascaramos el JSON para que los símbolos
                    // no rompan la URL de navegación
                    val encodedJson = Uri.encode(json)

                    // 3. Lanzamos la navegación
                    navController.navigate("start_activity/$encodedJson")
                }

            )
        }

    }

}



