package com.natcalc.domain.usecase

import com.natcalc.domain.repository.CalculationRepository
import javax.inject.Inject

class ClearHistoryUseCase @Inject constructor(
    private val repository: CalculationRepository
) {
    suspend operator fun invoke() = repository.clearHistory()
}
