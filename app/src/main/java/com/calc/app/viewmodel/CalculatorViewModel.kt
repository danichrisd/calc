package com.calc.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calc.app.math.ExpressionEvaluator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.min

class CalculatorViewModel : ViewModel() {

	data class UiState(
		val expression: String = "",
		val result: String = "",
		val isDegrees: Boolean = true
	) {
		val displayExpression: String
			get() = expression
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
				.replace("atanh(", "atanh⁻¹(")
				.replace("ln(", "ln(")
				.replace("log(", "log(")
				.replace("exp(", "eˣ(")
				.replace("abs(", "|(")
				.replace("pi", "π")
				.replace("^2", "²")
				.replace("^3", "³")
	}

	private val _uiState = MutableStateFlow(UiState())
	val uiState = _uiState.asStateFlow()

	private var evalJob: Job? = null

	fun onKey(key: CalculatorKey) {
		when (key) {
			CalculatorKey.AC -> setState { copy(expression = "", result = "") }
			CalculatorKey.C -> backspace()
			CalculatorKey.Equals -> evaluateAndCommit()
			CalculatorKey.Sign -> toggleSign()
			CalculatorKey.Parentheses -> appendParenthesis()
			CalculatorKey.Percent,
			CalculatorKey.Divide,
			CalculatorKey.Multiply,
			CalculatorKey.Minus,
			CalculatorKey.Plus,
			CalculatorKey.Dot,
			CalculatorKey.Fact,
			CalculatorKey.Pow,
			CalculatorKey.LParen,
			CalculatorKey.RParen -> append(key.display)

			CalculatorKey.Pi -> append(key.expression)
			CalculatorKey.Sqrt -> append(key.expression)
			CalculatorKey.Sin -> append(key.expression)
			CalculatorKey.Cos -> append(key.expression)
			CalculatorKey.Tan -> append(key.expression)
			CalculatorKey.Asin -> append(key.expression)
			CalculatorKey.Acos -> append(key.expression)
			CalculatorKey.Atan -> append(key.expression)
			CalculatorKey.Ln -> append(key.expression)
			CalculatorKey.Log -> append(key.expression)
			CalculatorKey.Exp -> append(key.expression)
			CalculatorKey.Sinh -> append(key.expression)
			CalculatorKey.Cosh -> append(key.expression)
			CalculatorKey.Tanh -> append(key.expression)
			CalculatorKey.Asinh -> append(key.expression)
			CalculatorKey.Acosh -> append(key.expression)
			CalculatorKey.Atanh -> append(key.expression)
			CalculatorKey.CubeRoot -> append(key.expression)
			CalculatorKey.Cube -> append(key.expression)
			CalculatorKey.TwoPowX -> append(key.expression)
			CalculatorKey.Euler -> append(key.expression)
			CalculatorKey.Square -> append(key.expression)
			CalculatorKey.Reciprocal -> append(key.expression)
			CalculatorKey.Abs -> append(key.expression)

			CalculatorKey.DegRadToggle -> setState { copy(isDegrees = !isDegrees) }.also { reeval() }
			CalculatorKey.ScientificToggle -> { /* handled in UI */ }

			else -> append(key.display)
		}
		reeval()
	}

	private fun setState(transform: UiState.() -> UiState) {
		_uiState.value = _uiState.value.transform()
	}

	private fun append(text: String) {
		val current = _uiState.value.expression
		val newExpression = when (text) {
			"0" -> {
				val lastNumIndex = lastNumberStartIndex(current)
				val lastNum = current.substring(lastNumIndex)
				if (lastNum == "0") current else current + text
			}
			in "1".."9" -> {
				// If last character is '0' and before that is not a digit/operator, replace the '0'
				if (current.isNotEmpty() && current.last() == '0' &&
					(current.length == 1 || !current[current.length - 2].isDigit())) {
					current.dropLast(1) + text
				} else {
					current + text
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
			// Ada kurung buka yang belum tertutup, tambahkan kurung tutup
			append(")")
		} else {
			// Tidak ada kurung buka yang belum tertutup, atau jumlah sama
			// Cek apakah kita sedang di tengah angka atau setelah operator
			val lastChar = expr.lastOrNull()
			if (lastChar == null || lastChar in "+-×÷^(") {
				// Awal ekspresi atau setelah operator, tambahkan kurung buka
				append("(")
			} else {
				// Setelah angka atau fungsi, tambahkan kurung tutup jika ada kurung buka yang belum tertutup
				if (openCount > closeCount) {
					append(")")
				} else {
					// Kalikan dengan kurung buka untuk operasi matematika
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
		// Wrap the last number with negation: (-(...))
		val idx = lastNumberStartIndex(expr)
		if (idx == expr.length) return
		val (start, end) = idx to expr.length
		val before = expr.substring(0, start)
		val target = expr.substring(start, end)
		val wrapped = "(-$target)"
		setState { copy(expression = before + wrapped) }
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
			setState { copy(expression = value.formatAsDisplay(), result = "") }
		} catch (_: Throwable) {
			// ignore on equals if invalid
		}
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
		val text = "%.10f".format(this).trimEnd('0').trimEnd('.')
		return text
	}

	enum class CalculatorKey(val display: String, val expression: String = display, val isOperator: Boolean = false) {
		AC("AC"),
		C("C"),
		Parentheses("()"),
		Percent("%", "%", true),
		Divide("÷", "÷", true),
		Multiply("×", "×", true),
		Minus("−", "−", true),
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
		Acos("cos⁻¹"),
		Atan("tan⁻¹"),
		Sinh("sinh"),
		Cosh("cosh"),
		Tanh("tanh"),
		Asinh("sinh⁻¹"),
		Acosh("cosh⁻¹"),
		Atanh("tanh⁻¹"),
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


