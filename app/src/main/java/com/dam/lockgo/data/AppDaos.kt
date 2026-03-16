package com.dam.lockgo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LockAndGoDao {
    //Pasos y Metas
    @Query("SELECT * FROM DailyProgress WHERE date = :date")
    fun getProgressByDate(date: String): Flow<DailyProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: DailyProgress)

    //Apps Bloqueadas
    @Query("SELECT * FROM BlockedApp")
    fun getAllBlockedApps(): Flow<List<BlockedApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedApp(app: BlockedApp)

    @Delete
    suspend fun deleteBlockedApp(app: BlockedApp)
}