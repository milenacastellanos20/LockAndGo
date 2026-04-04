package com.dam.lockgo.domain.repository

import com.dam.lockgo.domain.model.RewardWallet

interface RewardRepository {
    fun getWallet(): RewardWallet
    fun addCoins(amount: Int)
    fun completeObjective(reward: Int)
    fun spendCoins(amount: Int): Boolean
}