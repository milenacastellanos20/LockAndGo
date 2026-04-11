package com.dam.lockgo.domain.repository

import com.dam.lockgo.domain.model.PomodoroSession

interface PomodoroRepository {
    fun getCurrentSession(): PomodoroSession
    fun saveSession(session: PomodoroSession)
}
//Añadir