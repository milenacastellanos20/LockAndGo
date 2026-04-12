package com.dam.lockgo.service

data class RewardUiState(
    val coins: Int = 0,
    val completedObjectives: Int = 0,
    val ownedBadges: List<String> = emptyList(),
    val message: String = ""
)