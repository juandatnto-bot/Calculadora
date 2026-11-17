package com.example.calculator.viewmodel

import androidx.lifecycle.ViewModel
import com.example.calculator.model.CalculatorAction
import com.example.calculator.model.CalculatorEngine
import com.example.calculator.model.CalculatorState
import com.example.calculator.model.HistoryEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {
    private val engine = CalculatorEngine()
    private var calculatorState = CalculatorState()
    private var history: List<HistoryEntry> = emptyList()

    private val _uiState = MutableStateFlow(buildUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onAction(action: CalculatorAction) {
        val transition = engine.reduce(calculatorState, action)
        calculatorState = transition.state
        transition.completedCalculation?.let { history = history + it }
        _uiState.value = buildUiState()
    }

    private fun buildUiState() = CalculatorUiState(
        displayText = calculatorState.displayText,
        expression = calculatorState.expression,
        error = calculatorState.error,
        awaitingOperator = calculatorState.awaitingOperator,
        history = history,
    )
}
