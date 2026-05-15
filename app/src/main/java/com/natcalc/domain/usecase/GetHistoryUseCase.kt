package com.natcalc.domain.usecase

import com.natcalc.domain.model.Message
import com.natcalc.domain.repository.CalculationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: CalculationRepository
) {
    operator fun invoke(): Flow<List<Message>> = repository.getHistory()
}
