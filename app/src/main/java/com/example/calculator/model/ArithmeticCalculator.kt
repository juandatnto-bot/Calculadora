package com.example.calculator.model

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

sealed interface CalculationResult {
    data class Success(val value: BigDecimal) : CalculationResult
    data class Failure(val error: CalculatorError) : CalculationResult
}

object ArithmeticCalculator {
    private val CALCULATION_CONTEXT = MathContext(12, RoundingMode.HALF_UP)
    private val MAX_MAGNITUDE = BigDecimal.TEN.pow(99)
    private val ONE_HUNDRED = BigDecimal(100)

    fun calculate(left: BigDecimal, operator: Operator, right: BigDecimal): CalculationResult =
        when (operator) {
            Operator.Add -> normalize(left.add(right))
            Operator.Subtract -> normalize(left.subtract(right))
            Operator.Multiply -> normalize(left.multiply(right))
            Operator.Divide ->
                if (right.signum() == 0) {
                    CalculationResult.Failure(CalculatorError.DivisionByZero)
                } else {
                    normalize(left.divide(right, CALCULATION_CONTEXT))
                }
        }

    /**
     * Percentage of [value]. With a [base] (the left operand of an addition or subtraction)
     * it is a percentage of that base, otherwise it is simply value / 100.
     */
    fun percentage(value: BigDecimal, base: BigDecimal?): CalculationResult {
        val scaled = if (base == null) value else base.multiply(value)
        return normalize(scaled.divide(ONE_HUNDRED, CALCULATION_CONTEXT))
    }

    private fun normalize(value: BigDecimal): CalculationResult {
        val rounded = value.round(CALCULATION_CONTEXT)
        return if (rounded.abs() >= MAX_MAGNITUDE) {
            CalculationResult.Failure(CalculatorError.ResultTooLarge)
        } else {
            CalculationResult.Success(rounded)
        }
    }
}
