package com.dam.lockgo.data.repository

import com.dam.lockgo.domain.model.PomodoroSession
import com.dam.lockgo.domain.repository.PomodoroRepository

class PomodoroRepositoryImpl : PomodoroRepository {

    private var currentSession = PomodoroSession(
        workTimeMinutes = 25,
        breakTimeMinutes = 5,
        completedPomodoros = 0
    )

    override fun getCurrentSession(): PomodoroSession {
        return currentSession
    }

    override fun saveSession(session: PomodoroSession) {
        currentSession = session
    }
}

//Añadir