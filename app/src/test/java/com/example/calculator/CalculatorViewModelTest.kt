package com.example.calculator

import com.example.calculator.model.CalculatorAction
import com.example.calculator.model.CalculatorError
import com.example.calculator.model.Operator
import com.example.calculator.viewmodel.CalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorViewModelTest {
    private val viewModel = CalculatorViewModel()

    private fun calculateFiveTimesTwo() {
        viewModel.onAction(CalculatorAction.Digit(5))
        viewModel.onAction(CalculatorAction.OperatorPressed(Operator.Multiply))
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.Equals)
    }

    @Test
    fun `starts with an empty history and zero on the display`() {
        val state = viewModel.uiState.value
        assertEquals("0", state.displayText)
        assertTrue(state.history.isEmpty())
    }

    @Test
    fun `completed operations are appended to the history in order`() {
        calculateFiveTimesTwo()
        calculateFiveTimesTwo()
        val history = viewModel.uiState.value.history
        assertEquals(listOf("10", "10"), history.map { it.result })
    }

    @Test
    fun `clearing the calculator keeps the history`() {
        calculateFiveTimesTwo()
        viewModel.onAction(CalculatorAction.Clear)
        val state = viewModel.uiState.value
        assertEquals("0", state.displayText)
        assertEquals(1, state.history.size)
    }

    @Test
    fun `an invalid operation exposes an error without adding to the history`() {
        viewModel.onAction(CalculatorAction.Digit(4))
        viewModel.onAction(CalculatorAction.OperatorPressed(Operator.Divide))
        viewModel.onAction(CalculatorAction.Digit(0))
        viewModel.onAction(CalculatorAction.Equals)
        val state = viewModel.uiState.value
        assertEquals(CalculatorError.DivisionByZero, state.error)
        assertTrue(state.history.isEmpty())
    }
}
