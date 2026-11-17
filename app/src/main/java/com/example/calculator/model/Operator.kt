package com.example.calculator.model

enum class Operator {
    Add,
    Subtract,
    Multiply,
    Divide;

    val isAdditive: Boolean
        get() = this == Add || this == Subtract
}
