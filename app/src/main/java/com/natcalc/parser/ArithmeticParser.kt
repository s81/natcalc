package com.natcalc.parser

import javax.inject.Inject

class ArithmeticParser @Inject constructor() {

    fun canHandle(input: String): Boolean {
        val norm = normalize(input)
        return EXPR_REGEX.containsMatchIn(norm)
    }

    fun parse(input: String, isArabic: Boolean): ParseResult {
        return try {
            val norm = normalize(input)
            val expr = extractMathExpression(norm)
            val result = ExpressionEvaluator.evaluate(expr)
            if (result.isNaN() || result.isInfinite()) {
                ParseResult.Error(
                    if (isArabic) "النتيجة غير محددة" else "Result is undefined",
                    isArabic
                )
            } else {
                ParseResult.Arithmetic(expression = expr, result = result, isArabic = isArabic)
            }
        } catch (e: ExpressionException) {
            ParseResult.Error(
                if (isArabic) "خطأ: ${e.message}" else "Error: ${e.message}",
                isArabic
            )
        } catch (e: Exception) {
            ParseResult.Error(
                if (isArabic) "لم أتمكن من حساب هذه العملية" else "Cannot compute this expression",
                isArabic
            )
        }
    }

    private fun normalize(input: String): String {
        var s = input
            .replace(Regex("[?؟!،,]"), " ")
            .replace("×", "*").replace("÷", "/")
            .replace(Regex("(?i)\\b(what is|calculate|compute|find|please)\\b"), "")
            .replace(Regex("احسب|احسب لي|ما هو|ما نتيجة|ما حاصل"), "")

        ENGLISH_OPS.forEach { (word, sym) ->
            s = Regex("(?i)\\b$word\\b").replace(s, " $sym ")
        }
        ARABIC_OPS.forEach { (word, sym) ->
            s = s.replace(word, " $sym ")
        }

        return s.replace(Regex("\\s+"), " ").trim()
    }

    private fun extractMathExpression(normalized: String): String {
        // Keep only characters valid in a math expression
        val clean = normalized.filter { it.isDigit() || it in "+-*/^(). " }.trim()
        if (clean.isEmpty() || !EXPR_REGEX.containsMatchIn(clean)) {
            throw ExpressionException("No valid expression found")
        }
        return clean.replace(Regex("\\s+"), " ").trim()
    }

    companion object {
        private val EXPR_REGEX = Regex("""\d+\.?\d*\s*[+\-*/^]\s*\d+\.?\d*""")

        private val ENGLISH_OPS = mapOf(
            "plus" to "+", "add" to "+", "added to" to "+",
            "minus" to "-", "subtract" to "-", "take away" to "-",
            "times" to "*", "multiplied by" to "*", "multiply" to "*",
            "divided by" to "/", "over" to "/", "divide" to "/"
        )

        private val ARABIC_OPS = mapOf(
            "زائد" to "+", "يزيد" to "+",
            "ناقص" to "-",
            "مضروب في" to "*", "ضرب" to "*",
            "مقسوم على" to "/", "قسمة" to "/",
            "أس" to "^"
        )
    }
}
