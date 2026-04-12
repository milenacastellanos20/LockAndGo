package com.dam.lockgo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

@Dao
interface RewardDao {

    @Query("SELECT * FROM reward_profile WHERE id = 1")
    suspend fun getProfile(): RewardProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: RewardProfileEntity)

    @Query("SELECT * FROM owned_badges")
    suspend fun getOwnedBadges(): List<OwnedBadgeEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadge(badge: OwnedBadgeEntity): Long

    @Query("SELECT COUNT(*) FROM owned_badges WHERE name = :name")
    suspend fun badgeExists(name: String): Int
}

