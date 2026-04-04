package com.dam.lockgo.presentation.rewards

data class RewardUiState(
    val coins: Int = 0,
    val completedObjectives: Int = 0,
    val message: String = ""
)