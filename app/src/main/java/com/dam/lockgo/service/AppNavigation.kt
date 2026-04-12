package com.dam.lockgo.service

import android.app.Application
import android.content.Intent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dam.lockgo.ui.screens.AppSelectionScreen
import com.dam.lockgo.ui.screens.MainScreen
import com.dam.lockgo.ui.screens.StartActivityScreen
import com.google.gson.Gson
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dam.lockgo.ui.screens.PomodoroScreen
import com.dam.lockgo.ui.screens.ActivityInProgressScreen
import com.dam.lockgo.ui.screens.RewardScreen
import com.google.gson.reflect.TypeToken
import com.dam.lockgo.ui.screens.ShopScreen
import com.dam.lockgo.ui.screens.AchievementsScreen

@Composable
fun AppNavigation() {

    Log.d("NAV", "AppNavigation ejecutado")

    val navController = rememberNavController()

    Log.d("Hola", "Selección de screens abierta")

    val application = LocalContext.current.applicationContext as Application

    val context = LocalContext.current

    val viewModel: ActividadFinalizadaViewModel =
        viewModel(factory = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application))

    // Sumar monedas al completar la actividad
    val rewardViewModel: RewardViewModel = viewModel()

    Log.d("Sin screen", "Actividad finalizada: ${viewModel.actividadFinalizada}")

    //LaunchedEffect para cuando la variable cambie en tiempo real
    LaunchedEffect(viewModel.actividadFinalizada) {
        Log.d("LaunchedEffect", "Valor recibido: ${viewModel.actividadFinalizada}")
        if (viewModel.actividadFinalizada) {

            //Finalizo la actividad de la pantalla de la actividad en proceso
            //De lo contrario, a pesar de cambiarse de pantalla al terminar la actividad
            //a la main, la actividad seguirá mostrándose por encima

            val intent = Intent("end_activity_in_progress").apply {
                setPackage(context.packageName)
            }

            // 10 monedas por cumplir pasos
            val prefs = context.getSharedPreferences("LockAndGoPrefs", android.content.Context.MODE_PRIVATE)
            val actividadEnCurso = prefs.getBoolean("actividad_en_curso", false)
            if (actividadEnCurso) {
                rewardViewModel.completeObjective(10)

                prefs.edit().putBoolean("actividad_en_curso", false).apply()
            }

            context.sendBroadcast(intent)

            navController.navigate("main") {
                popUpTo(0) { inclusive = true }
            }
            Log.d("Screen 1", "Actividad finalizada: ${viewModel.actividadFinalizada}")
        } else {
            context.startActivity(Intent(context, ActivityInProgressScreen::class.java))
            Log.d("Screen 2", "Actividad finalizada: ${viewModel.actividadFinalizada}")
        }
    }

    NavHost(
        navController = navController,

        startDestination = "main",
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300) // 300ms es la duración estándar nativa
            )
        },

        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },

        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },

        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }

    ) {

        composable("main") {

            MainScreen(
                onNavigateAppSelection = { navController.navigate("app_selection") },
                onNavigatePomodoro = { navController.navigate("pomodoro") },
                onNavigateRewards = { navController.navigate("rewards") }
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

        composable("pomodoro") {
            PomodoroScreen()
        }


        composable("rewards") {
            RewardScreen(
                onNavigateShop = { navController.navigate("shop") },
                onNavigateAchievements = { navController.navigate("achievements") }
            )
        }

        composable("shop") {
            ShopScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("achievements") {
            AchievementsScreen(
                onBack = { navController.popBackStack() }
            )
        }

    }

}



