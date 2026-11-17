package com.example.calculator

import com.example.calculator.model.CalculatorAction
import com.example.calculator.model.CalculatorEngine
import com.example.calculator.model.CalculatorError
import com.example.calculator.model.CalculatorState
import com.example.calculator.model.ExpressionToken
import com.example.calculator.model.HistoryEntry
import com.example.calculator.model.Operator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalculatorEngineTest {
    private val engine = CalculatorEngine()

    private class Run(val state: CalculatorState, val history: List<HistoryEntry>) {
        val display: String get() = state.displayText
    }

    private fun press(keys: String): Run {
        var state = CalculatorState()
        val history = mutableListOf<HistoryEntry>()
        keys.forEach { key ->
            val transition = engine.reduce(state, key.toAction())
            state = transition.state
            transition.completedCalculation?.let(history::add)
        }
        return Run(state, history)
    }

    private fun Char.toAction(): CalculatorAction = when (this) {
        in '0'..'9' -> CalculatorAction.Digit(this - '0')
        '.' -> CalculatorAction.Decimal
        '+' -> CalculatorAction.OperatorPressed(Operator.Add)
        '-' -> CalculatorAction.OperatorPressed(Operator.Subtract)
        '*' -> CalculatorAction.OperatorPressed(Operator.Multiply)
        '/' -> CalculatorAction.OperatorPressed(Operator.Divide)
        '=' -> CalculatorAction.Equals
        '%' -> CalculatorAction.Percent
        'n' -> CalculatorAction.ToggleSign
        'C' -> CalculatorAction.Clear
        else -> error("Unknown key $this")
    }

    private fun HistoryEntry.render(): String {
        val text = expression.joinToString(" ") { token ->
            when (token) {
                is ExpressionToken.Number -> token.text
                is ExpressionToken.Symbol -> token.operator.name
                ExpressionToken.Equals -> "="
            }
        }
        return "$text -> $result"
    }

    @Test
    fun `adds two numbers and records the operation`() {
        val run = press("5+3=")
        assertEquals("8", run.display)
        assertEquals(listOf("5 Add 3 -> 8"), run.history.map { it.render() })
    }

    @Test
    fun `chains operations from left to right`() {
        val run = press("2+3*4=")
        assertEquals("20", run.display)
        assertEquals(listOf("2 Add 3 Multiply 4 -> 20"), run.history.map { it.render() })
    }

    @Test
    fun `decimals are exact`() {
        assertEquals("0.3", press("0.1+0.2=").display)
        assertEquals("0.5", press(".5").display)
    }

    @Test
    fun `ignores a second decimal separator`() {
        assertEquals("1.23", press("1.2.3").display)
    }

    @Test
    fun `groups thousands and limits the number of digits`() {
        assertEquals("1,234,567", press("1234567").display)
        assertEquals("123,456,789", press("123456789012").display)
    }

    @Test
    fun `division by zero reports an error and does not touch the history`() {
        val run = press("8/0=")
        assertEquals(CalculatorError.DivisionByZero, run.state.error)
        assertEquals(emptyList<HistoryEntry>(), run.history)
    }

    @Test
    fun `division by zero in a chain is reported when the next operator is pressed`() {
        assertEquals(CalculatorError.DivisionByZero, press("8/0+").state.error)
    }

    @Test
    fun `a digit recovers from an error but an operator does not`() {
        assertEquals(CalculatorError.DivisionByZero, press("8/0=+").state.error)
        val recovered = press("8/0=7")
        assertNull(recovered.state.error)
        assertEquals("7", recovered.display)
    }

    @Test
    fun `clear resets everything`() {
        val run = press("8+3=C")
        assertEquals(CalculatorState(), run.state)
        assertEquals("0", run.display)
    }

    @Test
    fun `repeated equals repeats the last operation`() {
        val run = press("2+3==")
        assertEquals("8", run.display)
        assertEquals(listOf("2 Add 3 -> 5", "5 Add 3 -> 8"), run.history.map { it.render() })
    }

    @Test
    fun `equals right after an operator uses the displayed value as operand`() {
        assertEquals("10", press("5+=").display)
    }

    @Test
    fun `equals without an operation changes nothing`() {
        val run = press("5=")
        assertEquals("5", run.display)
        assertEquals(emptyList<HistoryEntry>(), run.history)
    }

    @Test
    fun `pressing another operator replaces the pending one`() {
        assertEquals("2", press("5+-3=").display)
    }

    @Test
    fun `a new number after equals starts a new calculation`() {
        val run = press("2+3=7+1=")
        assertEquals("8", run.display)
        assertEquals(2, run.history.size)
    }

    @Test
    fun `an operator after equals continues with the result`() {
        assertEquals("15", press("2+3=*3=").display)
    }

    @Test
    fun `percent of a number divides by one hundred`() {
        assertEquals("0.5", press("50%").display)
    }

    @Test
    fun `percent after plus or minus is relative to the first operand`() {
        assertEquals("220", press("200+10%=").display)
        assertEquals("180", press("200-10%=").display)
    }

    @Test
    fun `percent after multiply is a plain percentage`() {
        assertEquals("10", press("200*5%=").display)
    }

    @Test
    fun `toggle sign flips the typed number`() {
        assertEquals("-5", press("5n").display)
        assertEquals("5", press("5nn").display)
        assertEquals("-0", press("n").display)
        assertEquals("-3", press("n3").display)
    }

    @Test
    fun `toggle sign works on a result and on a fresh operand`() {
        assertEquals("-8", press("5+3=n").display)
        assertEquals("2", press("5+n3=").display)
    }

    @Test
    fun `awaiting operator is exposed only until the operand is entered`() {
        assertEquals(Operator.Add, press("5+").state.awaitingOperator)
        assertNull(press("5+3").state.awaitingOperator)
    }

    @Test
    fun `division keeps a stable decimal result`() {
        assertEquals("2.5", press("10/4=").display)
        assertEquals("1", press("1/3*3=").display)
    }

    @Test
    fun `huge results are reported instead of overflowing`() {
        val run = press("999999999*999999999" + "=".repeat(12))
        assertEquals(CalculatorError.ResultTooLarge, run.state.error)
    }

    @Test
    fun `large values use scientific notation on the display`() {
        assertEquals("1E+9", press("999999999+1=").display)
    }
}
