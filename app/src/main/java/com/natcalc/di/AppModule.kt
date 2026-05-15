package com.natcalc.di

import com.natcalc.data.repository.CalculationRepositoryImpl
import com.natcalc.data.repository.CurrencyRepositoryImpl
import com.natcalc.domain.repository.CalculationRepository
import com.natcalc.domain.repository.CurrencyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindCalculationRepository(impl: CalculationRepositoryImpl): CalculationRepository

    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(impl: CurrencyRepositoryImpl): CurrencyRepository
}
