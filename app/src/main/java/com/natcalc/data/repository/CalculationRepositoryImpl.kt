package com.natcalc.data.repository

import com.natcalc.data.local.dao.MessageDao
import com.natcalc.data.local.entity.toDomain
import com.natcalc.data.local.entity.toEntity
import com.natcalc.domain.model.Message
import com.natcalc.domain.repository.CalculationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculationRepositoryImpl @Inject constructor(
    private val dao: MessageDao
) : CalculationRepository {

    override fun getHistory(): Flow<List<Message>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun saveMessage(message: Message): Long =
        dao.insert(message.toEntity())

    override suspend fun deleteMessage(message: Message) =
        dao.delete(message.toEntity())

    override suspend fun clearHistory() = dao.clearAll()
}
