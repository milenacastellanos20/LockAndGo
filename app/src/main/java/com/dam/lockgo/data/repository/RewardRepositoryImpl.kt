package com.dam.lockgo.data.repository

import android.content.Context
import com.dam.lockgo.data.AppDatabase
import com.dam.lockgo.data.OwnedBadgeEntity
import com.dam.lockgo.data.RewardProfileEntity
import com.dam.lockgo.domain.model.RewardWallet
import com.dam.lockgo.domain.repository.RewardRepository

class RewardRepositoryImpl(context: Context) : RewardRepository {

    private val rewardDao = AppDatabase.getDatabase(context).rewardDao()

    private suspend fun getOrCreateProfile(): RewardProfileEntity {
        val existing = rewardDao.getProfile()
        return if (existing != null) {
            existing
        } else {
            val defaultProfile = RewardProfileEntity()
            rewardDao.insertProfile(defaultProfile)
            defaultProfile
        }
    }

    override suspend fun getWallet(): RewardWallet {
        val profile = getOrCreateProfile()
        val badges = rewardDao.getOwnedBadges().map { it.name }

        return RewardWallet(
            coins = profile.coins,
            completedObjectives = profile.completedObjectives,
            ownedBadges = badges
        )
    }

    override suspend fun addCoins(amount: Int) {
        val profile = getOrCreateProfile()
        rewardDao.insertProfile(
            profile.copy(coins = profile.coins + amount)
        )
    }

    override suspend fun completeObjective(reward: Int) {
        val profile = getOrCreateProfile()
        rewardDao.insertProfile(
            profile.copy(
                coins = profile.coins + reward,
                completedObjectives = profile.completedObjectives + 1
            )
        )
    }

    override suspend fun buyBadge(name: String, cost: Int): Boolean {
        val profile = getOrCreateProfile()

        if (rewardDao.badgeExists(name) > 0) {
            return false
        }

        if (profile.coins < cost) {
            return false
        }

        rewardDao.insertProfile(
            profile.copy(coins = profile.coins - cost)
        )

        rewardDao.insertBadge(
            OwnedBadgeEntity(
                name = name,
                cost = cost
            )
        )

        return true
    }
}