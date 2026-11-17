package com.example.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.R
import com.example.calculator.model.CalculatorAction
import com.example.calculator.ui.components.DisplayPanel
import com.example.calculator.ui.components.KeySpacing
import com.example.calculator.ui.components.Keypad
import com.example.calculator.viewmodel.CalculatorUiState

private const val KEYPAD_COLUMNS = 4
private const val KEYPAD_ROWS = 5
private const val KEYPAD_MAX_HEIGHT_SHARE = 0.68f

@Composable
fun CalculatorScreen(
    uiState: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.calc_background))
            .safeDrawingPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            BasicText(
                text = stringResource(R.string.history_title),
                modifier = Modifier
                    .clickable(role = Role.Button, onClick = onOpenHistory)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                style = TextStyle(color = colorResource(R.color.calc_operator_background), fontSize = 18.sp),
            )
        }
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val gaps = KeySpacing * (KEYPAD_COLUMNS - 1)
            val widthBasedSize = (maxWidth - gaps) / KEYPAD_COLUMNS
            val heightBasedSize = (maxHeight * KEYPAD_MAX_HEIGHT_SHARE - KeySpacing * (KEYPAD_ROWS - 1)) / KEYPAD_ROWS
            val keySize = minOf(widthBasedSize, heightBasedSize).coerceAtLeast(0.dp)

            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                DisplayPanel(
                    uiState = uiState,
                    modifier = Modifier.weight(1f).padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
                )
                Keypad(
                    awaitingOperator = uiState.awaitingOperator,
                    keySize = keySize,
                    onAction = onAction,
                )
            }
        }
    }
}
