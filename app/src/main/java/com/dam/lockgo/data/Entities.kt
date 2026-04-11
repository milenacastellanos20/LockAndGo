package com.dam.lockgo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//Historial de actividades completadas
@Entity(tableName = "completed_activities")
data class CompletedActivity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,       // Ej: "2026-04-11"
    val stepGoal: Int       // Ej: 6000
)