package com.example.calculator.model

import java.math.BigDecimal

data class CalculatorTransition(
    val state: CalculatorState,
    val completedCalculation: HistoryEntry? = null,
)

/**
 * Pure calculator state machine following the iOS behavior: operations chain left to right,
 * "=" repeats the last operation, "%" depends on the pending operator and AC resets everything.
 */
class CalculatorEngine {

    fun reduce(state: CalculatorState, action: CalculatorAction): CalculatorTransition {
        if (state.error != null && action != CalculatorAction.Clear) {
            return recoverFromError(state, action)
        }
        return when (action) {
            is CalculatorAction.Digit -> CalculatorTransition(enterDigit(state, action.value))
            CalculatorAction.Decimal -> CalculatorTransition(enterDecimal(state))
            is CalculatorAction.OperatorPressed -> selectOperator(state, action.operator)
            CalculatorAction.Equals -> evaluate(state)
            CalculatorAction.Percent -> applyPercent(state)
            CalculatorAction.ToggleSign -> CalculatorTransition(toggleSign(state))
            CalculatorAction.Clear -> CalculatorTransition(CalculatorState())
        }
    }

    private fun recoverFromError(state: CalculatorState, action: CalculatorAction): CalculatorTransition =
        when (action) {
            is CalculatorAction.Digit, CalculatorAction.Decimal -> reduce(CalculatorState(), action)
            else -> CalculatorTransition(state)
        }

    private fun enterDigit(state: CalculatorState, digit: Int): CalculatorState {
        val current = state.restartIfFinished()
        val nextInput = when {
            current.inputMode == InputMode.Computed -> digit.toString()
            current.input == "0" -> digit.toString()
            current.input == "-0" -> "-$digit"
            current.input.count { it.isDigit() } >= MAX_INPUT_DIGITS -> current.input
            else -> current.input + digit
        }
        return current.copy(input = nextInput, inputMode = InputMode.Typing, isOperandReady = true)
    }

    private fun enterDecimal(state: CalculatorState): CalculatorState {
        val current = state.restartIfFinished()
        val nextInput = when {
            current.inputMode == InputMode.Computed -> "0."
            '.' in current.input -> current.input
            else -> current.input + "."
        }
        return current.copy(input = nextInput, inputMode = InputMode.Typing, isOperandReady = true)
    }

    private fun selectOperator(state: CalculatorState, operator: Operator): CalculatorTransition {
        val pending = state.pendingOperator
        if (pending != null && !state.isOperandReady) {
            val replaced = state.expression.dropLast(1) + ExpressionToken.Symbol(operator)
            return CalculatorTransition(state.copy(pendingOperator = operator, expression = replaced))
        }

        val operand = state.inputValue
        val operandToken = ExpressionToken.Number(NumberFormatter.formatValue(operand))
        if (pending == null) {
            val expression = listOf(operandToken, ExpressionToken.Symbol(operator))
            return CalculatorTransition(awaitingOperand(operand, operator, expression))
        }

        val stored = checkNotNull(state.storedOperand)
        return when (val result = ArithmeticCalculator.calculate(stored, pending, operand)) {
            is CalculationResult.Failure -> failed(result.error)
            is CalculationResult.Success -> {
                val expression = state.expression + operandToken + ExpressionToken.Symbol(operator)
                CalculatorTransition(awaitingOperand(result.value, operator, expression))
            }
        }
    }

    private fun evaluate(state: CalculatorState): CalculatorTransition {
        val pending = state.pendingOperator
        val repeat = state.repeatOperation
        val left: BigDecimal
        val operator: Operator
        val operand: BigDecimal
        when {
            pending != null -> {
                left = checkNotNull(state.storedOperand)
                operator = pending
                operand = state.inputValue
            }
            repeat != null -> {
                left = state.inputValue
                operator = repeat.operator
                operand = repeat.operand
            }
            else -> return CalculatorTransition(state)
        }

        val operandToken = ExpressionToken.Number(NumberFormatter.formatValue(operand))
        val expression = if (pending != null) {
            state.expression + operandToken
        } else {
            listOf(
                ExpressionToken.Number(NumberFormatter.formatValue(left)),
                ExpressionToken.Symbol(operator),
                operandToken,
            )
        }

        return when (val result = ArithmeticCalculator.calculate(left, operator, operand)) {
            is CalculationResult.Failure -> failed(result.error)
            is CalculationResult.Success -> {
                val finished = CalculatorState(
                    input = NumberFormatter.toInputText(result.value),
                    inputMode = InputMode.Computed,
                    repeatOperation = RepeatOperation(operator, operand),
                    expression = expression + ExpressionToken.Equals,
                )
                val entry = HistoryEntry(expression, NumberFormatter.formatValue(result.value))
                CalculatorTransition(finished, entry)
            }
        }
    }

    private fun applyPercent(state: CalculatorState): CalculatorTransition {
        val base = state.storedOperand.takeIf { state.pendingOperator?.isAdditive == true }
        return when (val result = ArithmeticCalculator.percentage(state.inputValue, base)) {
            is CalculationResult.Failure -> failed(result.error)
            is CalculationResult.Success -> CalculatorTransition(
                state.copy(
                    input = NumberFormatter.toInputText(result.value),
                    inputMode = InputMode.Computed,
                    isOperandReady = state.pendingOperator != null,
                ),
            )
        }
    }

    private fun toggleSign(state: CalculatorState): CalculatorState = when {
        state.pendingOperator != null && !state.isOperandReady ->
            state.copy(input = "-0", inputMode = InputMode.Typing, isOperandReady = true)
        state.inputMode == InputMode.Computed && state.inputValue.signum() == 0 -> state
        state.input.startsWith("-") -> state.copy(input = state.input.removePrefix("-"))
        else -> state.copy(input = "-" + state.input)
    }

    private fun awaitingOperand(
        value: BigDecimal,
        operator: Operator,
        expression: List<ExpressionToken>,
    ) = CalculatorState(
        input = NumberFormatter.toInputText(value),
        inputMode = InputMode.Computed,
        storedOperand = value,
        pendingOperator = operator,
        expression = expression,
    )

    private fun failed(error: CalculatorError) = CalculatorTransition(CalculatorState(error = error))

    private fun CalculatorState.restartIfFinished(): CalculatorState =
        if (inputMode == InputMode.Computed && pendingOperator == null) CalculatorState() else this

    private companion object {
        const val MAX_INPUT_DIGITS = 9
    }
}
