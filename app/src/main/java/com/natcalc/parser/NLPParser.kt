package com.natcalc.parser

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NLPParser @Inject constructor(
    private val arithmeticParser: ArithmeticParser,
    private val percentageParser: PercentageParser,
    private val currencyParser: CurrencyParser,
    private val fractionParser: FractionParser
) {

    fun parse(rawInput: String): ParseResult {
        val input = NumberExtractor.normalizeArabicNumerals(rawInput).trim()
        if (input.isEmpty()) return ParseResult.Error("Please enter a calculation.", false)

        val isArabic = LanguageDetector.isPrimarilyArabic(input)

        return when {
            fractionParser.canHandle(input)    -> fractionParser.parse(input, isArabic)
            percentageParser.canHandle(input)  -> percentageParser.parse(input, isArabic)
            currencyParser.canHandle(input)    -> currencyParser.parse(input, isArabic)
            arithmeticParser.canHandle(input)  -> arithmeticParser.parse(input, isArabic)
            else -> ParseResult.Error(buildHint(isArabic, input), isArabic)
        }
    }

    private fun buildHint(isArabic: Boolean, input: String): String = if (isArabic) {
        "لم أفهم «$input»\nجرّب مثلاً:\n• 100 + 200\n• 20 بالمية من 5000\n• حول 50 دولار إلى يورو\n• نصف 100"
    } else {
        "I couldn't understand \"$input\"\nTry for example:\n• 100 + 200\n• 20% of 500\n• Convert 50 USD to EUR\n• Half of 100"
    }
}
