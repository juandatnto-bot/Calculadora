package com.example.calculator.model

data class HistoryEntry(
    val expression: List<ExpressionToken>,
    val result: String,
)
