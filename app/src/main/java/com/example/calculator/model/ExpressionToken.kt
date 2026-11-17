package com.example.calculator.model

/**
 * A piece of an expression such as "5 + 3 =". Operators are kept semantic so the UI
 * decides how to render them from string resources.
 */
sealed interface ExpressionToken {
    data class Number(val text: String) : ExpressionToken
    data class Symbol(val operator: Operator) : ExpressionToken
    data object Equals : ExpressionToken
}
