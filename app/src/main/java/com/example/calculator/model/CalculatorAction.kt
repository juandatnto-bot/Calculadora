package com.example.calculator.model

sealed interface CalculatorAction {
    data class Digit(val value: Int) : CalculatorAction {
        init {
            require(value in 0..9) { "A digit must be between 0 and 9" }
        }
    }

    data class OperatorPressed(val operator: Operator) : CalculatorAction
    data object Decimal : CalculatorAction
    data object Equals : CalculatorAction
    data object Clear : CalculatorAction
    data object ToggleSign : CalculatorAction
    data object Percent : CalculatorAction
}
