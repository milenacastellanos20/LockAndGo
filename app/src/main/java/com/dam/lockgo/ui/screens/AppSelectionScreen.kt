package com.dam.lockgo.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    onBack: () -> Unit,
    onNavigateStartActivity: (List<String>) -> Unit
) {
    val context = LocalContext.current

    // Estado para guardar la lista de apps cargadas
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }

    // Estado para guardar los paquetes (packageName) de las apps que marcamos
    val selectedApps = remember { mutableStateListOf<String>() }

    // Cargamos las apps de fondo para no congelar la pantalla
    LaunchedEffect(Unit) {
        installedApps = getInstalledApps(context)
    }

    Scaffold(
        topBar = { TopBarComponent(onBackClick = { onBack() }) },
        // Botón flotante modernizado con degradado
        floatingActionButton = {
            Button(
                onClick = {
                    if (selectedApps.isEmpty()) {
                        // Si la lista está vacía, lanzamos el Toast
                        android.widget.Toast.makeText(
                            context,
                            "No hay ninguna app seleccionada",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onNavigateStartActivity(selectedApps.toList())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFE53935), // Rojo
                                    Color(0xFF43A047)  // Verde
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ESTABLECER META DE PASOS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        // Fondo principal oscuro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E1E1E), // Gris muy oscuro
                            Color(0xFF121212)  // Casi negro
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // Añadimos padding al fondo para que el botón flotante no tape la última app
                contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
            ) {
                // Pequeño texto de cabecera opcional para darle contexto a la lista
                item {
                    Text(
                        text = "Selecciona las apps a bloquear",
                        color = Color(0xFFAAAAAA),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                items(installedApps, key = { it.packageName }) { app ->
                    val isSelected = selectedApps.contains(app.packageName)

                    AppListItem(
                        appInfo = app,
                        isSelected = isSelected,
                        onToggleSelection = {
                            if (isSelected) {
                                selectedApps.remove(app.packageName)
                            } else {
                                selectedApps.add(app.packageName)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppListItem(
    appInfo: AppInfo,
    isSelected: Boolean,
    onToggleSelection: () -> Unit
) {
    val iconBitmap = remember(appInfo.icon) {
        appInfo.icon.toBitmap().asImageBitmap()
    }

    // Colores dinámicos dependiendo de si está seleccionada o no
    val backgroundColor = if (isSelected) Color(0xFF43A047).copy(alpha = 0.15f) else Color.Transparent
    val borderColor = if (isSelected) Color(0xFF43A047) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp) // Separación entre tarjetas
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onToggleSelection() }
            .padding(horizontal = 16.dp, vertical = 12.dp), // Padding interior de la tarjeta
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = iconBitmap,
            contentDescription = "Icono de ${appInfo.name}",
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = appInfo.name,
            modifier = Modifier.weight(1f),
            fontSize = 18.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = Color.White // Texto blanco para el tema oscuro
        )

        // Checkbox personalizado con temática verde
        Checkbox(
            checked = isSelected,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF43A047), // Verde al marcar
                uncheckedColor = Color(0xFFAAAAAA), // Gris inactivo
                checkmarkColor = Color.White // Tick blanco
            )
        )
    }
}

suspend fun getInstalledApps(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
    val pm = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val resolveInfoList = pm.queryIntentActivities(intent, 0)

    resolveInfoList.mapNotNull { resolveInfo ->
        val packageName = resolveInfo.activityInfo.packageName

        if (packageName == context.packageName) return@mapNotNull null

        val name = resolveInfo.loadLabel(pm).toString()
        val icon = resolveInfo.loadIcon(pm)

        AppInfo(name, packageName, icon)
    }.sortedBy { it.name.lowercase() }
}