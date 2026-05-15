package com.natcalc.data.remote.api

import com.natcalc.data.remote.dto.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApi {

    @GET("latest/{base}")
    suspend fun getLatestRates(@Path("base") baseCurrency: String): ExchangeRateResponse
}
