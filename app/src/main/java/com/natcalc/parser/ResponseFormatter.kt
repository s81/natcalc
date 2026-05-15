package com.natcalc.parser

import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

class ResponseFormatter @Inject constructor() {

    fun formatArithmetic(r: ParseResult.Arithmetic): String {
        val res = r.result.fmt()
        return if (r.isArabic) "النتيجة: $res" else "Result: $res"
    }

    fun formatPercentage(r: ParseResult.Percentage): String {
        val pct = r.percentage.fmt()
        val base = r.baseValue.fmt()
        val res = r.result.fmt()
        return if (r.isArabic) "$pct% من $base = $res" else "$pct% of $base = $res"
    }

    fun formatFraction(r: ParseResult.Fraction): String {
        val value = r.value.fmt()
        val res = r.result.fmt()
        return if (r.isArabic) "${r.fractionLabel} $value = $res" else "${r.fractionLabel} $value = $res"
    }

    fun formatCurrency(
        from: String, to: String,
        amount: Double, result: Double, rate: Double,
        isArabic: Boolean,
        isOffline: Boolean = false
    ): String {
        val fromName = CurrencyMapper.getDisplayName(from, isArabic)
        val toName = CurrencyMapper.getDisplayName(to, isArabic)
        val amtFmt = amount.fmt()
        val resFmt = result.fmtCurrency()
        val rateFmt = rate.fmtCurrency()
        val offlineNote = if (isOffline) {
            if (isArabic) "\n⚠ أسعار تقريبية (وضع عدم الاتصال)" else "\n⚠ Approximate rates (offline mode)"
        } else ""
        return if (isArabic) {
            "$amtFmt $fromName = $resFmt $toName\n(السعر: 1 $fromName = $rateFmt $toName)$offlineNote"
        } else {
            "$amtFmt $fromName = $resFmt $toName\n(Rate: 1 $from = $rateFmt $to)$offlineNote"
        }
    }

    fun formatError(msg: String) = msg

    private fun Double.fmt(): String {
        if (isNaN() || isInfinite()) return "—"
        return when {
            this == kotlin.math.floor(this) && kotlin.math.abs(this) < 1e15 ->
                NumberFormat.getNumberInstance(Locale.US).format(toLong())
            kotlin.math.abs(this) >= 1e12 || (kotlin.math.abs(this) < 1e-4 && this != 0.0) ->
                String.format(Locale.US, "%.4e", this)
            else -> String.format(Locale.US, "%.8f", this).trimEnd('0').trimEnd('.')
        }
    }

    private fun Double.fmtCurrency(): String {
        if (isNaN() || isInfinite()) return "—"
        return String.format(Locale.US, "%,.4f", this).trimEnd('0').trimEnd('.')
    }
}
