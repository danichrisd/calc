package com.calc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calc.app.data.CalculationHistory
import com.calc.app.data.CalculationType
import com.calc.app.math.ExpressionEvaluator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.min

class CalculatorViewModel : ViewModel() {

    data class UiState(
        val expression: String = "",
        val result: String = "",
        val isDegrees: Boolean = true
    ) {
        val displayExpression: String
            get() {
                val formatter = DecimalFormat("#,###.##########")
                val parts = expression.split(Regex("(?<=[^0-9.])|(?=[^0-9.])"))

                val formattedParts = parts.map { part ->
                    // Check if the part is a number and format it
                    if (part.endsWith('.') && part.dropLast(1).toDoubleOrNull() != null) {
                        val numberPart = part.dropLast(1)
                        formatter.format(numberPart.toDouble()) + "."
                    } else {
                        part.toDoubleOrNull()?.let {
                            formatter.format(it)
                        } ?: part // Otherwise, return the part as is (operator, function name, etc.)
                    }
                }

                return formattedParts.joinToString("")
                    .replace("/", "÷")
                    .replace("*", "×")
                    .replace("sqrt(", "√(")
                    .replace("cbrt(", "³√(")
                    .replace("sin(", "sin(")
                    .replace("cos(", "cos(")
                    .replace("tan(", "tan(")
                    .replace("asin(", "sin⁻¹(")
                    .replace("acos(", "cos⁻¹(")
                    .replace("atan(", "tan⁻¹(")
                    .replace("sinh(", "sinh(")
                    .replace("cosh(", "cosh(")
                    .replace("tanh(", "tanh(")
                    .replace("asinh(", "sinh⁻¹(")
                    .replace("acosh(", "cosh⁻¹(")
                    .replace("atanh(", "tanh⁻¹(")
                    .replace("ln(", "ln(")
                    .replace("log(", "log(")
                    .replace("exp(", "eˣ(")
                    .replace("abs(", "|(")
                    .replace("pi", "π")
                    .replace("e", "e")
                    .replace("^2", "²")
                    .replace("^3", "³")
            }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private var evalJob: Job? = null
    private var isResultDisplayed = false

    fun onKey(key: CalculatorKey) {
        // AC should always reset the state immediately.
        if (key == CalculatorKey.AC) {
            setState { copy(expression = "", result = "") }
            isResultDisplayed = false
            reeval()
            return
        }

        // If a result is showing, the next key press requires special logic.
        if (isResultDisplayed && key != CalculatorKey.Equals) {
            val currentResult = _uiState.value.expression
            isResultDisplayed = false // Flag is reset on any key press after result.

            when {
                // Case 1: A digit is pressed. This starts a completely new calculation.
                key.display.length == 1 && key.display.first().isDigit() -> {
                    setState { copy(expression = key.expression, result = "") }
                }
                // Case 2: A function with parenthesis is pressed (e.g., sin, sqrt, log).
                // These should wrap the previous result.
                key.expression.endsWith("(") -> {
                    setState { copy(expression = key.expression + currentResult, result = "") }
                }
                // Case 3: An operator or a postfix function is pressed.
                // This continues the calculation with the previous result.
                else -> {
                    setState { copy(expression = currentResult + key.expression, result = "") }
                }
            }
            reeval() // Re-evaluate the newly formed expression.
            return // The key press has been fully handled.
        }

        // Standard key handling for when a result is not displayed.
        when (key) {
            CalculatorKey.C -> {
                backspace()
                isResultDisplayed = false
            }
            CalculatorKey.Equals -> evaluateAndCommit()
            CalculatorKey.Sign -> toggleSign()
            CalculatorKey.Reciprocal -> applyReciprocal()
            CalculatorKey.Fact -> applyFactorial()
            CalculatorKey.Parentheses -> appendParenthesis()
            else -> append(key.expression)
        }

        if (key != CalculatorKey.Equals) {
            reeval()
        }
    }

    private fun setState(transform: UiState.() -> UiState) {
        _uiState.value = _uiState.value.transform()
    }

    private fun append(text: String) {
        val current = _uiState.value.expression
        val newExpression = when (text) {
            in "0".."9" -> {
                if (text == "0") {
                    val lastNumIndex = lastNumberStartIndex(current)
                    val lastNum = current.substring(lastNumIndex)
                    if (lastNum == "0") current else current + text
                } else {
                    if (current.isNotEmpty() && current.last() == '0' &&
                        (current.length == 1 || !current[current.length - 2].isDigit())
                    ) {
                        current.dropLast(1) + text
                    } else {
                        current + text
                    }
                }
            }

            "." -> {
                val lastNumIndex = lastNumberStartIndex(current)
                val lastNum = current.substring(lastNumIndex)
                if ("." in lastNum || (current.isNotEmpty() && current.last() == ')')) {
                    current
                } else if (current.isEmpty() || current.last()
                        .let { it !in '0'..'9' && it != '%' }
                ) {
                    current + "0."
                } else {
                    current + "."
                }
            }

            "%" -> {
                if (current.isNotEmpty() && (current.last().isDigit() || current.last() == ')')) {
                    current + text
                } else {
                    current
                }
            }

            in listOf("/", "*", "+", "^", "-") -> {
                if (current.isEmpty()) {
                    if (text == "-") current + text else current
                } else {
                    val lastChar = current.last()
                    if (lastChar == '(') {
                        if (text == "-") current + text else current
                    } else if (lastChar.toString() in listOf("/", "*", "^") && text == "-") {
                        current + text
                    } else if (lastChar.toString() in listOf("/", "*", "+", "^", "-")) {
                        current.dropLast(1) + text
                    } else {
                        current + text
                    }
                }
            }

            else -> current + text
        }
        setState { copy(expression = newExpression) }
    }

    private fun backspace() {
        val expr = _uiState.value.expression
        if (expr.isEmpty()) return
        val newExpr = when {
            expr.endsWith("sqrt(") -> expr.dropLast(5)
            expr.endsWith("asin(") || expr.endsWith("acos(") || expr.endsWith("atan(") ||
            expr.endsWith("sinh(") || expr.endsWith("cosh(") || expr.endsWith("tanh(") ||
            expr.endsWith("asinh(") || expr.endsWith("acosh(") || expr.endsWith("atanh(") ||
            expr.endsWith("cbrt(") || expr.endsWith("abs(") -> expr.dropLast(5)
            expr.endsWith("sin(") || expr.endsWith("cos(") || expr.endsWith("tan(") ||
            expr.endsWith("log(") || expr.endsWith("ln(") || expr.endsWith("exp(") -> expr.dropLast(4)
            expr.endsWith("pi") || expr.endsWith("^2") || expr.endsWith("^3") -> expr.dropLast(2)
            else -> expr.dropLast(1)
        }
        setState { copy(expression = newExpr) }
    }

    private fun appendParenthesis() {
        val expr = _uiState.value.expression
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }

        if (openCount > closeCount) {
            append(")")
        } else {
            val lastChar = expr.lastOrNull()
            if (lastChar == null || lastChar in "+-*/^(") {
                append("(")
            } else {
                if (openCount > closeCount) {
                    append(")")
                } else {
                    append("*(")
                }
            }
        }
    }

    private fun toggleSign() {
        val expr = _uiState.value.expression
        if (expr.isEmpty()) {
            append("-")
            return
        }
        val idx = lastNumberStartIndex(expr)
        if (idx == expr.length) return
        val (start, end) = idx to expr.length
        val before = expr.substring(0, start)
        val target = expr.substring(start, end)

        if (start > 0 && expr[start - 1] == '(' && expr.last() == ')') {
            val prevChar = if (start > 1) expr[start - 2] else null
            if (prevChar == '-') {
                val outerBefore = expr.substring(0, start - 2)
                setState { copy(expression = outerBefore + target) }
                return
            }
        }

        val wrapped = "(-$target)"
        setState { copy(expression = before + wrapped) }
    }

    private fun applyReciprocal() {
        val expr = _uiState.value.expression
        if (expr.isEmpty()) return
        val idx = lastNumberStartIndex(expr)
        if (idx == expr.length) return
        val (start, end) = idx to expr.length
        val before = expr.substring(0, start)
        val target = expr.substring(start, end)
        val wrapped = "1/($target)"
        setState { copy(expression = before + wrapped) }
    }

    private fun applyFactorial() {
        val expr = _uiState.value.expression
        if (expr.isEmpty()) return
        val lastChar = expr.last()
        if (lastChar.isDigit() || lastChar == ')') {
            append("!")
        }
    }

    private fun lastNumberStartIndex(expr: String): Int {
        var i = expr.length - 1
        while (i >= 0 && (expr[i].isDigit() || expr[i] == '.' || expr[i] == '%')) {
            i--
        }
        return i + 1
    }

    private fun evaluateAndCommit() {
        val current = _uiState.value
        val expression = autoCloseParentheses(current.expression)
        try {
            val value = ExpressionEvaluator.evaluate(expression, current.isDegrees)
            val resultStr = value.formatAsDisplay()

            if (expression.isNotEmpty() && resultStr != "Error") {
                val displayExpr = current.displayExpression
                HistoryViewModel.getInstance().addHistory(
                    CalculationHistory(
                        type = if (isScientificExpression(expression))
                            CalculationType.SCIENTIFIC
                        else
                            CalculationType.BASIC,
                        expression = displayExpr,
                        result = resultStr
                    )
                )
            }

            val resultForExpression = if (value.isFinite() && value == value.toLong().toDouble()) {
                value.toLong().toString()
            } else {
                value.toString()
            }

            setState { copy(expression = resultForExpression, result = "") }
            isResultDisplayed = true
        } catch (_: Throwable) {
            // ignore on equals if invalid
        }
    }

    private fun isScientificExpression(expr: String): Boolean {
        val scientificFunctions = listOf(
            "sin", "cos", "tan", "asin", "acos", "atan",
            "sinh", "cosh", "tanh", "asinh", "acosh", "atanh",
            "sqrt", "cbrt", "log", "ln", "exp", "abs", "pi", "^"
        )
        return scientificFunctions.any { expr.contains(it) }
    }

    private fun autoCloseParentheses(expr: String): String {
        var result = expr
        val openCount = result.count { it == '(' }
        val closeCount = result.count { it == ')' }
        repeat(openCount - closeCount) {
            result += ")"
        }
        return result
    }

    private fun reeval() {
        evalJob?.cancel()
        val current = _uiState.value
        evalJob = viewModelScope.launch(Dispatchers.Default) {
            try {
                val value = ExpressionEvaluator.evaluate(current.expression, current.isDegrees)
                setState { copy(result = value.formatAsDisplay()) }
            } catch (_: Throwable) {
                setState { copy(result = "") }
            }
        }
    }

    private fun Double.formatAsDisplay(): String {
        if (this.isNaN() || this.isInfinite()) return "Error"
        val formatter = DecimalFormat("#,###.##########")
        return formatter.format(this)
    }

    enum class CalculatorKey(val display: String, val expression: String = display, val isOperator: Boolean = false) {
        AC("AC"),
        C("C"),
        Parentheses("()"),
        Percent("%", "%", true),
        Divide("÷", "/", true),
        Multiply("×", "*", true),
        Minus("−", "-", true),
        Plus("+", "+", true),
        Equals("="),
        Dot("."),
        Sign("±"),

        Digit0("0"), Digit1("1"), Digit2("2"), Digit3("3"), Digit4("4"),
        Digit5("5"), Digit6("6"), Digit7("7"), Digit8("8"), Digit9("9"),

        Pi("π", "pi"),
        Sqrt("√", "sqrt("),
        Pow("^", "^", true),
        Exp("eˣ", "exp("),
        Log("log", "log("),
        Ln("ln", "ln("),
        Sin("sin", "sin("),
        Cos("cos", "cos("),
        Tan("tan", "tan("),
        Asin("sin⁻¹", "asin("),
        Acos("cos⁻¹", "acos("),
        Atan("tan⁻¹", "atan("),
        Sinh("sinh", "sinh("),
        Cosh("cosh", "cosh("),
        Tanh("tanh", "tanh("),
        Asinh("sinh⁻¹", "asinh("),
        Acosh("cosh⁻¹", "acosh("),
        Atanh("tanh⁻¹", "atanh("),
        CubeRoot("³√", "cbrt("),
        Cube("x³", "^3"),
        TwoPowX("2ˣ", "2^"),
        Euler("e", "e"),
        Square("x²", "^2"),
        Reciprocal("1/x", "1/"),
        Abs("|x|", "abs("),
        Fact("x!"),
        LParen("("),
        RParen(")"),
        DegRadToggle("Rad"),
        ScientificToggle("⇆")
    }
}