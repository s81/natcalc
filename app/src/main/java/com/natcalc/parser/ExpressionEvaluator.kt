package com.natcalc.parser

import java.math.BigDecimal
import java.math.MathContext
import kotlin.math.sqrt

object ExpressionEvaluator {

    fun evaluate(expr: String): Double = Parser(expr).parse()

    private class Parser(raw: String) {
        private val expr = raw.replace(",", "").replace("×", "*").replace("÷", "/").trim()
        private var pos = 0

        fun parse(): Double {
            val result = parseExpression()
            skipSpaces()
            if (pos < expr.length) throw ExpressionException("Unexpected token at $pos: '${expr[pos]}'")
            return result
        }

        private fun parseExpression(): Double {
            var result = parseTerm()
            while (true) {
                skipSpaces()
                result = when {
                    pos < expr.length && expr[pos] == '+' -> { pos++; result + parseTerm() }
                    pos < expr.length && expr[pos] == '-' -> { pos++; result - parseTerm() }
                    else -> break
                }
            }
            return result
        }

        private fun parseTerm(): Double {
            var result = parsePower()
            while (true) {
                skipSpaces()
                result = when {
                    pos < expr.length && expr[pos] == '*' -> { pos++; result * parsePower() }
                    pos < expr.length && expr[pos] == '/' -> {
                        pos++
                        val d = parsePower()
                        if (d == 0.0) throw ExpressionException("Division by zero")
                        result / d
                    }
                    pos < expr.length && expr[pos] == '%' -> { pos++; result / 100.0 }
                    else -> break
                }
            }
            return result
        }

        private fun parsePower(): Double {
            val base = parseUnary()
            skipSpaces()
            return if (pos < expr.length && expr[pos] == '^') {
                pos++
                Math.pow(base, parseUnary())
            } else base
        }

        private fun parseUnary(): Double {
            skipSpaces()
            return when {
                pos < expr.length && expr[pos] == '-' -> { pos++; -parsePrimary() }
                pos < expr.length && expr[pos] == '+' -> { pos++; parsePrimary() }
                else -> parsePrimary()
            }
        }

        private fun parsePrimary(): Double {
            skipSpaces()
            if (pos >= expr.length) throw ExpressionException("Unexpected end of expression")

            if (expr.startsWith("sqrt", pos) || (pos < expr.length && expr[pos] == '√')) {
                pos += if (expr[pos] == '√') 1 else 4
                val arg = parsePrimary()
                if (arg < 0) throw ExpressionException("Cannot compute sqrt of negative number")
                return sqrt(arg)
            }

            return if (expr[pos] == '(') {
                pos++
                val v = parseExpression()
                skipSpaces()
                if (pos >= expr.length || expr[pos] != ')') throw ExpressionException("Missing ')'")
                pos++
                v
            } else {
                parseNumber()
            }
        }

        private fun parseNumber(): Double {
            skipSpaces()
            val start = pos
            while (pos < expr.length && (expr[pos].isDigit() || expr[pos] == '.')) pos++
            if (pos == start) throw ExpressionException("Expected number at position $pos")
            return expr.substring(start, pos).toDoubleOrNull()
                ?: throw ExpressionException("Invalid number: ${expr.substring(start, pos)}")
        }

        private fun skipSpaces() {
            while (pos < expr.length && expr[pos] == ' ') pos++
        }
    }
}

class ExpressionException(message: String) : Exception(message)
