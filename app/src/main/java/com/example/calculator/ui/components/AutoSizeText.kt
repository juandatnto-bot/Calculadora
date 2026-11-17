package com.example.calculator.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit

private const val SHRINK_FACTOR = 0.9f

/** Single line text that shrinks until it fits the available width. */
@Composable
fun AutoSizeText(
    text: String,
    maxFontSize: TextUnit,
    minFontSize: TextUnit,
    color: Color,
    modifier: Modifier = Modifier,
) {
    var fontSize by remember(text) { mutableStateOf(maxFontSize) }

    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(color = color, fontSize = fontSize, textAlign = TextAlign.End),
        maxLines = 1,
        softWrap = false,
        onTextLayout = { result ->
            if (result.didOverflowWidth && fontSize.value > minFontSize.value) {
                fontSize = (fontSize * SHRINK_FACTOR).coerceAtLeast(minFontSize)
            }
        },
    )
}

private fun TextUnit.coerceAtLeast(minimum: TextUnit): TextUnit =
    if (value < minimum.value) minimum else this
