package com.example.calculator.model

import java.math.BigDecimal

enum class InputMode {
    /** The user is typing: digits are appended to the input. */
    Typing,

    /** The input holds a computed value: the next digit starts a new number. */
    Computed,
}

data class RepeatOperation(
    val operator: Operator,
    val operand: BigDecimal,
)

data class CalculatorState(
    val input: String = "0",
    val inputMode: InputMode = InputMode.Typing,
    val storedOperand: BigDecimal? = null,
    val pendingOperator: Operator? = null,
    val isOperandReady: Boolean = false,
    val repeatOperation: RepeatOperation? = null,
    val expression: List<ExpressionToken> = emptyList(),
    val error: CalculatorError? = null,
) {
    val inputValue: BigDecimal
        get() = BigDecimal(input)

    val displayText: String
        get() = when (inputMode) {
            InputMode.Typing -> NumberFormatter.formatTyping(input)
            InputMode.Computed -> NumberFormatter.formatValue(inputValue)
        }

    /** The operator waiting for its right operand, used to highlight its key. */
    val awaitingOperator: Operator?
        get() = pendingOperator.takeIf { !isOperandReady }
}
