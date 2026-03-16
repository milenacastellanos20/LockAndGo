package com.dam.lockgo.presentation.pomodoro

import androidx.lifecycle.ViewModel
import com.dam.lockgo.domain.model.PomodoroState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PomodoroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    fun startPomodoro() {
        _uiState.value = _uiState.value.copy(
            pomodoroState = PomodoroState.RUNNING
        )
    }

    fun pausePomodoro() {
        _uiState.value = _uiState.value.copy(
            pomodoroState = PomodoroState.PAUSED
        )
    }

    fun resetPomodoro() {
        _uiState.value = PomodoroUiState()
    }

    fun completePomodoro() {
        _uiState.value = _uiState.value.copy(
            pomodoroState = PomodoroState.BREAK,
            completedPomodoros = _uiState.value.completedPomodoros + 1,
            minutesLeft = 5,
            secondsLeft = 0
        )
    }

    fun finishBreak() {
        _uiState.value = _uiState.value.copy(
            pomodoroState = PomodoroState.IDLE,
            minutesLeft = 25,
            secondsLeft = 0
        )
    }
}