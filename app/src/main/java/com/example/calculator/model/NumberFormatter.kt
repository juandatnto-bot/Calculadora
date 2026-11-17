package com.example.calculator.model

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Locale independent formatting: "." is always the decimal separator and "," the grouping one,
 * so the display, the history and the internal values never disagree.
 */
object NumberFormatter {
    private const val DISPLAY_DIGITS = 9
    private const val GROUP_SIZE = 3
    private val DISPLAY_CONTEXT = MathContext(DISPLAY_DIGITS, RoundingMode.HALF_UP)
    private val PLAIN_UPPER_LIMIT = BigDecimal("1E9")
    private val PLAIN_LOWER_LIMIT = BigDecimal("1E-8")

    /** Formats text that the user is typing, preserving trailing zeros and a trailing ".". */
    fun formatTyping(rawInput: String): String {
        val sign = if (rawInput.startsWith("-")) "-" else ""
        val unsigned = rawInput.removePrefix("-")
        val integerPart = unsigned.substringBefore('.')
        val fractionPart = if ('.' in unsigned) "." + unsigned.substringAfter('.') else ""
        return sign + groupThousands(integerPart) + fractionPart
    }

    /** Formats a computed value, rounded to what fits on the display. */
    fun formatValue(value: BigDecimal): String {
        if (value.signum() == 0) return "0"
        val stripped = value.round(DISPLAY_CONTEXT).stripTrailingZeros()
        val magnitude = stripped.abs()
        val needsScientificNotation = magnitude >= PLAIN_UPPER_LIMIT || magnitude < PLAIN_LOWER_LIMIT
        return if (needsScientificNotation) stripped.toString() else formatTyping(stripped.toPlainString())
    }

    /** Canonical text stored in the calculator state for a computed value. */
    fun toInputText(value: BigDecimal): String =
        if (value.signum() == 0) "0" else value.stripTrailingZeros().toPlainString()

    private fun groupThousands(digits: String): String =
        digits.reversed().chunked(GROUP_SIZE).joinToString(",").reversed()
}
