package com.example.calculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.R

enum class KeyStyle { Function, Digit, Operator }

private const val FONT_SIZE_RATIO = 0.42f
private const val WIDE_KEY_PADDING_RATIO = 0.4f

@Composable
fun CalculatorKey(
    label: String,
    style: KeyStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    contentAlignment: Alignment = Alignment.Center,
) {
    val (containerColor, contentColor) = keyColors(style, isActive)

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(containerColor)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = contentAlignment,
    ) {
        val horizontalPadding = if (contentAlignment == Alignment.CenterStart) {
            maxHeight * WIDE_KEY_PADDING_RATIO
        } else {
            0.dp
        }
        BasicText(
            text = label,
            modifier = Modifier.padding(horizontal = horizontalPadding),
            style = TextStyle(color = contentColor, fontSize = (maxHeight.value * FONT_SIZE_RATIO).sp),
        )
    }
}

@Composable
private fun keyColors(style: KeyStyle, isActive: Boolean): Pair<Color, Color> = when {
    style == KeyStyle.Operator && isActive -> Pair(
        colorResource(R.color.calc_operator_active_background),
        colorResource(R.color.calc_operator_active_content),
    )
    style == KeyStyle.Operator -> Pair(
        colorResource(R.color.calc_operator_background),
        colorResource(R.color.calc_operator_content),
    )
    style == KeyStyle.Function -> Pair(
        colorResource(R.color.calc_function_background),
        colorResource(R.color.calc_function_content),
    )
    else -> Pair(
        colorResource(R.color.calc_digit_background),
        colorResource(R.color.calc_digit_content),
    )
}
