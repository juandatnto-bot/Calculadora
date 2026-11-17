package com.example.calculator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.calculator.R
import com.example.calculator.model.CalculatorAction
import com.example.calculator.model.Operator

val KeySpacing = 12.dp

@Composable
fun Keypad(
    awaitingOperator: Operator?,
    keySize: Dp,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyModifier = Modifier.size(keySize)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(KeySpacing)) {
        KeyRow {
            FunctionKey(R.string.key_clear, keyModifier) { onAction(CalculatorAction.Clear) }
            FunctionKey(R.string.key_toggle_sign, keyModifier) { onAction(CalculatorAction.ToggleSign) }
            FunctionKey(R.string.key_percent, keyModifier) { onAction(CalculatorAction.Percent) }
            OperatorKey(Operator.Divide, awaitingOperator, keyModifier, onAction)
        }
        KeyRow {
            DigitKeys(7..9, keyModifier, onAction)
            OperatorKey(Operator.Multiply, awaitingOperator, keyModifier, onAction)
        }
        KeyRow {
            DigitKeys(4..6, keyModifier, onAction)
            OperatorKey(Operator.Subtract, awaitingOperator, keyModifier, onAction)
        }
        KeyRow {
            DigitKeys(1..3, keyModifier, onAction)
            OperatorKey(Operator.Add, awaitingOperator, keyModifier, onAction)
        }
        KeyRow {
            CalculatorKey(
                label = stringResource(R.string.key_digit, 0),
                style = KeyStyle.Digit,
                onClick = { onAction(CalculatorAction.Digit(0)) },
                modifier = Modifier.size(width = keySize * 2 + KeySpacing, height = keySize),
                contentAlignment = Alignment.CenterStart,
            )
            CalculatorKey(
                label = stringResource(R.string.key_decimal),
                style = KeyStyle.Digit,
                onClick = { onAction(CalculatorAction.Decimal) },
                modifier = keyModifier,
            )
            CalculatorKey(
                label = stringResource(R.string.key_equals),
                style = KeyStyle.Operator,
                onClick = { onAction(CalculatorAction.Equals) },
                modifier = keyModifier,
            )
        }
    }
}

@Composable
private fun KeyRow(content: @Composable RowScope.() -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(KeySpacing), content = content)
}

@Composable
private fun FunctionKey(labelRes: Int, modifier: Modifier, onClick: () -> Unit) {
    CalculatorKey(
        label = stringResource(labelRes),
        style = KeyStyle.Function,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun DigitKeys(digits: IntRange, modifier: Modifier, onAction: (CalculatorAction) -> Unit) {
    digits.forEach { digit ->
        CalculatorKey(
            label = stringResource(R.string.key_digit, digit),
            style = KeyStyle.Digit,
            onClick = { onAction(CalculatorAction.Digit(digit)) },
            modifier = modifier,
        )
    }
}

@Composable
private fun OperatorKey(
    operator: Operator,
    awaitingOperator: Operator?,
    modifier: Modifier,
    onAction: (CalculatorAction) -> Unit,
) {
    CalculatorKey(
        label = stringResource(operator.labelRes),
        style = KeyStyle.Operator,
        onClick = { onAction(CalculatorAction.OperatorPressed(operator)) },
        modifier = modifier,
        isActive = operator == awaitingOperator,
    )
}
