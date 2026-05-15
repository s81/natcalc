package com.natcalc.domain.repository

interface CurrencyRepository {
    suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): Double
}
