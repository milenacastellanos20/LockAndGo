package com.dam.lockgo.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
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
                    .padding(horizontal = 16.dp), // Separación de los bordes
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text("Establecer meta de pasos")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                Divider(color = Color.LightGray, thickness = 0.5.dp)
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

    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onToggleSelection() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
            color = MaterialTheme.colorScheme.onSurface
        )

        Checkbox(
            checked = isSelected,
            onCheckedChange = null
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