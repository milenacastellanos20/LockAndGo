package com.dam.lockgo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//Historial de actividades completadas
@Entity(tableName = "completed_activities")
data class CompletedActivity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val stepGoal: Int
)

@Entity(tableName = "reward_profile")
data class RewardProfileEntity(
    @PrimaryKey val id: Int = 1,
    val coins: Int = 0,
    val completedObjectives: Int = 0
)

@Entity(tableName = "owned_badges")
data class OwnedBadgeEntity(
    @PrimaryKey val name: String,
    val cost: Int
)