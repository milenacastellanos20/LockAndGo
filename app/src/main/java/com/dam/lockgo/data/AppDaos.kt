package com.dam.lockgo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LockAndGoDao {

    // Da todo el historial desde el mas reciente hasta el mas antiguo
    @Query("SELECT * FROM completed_activities ORDER BY id DESC")
    fun getAllCompletedActivities(): Flow<List<CompletedActivity>>

    // Guarda una nueva meta cumplida
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCompletedActivity(activity: CompletedActivity)
}