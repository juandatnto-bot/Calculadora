package com.example.calculator.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.R
import com.example.calculator.viewmodel.CalculatorUiState

@Composable
fun DisplayPanel(uiState: CalculatorUiState, modifier: Modifier = Modifier) {
    val error = uiState.error
    val mainText = if (error != null) stringResource(error.messageRes) else uiState.displayText
    val mainColor = colorResource(if (error != null) R.color.calc_text_error else R.color.calc_text_primary)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        BasicText(
            text = uiState.expression.asDisplayText(),
            modifier = Modifier.heightIn(min = 28.dp),
            style = TextStyle(
                color = colorResource(R.color.calc_text_secondary),
                fontSize = 22.sp,
                textAlign = TextAlign.End,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        AutoSizeText(
            text = mainText,
            maxFontSize = 88.sp,
            minFontSize = 28.sp,
            color = mainColor,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
