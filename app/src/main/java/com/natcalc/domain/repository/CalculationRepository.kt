package com.natcalc.domain.repository

import com.natcalc.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface CalculationRepository {
    fun getHistory(): Flow<List<Message>>
    suspend fun saveMessage(message: Message): Long
    suspend fun deleteMessage(message: Message)
    suspend fun clearHistory()
}
