package com.example.calculator.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.calculator.R
import com.example.calculator.model.CalculatorError
import com.example.calculator.model.ExpressionToken
import com.example.calculator.model.Operator

@get:StringRes
val Operator.labelRes: Int
    get() = when (this) {
        Operator.Add -> R.string.key_add
        Operator.Subtract -> R.string.key_subtract
        Operator.Multiply -> R.string.key_multiply
        Operator.Divide -> R.string.key_divide
    }

@get:StringRes
val CalculatorError.messageRes: Int
    get() = when (this) {
        CalculatorError.DivisionByZero -> R.string.error_division_by_zero
        CalculatorError.ResultTooLarge -> R.string.error_result_too_large
    }

@Composable
fun List<ExpressionToken>.asDisplayText(): String = buildString {
    this@asDisplayText.forEachIndexed { index, token ->
        if (index > 0) append(' ')
        append(
            when (token) {
                is ExpressionToken.Number -> token.text
                is ExpressionToken.Symbol -> stringResource(token.operator.labelRes)
                ExpressionToken.Equals -> stringResource(R.string.key_equals)
            },
        )
    }
}
