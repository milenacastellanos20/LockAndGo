package com.dam.lockgo.domain.repository

import com.dam.lockgo.domain.model.RewardWallet

interface RewardRepository {
    suspend fun getWallet(): RewardWallet
    suspend fun addCoins(amount: Int)
    suspend fun completeObjective(reward: Int)
    suspend fun buyBadge(name: String, cost: Int): Boolean
}