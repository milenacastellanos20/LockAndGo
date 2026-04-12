package com.dam.lockgo.service

import com.dam.lockgo.domain.model.PomodoroState

data class PomodoroUiState(
    val minutesLeft: Int = 25,
    val secondsLeft: Int = 0,
    val pomodoroState: PomodoroState = PomodoroState.IDLE,
    val completedPomodoros: Int = 0
)