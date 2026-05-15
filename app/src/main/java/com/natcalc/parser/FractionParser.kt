package com.natcalc.parser

import javax.inject.Inject

class FractionParser @Inject constructor() {

    fun canHandle(input: String): Boolean =
        FRACTION_KEYWORDS.any { input.contains(it, ignoreCase = true) } ||
        ARABIC_POSSESSION_PATTERN.containsMatchIn(input)

    fun parse(input: String, isArabic: Boolean): ParseResult {
        // "معي/عندي X [unit] كم نصها" pattern
        val possessionMatch = ARABIC_POSSESSION_PATTERN.find(input)
        if (possessionMatch != null) {
            val value = possessionMatch.groupValues[1].replace(",", "").toDoubleOrNull()
                ?: return ParseResult.Error("لم أتمكن من قراءة الرقم", true)
            val fractionKw = possessionMatch.groupValues[2]
            val key = when {
                fractionKw.contains("نص") -> "نصف"
                fractionKw.contains("ربع") -> "ربع"
                fractionKw.contains("ثلث") -> "ثلث"
                else -> fractionKw
            }
            return buildFractionResult(key, value, isArabic = true)
        }

        // Try direct patterns from FRACTION_MAP
        for ((key, entry) in FRACTION_MAP) {
            val match = entry.regex.find(input) ?: continue
            // Some patterns have alternation groups — grab first non-empty capture group
            val numStr = match.groupValues.drop(1).firstOrNull { it.isNotEmpty() } ?: continue
            val value = numStr.replace(",", "").toDoubleOrNull() ?: continue
            return buildFractionResult(key, value, isArabic)
        }

        return ParseResult.Error(
            if (isArabic) "لم أفهم الكسر المطلوب" else "Couldn't understand the fraction",
            isArabic
        )
    }

    private fun buildFractionResult(key: String, value: Double, isArabic: Boolean): ParseResult {
        val entry = FRACTION_MAP[key]
            ?: FRACTION_MAP.entries.firstOrNull { (k, _) -> key.contains(k) || k.contains(key) }?.value
            ?: return ParseResult.Error(if (isArabic) "كسر غير معروف" else "Unknown fraction", isArabic)

        val result = value * entry.numerator.toDouble() / entry.denominator.toDouble()
        return ParseResult.Fraction(
            fractionLabel = if (isArabic) entry.labelAr else entry.labelEn,
            numerator = entry.numerator,
            denominator = entry.denominator,
            value = value,
            result = result,
            isArabic = isArabic
        )
    }

    private data class FractionEntry(
        val regex: Regex,
        val numerator: Int,
        val denominator: Int,
        val labelEn: String,
        val labelAr: String
    )

    companion object {
        private val FRACTION_MAP: Map<String, FractionEntry> = linkedMapOf(
            "three-quarters" to FractionEntry(
                Regex("""(?i)three[\s\-]quarters?\s+of\s+([\d,]+\.?\d*)"""), 3, 4, "¾ of", "ثلاثة أرباع"),
            "ثلاثة أرباع" to FractionEntry(
                Regex("""ثلاثة\s+أرباع\s+([\d,]+\.?\d*)"""), 3, 4, "¾ of", "ثلاثة أرباع"),
            "half" to FractionEntry(
                Regex("""(?i)half\s+of\s+([\d,]+\.?\d*)|([\d,]+\.?\d*)\s+half"""), 1, 2, "Half of", "نصف"),
            "نصف" to FractionEntry(
                Regex("""نصف\s+([\d,]+\.?\d*)|([\d,]+\.?\d*)\s*(?:نصفه?ا?|نصه?ا?)"""), 1, 2, "Half of", "نصف"),
            "نص" to FractionEntry(
                Regex("""نص\s+([\d,]+\.?\d*)"""), 1, 2, "Half of", "نصف"),
            "quarter" to FractionEntry(
                Regex("""(?i)(?:a\s+)?quarter\s+of\s+([\d,]+\.?\d*)|([\d,]+\.?\d*)\s+quarter"""), 1, 4, "Quarter of", "ربع"),
            "ربع" to FractionEntry(
                Regex("""ربع\s+([\d,]+\.?\d*)|([\d,]+\.?\d*)\s*ربعه?ا?"""), 1, 4, "Quarter of", "ربع"),
            "third" to FractionEntry(
                Regex("""(?i)(?:a\s+)?third\s+of\s+([\d,]+\.?\d*)"""), 1, 3, "Third of", "ثلث"),
            "ثلث" to FractionEntry(
                Regex("""ثلث\s+([\d,]+\.?\d*)|([\d,]+\.?\d*)\s*ثلثه?ا?"""), 1, 3, "Third of", "ثلث")
        )

        // [\p{L}]+ matches any Unicode letter (works for Arabic)
        private val ARABIC_POSSESSION_PATTERN = Regex(
            """(?:معي|عندي|لدي|معه|معها)\s+([\d,]+\.?\d*)\s*(?:[\p{L}]+\s*)?كم\s+(نصف?ه?ا?|ربعه?ا?|ثلثه?ا?)"""
        )

        private val FRACTION_KEYWORDS = listOf(
            "half", "quarter", "third", "three-quarters",
            "نصف", "نص", "ربع", "ثلث", "ثلاثة أرباع",
            "نصها", "نصه", "ربعها", "ربعه", "ثلثها", "ثلثه"
        )
    }
}
