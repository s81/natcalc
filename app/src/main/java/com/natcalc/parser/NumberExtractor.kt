package com.natcalc.parser

object NumberExtractor {

    private val NUMBER_REGEX = Regex("""\d{1,3}(?:,\d{3})*(?:\.\d+)?|\d+(?:\.\d+)?""")

    fun extractAll(text: String): List<Double> =
        NUMBER_REGEX.findAll(text).mapNotNull { it.value.replace(",", "").toDoubleOrNull() }.toList()

    fun extractFirst(text: String): Double? =
        NUMBER_REGEX.find(text)?.value?.replace(",", "")?.toDoubleOrNull()

    fun normalizeArabicNumerals(text: String): String = text.map { c ->
        when (c) {
            '٠', '۰' -> '0'; '١', '۱' -> '1'; '٢', '۲' -> '2'; '٣', '۳' -> '3'
            '٤', '۴' -> '4'; '٥', '۵' -> '5'; '٦', '۶' -> '6'; '٧', '۷' -> '7'
            '٨', '۸' -> '8'; '٩', '۹' -> '9'
            else -> c
        }
    }.joinToString("")
}
