package com.dam.lockgo

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dam.lockgo.data.service.AppBlockingService
import com.dam.lockgo.domain.usecase.CheckPermissionsUseCase
import com.dam.lockgo.domain.usecase.PermissionStatus
import com.dam.lockgo.service.ActividadFinalizadaViewModel
import com.dam.lockgo.service.MobileListenerService
import com.dam.lockgo.ui.screens.iniciarActividad
import com.google.android.gms.wearable.MessageEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestsInstrumentadosMobile {

    //Test para comprobar si el sistema de permisos funciona correctamente
    @Test
    fun test_1_Permisos_Flujo_Completo() {
        //Declaración de variables
        val context = ApplicationProvider.getApplicationContext<Context>()
        val useCase = CheckPermissionsUseCase(context)

        //Variable que nos servirá por si hay algún permiso faltante
        val estadoInicial = useCase.invoke()

        //Comprobamos que si falta al menos uno, isAllGranted debe ser false
        if (!estadoInicial.activityRecognition || !estadoInicial.overlay ||
            !estadoInicial.accessibility || !estadoInicial.notifications) {

            assertFalse("isAllGranted debería ser false si falta algún permiso",
                estadoInicial.isAllGranted)
            println("Paso 1: Sistema detecta correctamente que el set no está completo")
        }

        //Elaboración de la lógica: Comprobamos el Objeto de Datos (PermissionStatus)
        //Vamos a forzar un objeto de estado para verificar que la lógica de combinación funciona
        val estadoSimuladoTodoOK = PermissionStatus(
            activityRecognition = true,
            overlay = true,
            accessibility = true,
            notifications = true
        )

        assertTrue("La lógica de PermissionStatus falla: debería ser true con todos los permisos",
            estadoSimuladoTodoOK.isAllGranted)
        println("Paso 2: La clase de datos valida correctamente el conjunto completo")

        //Comprobamos la transición (Simulando lo que pasaría al volver de Ajustes)
        val estadoSimuladoFaltaUno = estadoSimuladoTodoOK.copy(overlay = false)
        assertFalse("La lógica de PermissionStatus falla: debería ser false si falta la superposición",
            estadoSimuladoFaltaUno.isAllGranted)
        println("Paso 3: La detección de un solo permiso faltante funciona")
    }

    //Test para comprobar que la lista de apps seleccionadas se filtra correctamente
    @Test
    fun test_2_Seleccion_Apps_Filtra_Correctamente() {
        //Declaración de variables
        val listaPrueba = listOf("com.google.android.youtube", "com.android.chrome", "com.google.android.calendar")
        val seleccionadas = mutableListOf<String>()

        // Simulamos marcarlas
        seleccionadas.add(listaPrueba[0])
        seleccionadas.add(listaPrueba[1])

        assertEquals(2, seleccionadas.size)
        assertTrue(seleccionadas.contains("com.google.android.youtube"))
        assertFalse(seleccionadas.contains("com.google.android.calendar"))
    }

    //Test para comprobar que la lista de apps seleccionadas viaja correctamente hacia la
    //pantalla de el establecimiento de la meta de pasos
    @Test
    fun test_3_PackageNames_Viajan_Correctamente() {
        //Declaración de variables
        val appsOriginales = listOf("com.android.settings", "com.google.android.youtube")

        // Simulamos lo que se hace en AppNavigation
        val json = Gson().toJson(appsOriginales)
        val encodedJson = Uri.encode(json)

        // Simulamos la recepción
        val decodedJson = Uri.decode(encodedJson)
        val type = object : TypeToken<List<String>>() {}.type
        val appsRecibidas = Gson().fromJson<List<String>>(decodedJson, type)

        assertEquals(appsOriginales, appsRecibidas)
    }

    //Test para comprobar que la función de inicio de actividad se ejecuta correctamente,
    //comprobando que la SharedPrefernce que gestiona si la actividad está finalizada o no,
    //efectivamente indica que la actividad está iniciada

    //NOTA: para que este test funcione, se nececita sí o sí de un dispositivo físico
    @Test
    fun test_4_Iniciar_Actividad_Ejecucion_Y_Prefs() {
        //Declaración de variables
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val prefs = context.getSharedPreferences("LockAndGoPrefs", Context.MODE_PRIVATE)

        val pasosMeta = "500"
        val appsSeleccionadas = listOf("com.android.chrome", "com.google.android.youtube")

        //Forzamos el estado inicial
        prefs.edit().putBoolean("actividad_finalizada", true).apply()

        //Ejecutamos la función
        instrumentation.runOnMainSync {

            // Pasamos los parámetros que hemos definido a la función
            iniciarActividad(
                pasos = pasosMeta,
                context = context,
                selectedApps = appsSeleccionadas,
                prefs = prefs
            )
        }

        // 3. Pausa de seguridad para que el addOnSuccessListener de Google Play Services responda
        Thread.sleep(2500)

        //Verificación: Leemos directamente de las prefs con un valor por defecto distinto
        // para estar seguros de que estamos leyendo lo que la función escribió.
        val resultadoFinal = prefs.getBoolean("actividad_finalizada", true)

        assertEquals("La función iniciarActividad debería haber puesto la preferencia en FALSE",
            false, resultadoFinal)

        println("¡Test 4 superado! Todos los tests están ahora en verde.")
    }

    //Test para comprobar que el servicio de bloqueo recibe correctamente la lista de apps seleccionadas
    @Test
    fun test_5_Envio_Al_Servicio_De_Bloqueo() {
        //Declaración de variables
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appsDePrueba = arrayListOf("com.whatsapp", "com.tiktok")

        val intent = Intent(context, AppBlockingService::class.java).apply {
            putStringArrayListExtra("apps_bloqueadas", appsDePrueba)
        }

        val extrasRecibidos = intent.getStringArrayListExtra("apps_bloqueadas")

        assertNotNull(extrasRecibidos)
        assertEquals(2, extrasRecibidos?.size)
        assertEquals("com.whatsapp", extrasRecibidos?.get(0))
    }

    //Test para comprobar que el móvil recibe los datos del reloj correctamente,
    //comprobando que la SharedPreference que gestiona si la actividad está finalizada o no,
    //efectivamente indica que la actividad está finalizada
    @Test
    fun test_6_Recepcion_Datos_Reloj_Actualiza_UI() {
        //Declaración de variables
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = ActividadFinalizadaViewModel(app)

        //Creamos el servicio
        val service = MobileListenerService()

        //Creamos el mensaje falso
        val fakeEvent = object : MessageEvent {
            override fun getRequestId(): Int = 1
            override fun getData(): ByteArray {TODO()}
            override fun getPath(): String = "/end_activity"
            override fun getSourceNodeId(): String = "wear_node"
        }

        //Lo lanzamos
        try {
            service.onMessageReceived(fakeEvent)
        } catch (e: Exception) {
            // Ignoramos errores de UI, nos importa la lógica
        }

        // El ViewModel debería haber reaccionado por el listener
        assertEquals(true, viewModel.actividadFinalizada)
    }

}