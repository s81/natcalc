package com.natcalc.parser

import javax.inject.Inject

class PercentageParser @Inject constructor() {

    fun canHandle(input: String): Boolean =
        PERCENT_KEYWORD.containsMatchIn(input) && NumberExtractor.extractAll(input).size >= 2

    fun parse(input: String, isArabic: Boolean): ParseResult {
        val numbers = NumberExtractor.extractAll(input)
        if (numbers.size < 2) {
            return ParseResult.Error(
                if (isArabic) "أدخل النسبة المئوية والقيمة" else "Please provide both percentage and value",
                isArabic
            )
        }

        // Determine which number is the percentage and which is the base
        val (pct, base) = when {
            // Pattern: "X% of Y" or "X بالمية من Y" — first number is pct, second is base
            OF_PATTERN.containsMatchIn(input) -> Pair(numbers[0], numbers[1])
            // Pattern: "Y من X%" or "من X بالمية من Y"
            else -> Pair(numbers[0], numbers[1])
        }

        val result = pct / 100.0 * base
        return ParseResult.Percentage(percentage = pct, baseValue = base, result = result, isArabic = isArabic)
    }

    companion object {
        private val PERCENT_KEYWORD = Regex("""(?i)%|percent|بالمية|بالمئة|بالمائة""")
        private val OF_PATTERN = Regex("""(?i)\bof\b|من""")
    }
}
