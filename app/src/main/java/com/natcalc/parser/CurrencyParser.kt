package com.natcalc.parser

import javax.inject.Inject

class CurrencyParser @Inject constructor() {

    fun canHandle(input: String): Boolean {
        val codes = findCurrencies(input)
        return codes.size >= 2 && NumberExtractor.extractFirst(input) != null
    }

    fun parse(input: String, isArabic: Boolean): ParseResult {
        val amount = NumberExtractor.extractFirst(input)
            ?: return ParseResult.Error(
                if (isArabic) "لم أجد مبلغاً" else "No amount found", isArabic
            )

        val currencies = findCurrencies(input)
        if (currencies.size < 2) {
            return ParseResult.Error(
                if (isArabic) "أحتاج إلى عملتين للتحويل" else "Need two currencies to convert", isArabic
            )
        }

        return ParseResult.Currency(
            amount = amount,
            fromCurrency = currencies[0],
            toCurrency = currencies[1],
            isArabic = isArabic
        )
    }

    private fun findCurrencies(input: String): List<String> {
        val found = mutableListOf<String>()
        val lower = input.lowercase()

        // Try multi-word matches first (longer keys shadow shorter ones)
        CurrencyMapper.aliases.keys
            .sortedByDescending { it.length }
            .forEach { key ->
                if (lower.contains(key) && found.none { it == CurrencyMapper.aliases[key] }) {
                    CurrencyMapper.aliases[key]?.let { found.add(it) }
                }
                if (found.size == 2) return found
            }

        // Also catch bare ISO codes like "USD", "EUR"
        Regex("""\b([A-Z]{3})\b""").findAll(input).forEach { m ->
            val code = m.groupValues[1]
            if (CurrencyMapper.mockRatesFromUsd.containsKey(code) && !found.contains(code)) {
                found.add(code)
            }
        }

        return found.take(2)
    }
}
