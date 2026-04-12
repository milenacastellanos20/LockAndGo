package com.dam.lockgo.service

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dam.lockgo.data.repository.RewardRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RewardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RewardRepositoryImpl(application)
    private val _uiState = MutableStateFlow(RewardUiState())
    val uiState: StateFlow<RewardUiState> = _uiState.asStateFlow()

    init {
        refreshWallet()
    }

    fun refreshWallet() {
        viewModelScope.launch {
            val wallet = repository.getWallet()
            _uiState.value = _uiState.value.copy(
                coins = wallet.coins,
                completedObjectives = wallet.completedObjectives,
                ownedBadges = wallet.ownedBadges
            )
        }
    }

    fun completeObjective(reward: Int) {
        viewModelScope.launch {
            repository.completeObjective(reward)
            val wallet = repository.getWallet()
            _uiState.value = _uiState.value.copy(
                coins = wallet.coins,
                completedObjectives = wallet.completedObjectives,
                ownedBadges = wallet.ownedBadges,
                message = "Objetivo completado: +$reward monedas"
            )
        }
    }

    fun buyBadge(name: String, cost: Int) {
        viewModelScope.launch {
            val walletBefore = repository.getWallet()
            val success = repository.buyBadge(name, cost)
            val walletAfter = repository.getWallet()

            val message = when {
                success -> "Has comprado el emblema $name"
                walletBefore.ownedBadges.contains(name) -> "Ya tienes ese emblema"
                else -> "No tienes monedas suficientes"
            }

            _uiState.value = _uiState.value.copy(
                coins = walletAfter.coins,
                completedObjectives = walletAfter.completedObjectives,
                ownedBadges = walletAfter.ownedBadges,
                message = message
            )
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = "")
    }
}