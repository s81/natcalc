package com.natcalc.parser

object LanguageDetector {

    private val ARABIC_REGEX = Regex("[؀-ۿݐ-ݿࢠ-ࣿ]")

    fun isPrimarilyArabic(text: String): Boolean {
        val nonSpace = text.filter { !it.isWhitespace() }
        if (nonSpace.isEmpty()) return false
        val arabicCount = ARABIC_REGEX.findAll(nonSpace).count()
        return arabicCount.toDouble() / nonSpace.length > 0.25
    }
}
