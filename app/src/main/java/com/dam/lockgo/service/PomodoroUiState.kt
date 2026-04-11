package com.dam.lockgo.presentation.pomodoro

import com.dam.lockgo.domain.model.PomodoroState

data class PomodoroUiState(
    val minutesLeft: Int = 25,
    val secondsLeft: Int = 0,
    val pomodoroState: PomodoroState = PomodoroState.IDLE,
    val completedPomodoros: Int = 0
)
//Añadir