package com.dam.lockgo.data.repository

import com.dam.lockgo.domain.model.RewardWallet
import com.dam.lockgo.domain.repository.RewardRepository

class RewardRepositoryImpl : RewardRepository {

    private var wallet = RewardWallet(
        coins = 0,
        completedObjectives = 0
    )

    override fun getWallet(): RewardWallet {
        return wallet
    }

    override fun addCoins(amount: Int) {
        wallet = wallet.copy(
            coins = wallet.coins + amount
        )
    }

    override fun completeObjective(reward: Int) {
        wallet = wallet.copy(
            coins = wallet.coins + reward,
            completedObjectives = wallet.completedObjectives + 1
        )
    }

    override fun spendCoins(amount: Int): Boolean {
        return if (wallet.coins >= amount) {
            wallet = wallet.copy(
                coins = wallet.coins - amount
            )
            true
        } else {
            false
        }
    }
}