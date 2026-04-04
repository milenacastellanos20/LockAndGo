package com.dam.lockgo.presentation.rewards

import androidx.lifecycle.ViewModel
import com.dam.lockgo.data.repository.RewardRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RewardViewModel : ViewModel() {

    private val repository = RewardRepositoryImpl()

    private val _uiState = MutableStateFlow(RewardUiState())
    val uiState: StateFlow<RewardUiState> = _uiState.asStateFlow()

    init {
        loadWallet()
    }

    private fun loadWallet() {
        val wallet = repository.getWallet()
        _uiState.value = RewardUiState(
            coins = wallet.coins,
            completedObjectives = wallet.completedObjectives,
            message = ""
        )
    }

    fun addCoins(amount: Int) {
        repository.addCoins(amount)
        val wallet = repository.getWallet()
        _uiState.value = _uiState.value.copy(
            coins = wallet.coins,
            completedObjectives = wallet.completedObjectives,
            message = "$amount monedas añadidas"
        )
    }

    fun completeObjective(reward: Int) {
        repository.completeObjective(reward)
        val wallet = repository.getWallet()
        _uiState.value = _uiState.value.copy(
            coins = wallet.coins,
            completedObjectives = wallet.completedObjectives,
            message = "Objetivo completado: +$reward monedas"
        )
    }

    fun spendCoins(amount: Int) {
        val success = repository.spendCoins(amount)
        val wallet = repository.getWallet()

        _uiState.value = _uiState.value.copy(
            coins = wallet.coins,
            completedObjectives = wallet.completedObjectives,
            message = if (success) {
                "Has gastado $amount monedas"
            } else {
                "No tienes monedas suficientes"
            }
        )
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            message = ""
        )
    }
}