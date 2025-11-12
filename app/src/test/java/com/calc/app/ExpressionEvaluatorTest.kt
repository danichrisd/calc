package com.calc.app

import com.calc.app.math.ExpressionEvaluator
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpressionEvaluatorTest {

	@Test
	fun test_basic_arithmetic() {
		assertEquals(7.0, ExpressionEvaluator.evaluate("3+4", true), 1e-9)
		assertEquals(3.0, ExpressionEvaluator.evaluate("10-7", true), 1e-9)
		assertEquals(24.0, ExpressionEvaluator.evaluate("3*8", true), 1e-9)
		assertEquals(2.5, ExpressionEvaluator.evaluate("5/2", true), 1e-9)
	}

	@Test
	fun test_parentheses_power() {
		assertEquals(9.0, ExpressionEvaluator.evaluate("(1+2)^2", true), 1e-9)
		assertEquals(16.0, ExpressionEvaluator.evaluate("2^4", true), 1e-9)
	}

	@Test
	fun test_functions() {
		assertEquals(1.0, ExpressionEvaluator.evaluate("sin(90)", true), 1e-6)
		assertEquals(0.0, ExpressionEvaluator.evaluate("cos(90)", true), 1e-6)
		assertEquals(2.0, ExpressionEvaluator.evaluate("log(100)", true), 1e-9)
		assertEquals(1.0, ExpressionEvaluator.evaluate("ln(exp(1))", true), 1e-9)
	}

	@Test
	fun test_factorial_percent() {
		assertEquals(120.0, ExpressionEvaluator.evaluate("5!", true), 1e-9)
		assertEquals(0.5, ExpressionEvaluator.evaluate("50%", true), 1e-9)
	}
}


