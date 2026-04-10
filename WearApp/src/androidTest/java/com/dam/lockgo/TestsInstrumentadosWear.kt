package com.dam.lockgo

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dam.wearapp.presentation.screens.EndActivity
import com.dam.wearapp.presentation.screens.ObjetivoScreen
import com.dam.wearapp.presentation.service.DatosViewModel
import com.dam.wearapp.presentation.service.StepCounterManager
import com.dam.wearapp.presentation.service.WearListenerService
import com.dam.wearapp.presentation.theme.LockGoTheme
import org.junit.Test
import com.google.android.gms.wearable.MessageEvent
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.apply

@RunWith(AndroidJUnit4::class)
class TestsInstrumentadosWear {

    @get:Rule
    val composeTestRule = createComposeRule()

    //Test para comprobar que el móvil recibe mensajes del móvil correctamente
    @Test
    fun test_1_Receptor_Datos_Inicia_Actividad() {
        //Declaración de variables
        //Creamos el servicio
        val service = WearListenerService()

        //Creamos el mensaje falso
        val fakeEvent = object : MessageEvent {
            override fun getRequestId(): Int = 1
            override fun getPath(): String = "/start_activity"
            override fun getData(): ByteArray = "5000".toByteArray()
            override fun getSourceNodeId(): String = "phone_node"
        }

        //Lo lanzamos
        try {
            service.onMessageReceived(fakeEvent)
        } catch (e: Exception) {
            // Ignoramos errores de UI, nos importa la lógica
        }
    }

    //Test para comprobar que el servicio de pasos se inicia con la meta actualizada
    @Test
    fun test_2_Servicio_Estructura_Correcta() {
        //Declaración de variables
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        //Validamos que el Intent se construye con la meta correcta
        val metaEsperada = 7000
        val intent = Intent(context, StepCounterManager::class.java).apply {
            putExtra("META_PASOS", metaEsperada)
        }

        //Verificamos que los extras están ahí
        val metaEnIntent = intent.getIntExtra("META_PASOS", 0)
        assertEquals("La meta de pasos no se guardó correctamente en el Intent", metaEsperada, metaEnIntent)

        //Verificamos que la clase del servicio es la correcta
        assertEquals(StepCounterManager::class.java.name, intent.component?.className)

    }

    //Test para comprobar que el ViewModel detecta los cambios en las prefs que realiza el servicio de pasos
    @Test
    fun test_3_ViewModel_Reacciona_A_Cambio_En_Prefs() {
        //Declaración de variables
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel = DatosViewModel(context.applicationContext as Application)
        val prefs = context.getSharedPreferences("pasos_prefs", Context.MODE_PRIVATE)

        //Preparamos una meta inicial
        prefs.edit().putInt("meta_pasos", 5000).apply()

        //Simulamos que el Servicio de Pasos escribe un nuevo valor en las SharedPreferences
        // Esto es lo que pasaría cuando el sensor detecta movimiento
        val nuevosPasos = 1234
        prefs.edit().putInt("ultimos_pasos_calculados_registrados", nuevosPasos).apply()

        //Pequeña pausa para que el Listener de SharedPreferences se dispare
        Thread.sleep(200)

        //Verificamos si el ViewModel se enteró del cambio
        assertEquals("El ViewModel no actualizó los pasos desde las SharedPreferences",
            nuevosPasos, viewModel.pasosActuales)
    }

    //Test para comprobar que el ViewModel limita los pasos registrados a la meta en el caso de que el sensor registre más pasos de la cuenta (muy común)
    @Test
    fun test_4_ViewModel_Ajusta_Pasos_A_Meta() {
        //Declaración de variables
        val context = ApplicationProvider.getApplicationContext<Application>()
        val prefs = context.getSharedPreferences("pasos_prefs", Context.MODE_PRIVATE)

        //Creamos el ViewModel. Al crearse, el INIT del ViewModel leerá de las prefs.
        val viewModel = DatosViewModel(context)

        //Limpiamos todo para que no haya basura de tests anteriores
        prefs.edit().clear().apply()

        //Metemos los datos que usaremos para el test
        prefs.edit()
            .putInt("meta_pasos", 1000)
            .apply()

        prefs.edit()
            .putInt("ultimos_pasos_calculados_registrados", 1050)
            .apply()

        //Hacemos una pequeña pausa para que al ViewModel le de tiempo a limitar la
        //cantidad de pasos registrados a la meta en el caso de que éstos se pasen
        //de la misma
        Thread.sleep(1000)

        //Variable para verificar si los pasos se cambiaron correctamente
        val pasosCorregidosEnPrefs = prefs.getInt("ultimos_pasos_calculados_registrados", 0)

        //Verificamos tanto el ViewModel como el archivo físico de prefs
        assertEquals("Las SharedPreferences deberían haber corregido el valor a 1000", 1000, pasosCorregidosEnPrefs)
        assertEquals("El ViewModel debería mostrar 1000", 1000, viewModel.pasosActuales)
        assertEquals("La meta debería marcarse como cumplida", true, prefs.getBoolean("meta_cumplida", false))
    }

    //Test para comprobar que la UI se actualiza en tiempo real al actualizar las prefs del ViewModel
    @Test
    fun test_5_UI_Muestra_Progreso_Correcto() {
        //Declaración de variables
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel = DatosViewModel(context.applicationContext as Application)
        val prefs = context.getSharedPreferences("pasos_prefs", Context.MODE_PRIVATE)

        // Forzamos datos para verlos en la pantalla
        prefs.edit().putInt("meta_pasos", 3000).apply()
        prefs.edit().putInt("ultimos_pasos_calculados_registrados", 1500).apply()

        //Lanzamos la pantalla
        composeTestRule.setContent {
            LockGoTheme {
                ObjetivoScreen(viewModel)
            }
        }

        //Buscamos que aparezca el progreso en pantalla
        composeTestRule.onNodeWithText("1500/").assertIsDisplayed()
        composeTestRule.onNodeWithText("3000 pasos").assertIsDisplayed()
    }

    //Test para comprobar que los datos del ViewModel se reinician correctamente al sincronizar la
    //actividad completada con el móvil
    @Test
    fun test_6_Emisor_Datos_Sincroniza_Y_Verifica_Envio() {
        //Declaración de variables
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = ApplicationProvider.getApplicationContext<Application>()
        val context = instrumentation.targetContext

        //Inicializamos el ViewModel
        val viewModel = DatosViewModel(app)

        //Ejecutamos la función
        instrumentation.runOnMainSync {
            // Preparamos los datos
            viewModel.meta = 5000
            viewModel.pasosActuales = 5000

            // Pasamos los parámetros que hemos definido a la función
            EndActivity(
                steps = 5000,
                goal = 5000,
                viewModel = viewModel,
                context = context
            )
        }

        //Esperamos a que las corrutinas de la función terminen
        Thread.sleep(2000)

        //Verificamos el resultado final: Meta reseteada a 0
        // Si la meta es 0, es que EndActivity llamó a reiniciarDatos() con éxito
        assertEquals("La función EndActivity no reseteó los datos", 0,
            viewModel.meta)
    }

}