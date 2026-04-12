package com.dam.lockgo.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.lockgo.domain.model.PomodoroState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PomodoroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun startPomodoro() {
        if (_uiState.value.pomodoroState == PomodoroState.RUNNING) return

        _uiState.value = _uiState.value.copy(
            pomodoroState = PomodoroState.RUNNING
        )

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.minutesLeft > 0 || _uiState.value.secondsLeft > 0) {
                delay(1000)

                val current = _uiState.value

                if (current.secondsLeft == 0) {
                    if (current.minutesLeft > 0) {
                        _uiState.value = current.copy(
                            minutesLeft = current.minutesLeft - 1,
                            secondsLeft = 59
                        )
                    }
                } else {
                    _uiState.value = current.copy(
                        secondsLeft = current.secondsLeft - 1
                    )
                }
            }

            completePomodoro()
        }
    }

    fun pausePomodoro() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            pomodoroState = PomodoroState.PAUSED
        )
    }

    fun resetPomodoro() {
        timerJob?.cancel()
        _uiState.value = PomodoroUiState()
    }

    fun completePomodoro() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            pomodoroState = PomodoroState.BREAK,
            completedPomodoros = _uiState.value.completedPomodoros + 1,
            minutesLeft = 5,
            secondsLeft = 0
        )
    }

    fun finishBreak() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            pomodoroState = PomodoroState.IDLE,
            minutesLeft = 25,
            secondsLeft = 0
        )
    }
}
//Añadir