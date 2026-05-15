package com.natcalc.parser

sealed class ParseResult {

    data class Arithmetic(
        val expression: String,
        val result: Double,
        val isArabic: Boolean = false
    ) : ParseResult()

    data class Percentage(
        val percentage: Double,
        val baseValue: Double,
        val result: Double,
        val isArabic: Boolean = false
    ) : ParseResult()

    data class Currency(
        val amount: Double,
        val fromCurrency: String,
        val toCurrency: String,
        val isArabic: Boolean = false
    ) : ParseResult()

    data class Fraction(
        val fractionLabel: String,
        val numerator: Int,
        val denominator: Int,
        val value: Double,
        val result: Double,
        val isArabic: Boolean = false
    ) : ParseResult()

    data class Error(
        val message: String,
        val isArabic: Boolean = false
    ) : ParseResult()
}
