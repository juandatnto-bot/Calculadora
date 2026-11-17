package com.example.calculator.viewmodel

import com.example.calculator.model.CalculatorError
import com.example.calculator.model.ExpressionToken
import com.example.calculator.model.HistoryEntry
import com.example.calculator.model.Operator

data class CalculatorUiState(
    val displayText: String = "0",
    val expression: List<ExpressionToken> = emptyList(),
    val error: CalculatorError? = null,
    val awaitingOperator: Operator? = null,
    val history: List<HistoryEntry> = emptyList(),
)
