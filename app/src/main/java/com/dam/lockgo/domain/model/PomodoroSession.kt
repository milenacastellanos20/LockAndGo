package com.dam.lockgo.domain.model

data class PomodoroSession(
    val workTimeMinutes: Int,
    val breakTimeMinutes: Int,
    val completedPomodoros: Int
)
//Añadir