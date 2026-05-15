package com.natcalc.domain.usecase

import com.natcalc.data.repository.CurrencyRepositoryImpl
import com.natcalc.domain.model.Message
import com.natcalc.domain.model.MessageType
import com.natcalc.domain.repository.CalculationRepository
import com.natcalc.domain.repository.CurrencyRepository
import com.natcalc.parser.*
import javax.inject.Inject

class ProcessInputUseCase @Inject constructor(
    private val nlpParser: NLPParser,
    private val currencyRepository: CurrencyRepository,
    private val calculationRepository: CalculationRepository,
    private val formatter: ResponseFormatter
) {

    suspend operator fun invoke(userInput: String): Message {
        val trimmed = userInput.trim()
        if (trimmed.isEmpty()) {
            return Message(
                userInput = trimmed,
                botResponse = "Please type a calculation.",
                type = MessageType.ERROR
            )
        }

        val parseResult = nlpParser.parse(trimmed)

        val (botResponse, type) = when (parseResult) {
            is ParseResult.Arithmetic -> {
                Pair(formatter.formatArithmetic(parseResult), MessageType.CALCULATION)
            }
            is ParseResult.Percentage -> {
                Pair(formatter.formatPercentage(parseResult), MessageType.PERCENTAGE)
            }
            is ParseResult.Fraction -> {
                Pair(formatter.formatFraction(parseResult), MessageType.FRACTION)
            }
            is ParseResult.Currency -> {
                resolveCurrency(parseResult)
            }
            is ParseResult.Error -> {
                Pair(formatter.formatError(parseResult.message), MessageType.ERROR)
            }
        }

        val message = Message(
            userInput = trimmed,
            botResponse = botResponse,
            type = type
        )
        calculationRepository.saveMessage(message)
        return message
    }

    private suspend fun resolveCurrency(p: ParseResult.Currency): Pair<String, MessageType> {
        return try {
            val rate = currencyRepository.getExchangeRate(p.fromCurrency, p.toCurrency)
            val result = p.amount * rate
            val isOffline = (currencyRepository as? CurrencyRepositoryImpl)
                ?.isUsingOfflineRates(p.fromCurrency) ?: false
            val response = formatter.formatCurrency(
                from = p.fromCurrency, to = p.toCurrency,
                amount = p.amount, result = result, rate = rate,
                isArabic = p.isArabic, isOffline = isOffline
            )
            Pair(response, MessageType.CURRENCY)
        } catch (e: Exception) {
            val msg = if (p.isArabic) {
                "خطأ في التحويل: ${e.message}"
            } else {
                "Conversion error: ${e.message}"
            }
            Pair(msg, MessageType.ERROR)
        }
    }
}
