package com.natcalc.data.repository

import com.natcalc.data.remote.api.CurrencyApi
import com.natcalc.domain.repository.CurrencyRepository
import com.natcalc.parser.CurrencyMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi
) : CurrencyRepository {

    // In-memory cache: baseCurrency -> (rates map, fetchTime)
    private val cache = mutableMapOf<String, Pair<Map<String, Double>, Long>>()
    private val cacheTtlMs = 3_600_000L // 1 hour

    override suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): Double {
        if (fromCurrency == toCurrency) return 1.0

        val rates = fetchRates(fromCurrency)
        return rates[toCurrency]
            ?: throw UnsupportedCurrencyException("Currency not supported: $toCurrency")
    }

    private suspend fun fetchRates(base: String): Map<String, Double> {
        val cached = cache[base]
        if (cached != null && System.currentTimeMillis() - cached.second < cacheTtlMs) {
            return cached.first
        }
        return try {
            val response = api.getLatestRates(base)
            cache[base] = Pair(response.rates, System.currentTimeMillis())
            response.rates
        } catch (e: Exception) {
            // Offline fallback — compute from mock rates
            computeMockRatesFor(base) ?: throw NetworkException("Network unavailable and no offline rates for $base")
        }
    }

    private fun computeMockRatesFor(base: String): Map<String, Double>? {
        val baseUsdRate = CurrencyMapper.mockRatesFromUsd[base] ?: return null
        return CurrencyMapper.mockRatesFromUsd.mapValues { (_, rate) -> rate / baseUsdRate }
    }

    fun isUsingOfflineRates(base: String): Boolean {
        val cached = cache[base]
        return cached == null || System.currentTimeMillis() - cached.second >= cacheTtlMs
    }
}

class UnsupportedCurrencyException(message: String) : Exception(message)
class NetworkException(message: String) : Exception(message)
