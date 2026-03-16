package com.dam.lockgo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//tabla para el progreso diario (Pasos y Metas)
@Entity(tableName = "daily_progress")
data class DailyProgress(
    @PrimaryKey val date: String,
    val currentSteps: Int = 0,
    val stepGoal: Int = 6000
)

//Tabla para las apps bloqueadas
@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey val packageName: String, // ID es el paquete (ej: "com.instagram.android")
    val appName: String
)
