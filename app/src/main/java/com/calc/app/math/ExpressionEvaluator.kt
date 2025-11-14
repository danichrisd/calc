package com.calc.app.math

import kotlin.math.*
import java.util.ArrayDeque

object ExpressionEvaluator {
    // Public API
    fun evaluate(expr: String, degrees: Boolean): Double {
        if (expr.isBlank()) return 0.0
        val tokens = tokenize(expr)
        val rpn = shuntingYard(tokens)
        return evalRpn(rpn, degrees)
    }

    // Tokenization
    private fun tokenize(expr: String): List<Token> {
        val s = expr.replace(" ", "")
        val out = mutableListOf<Token>()
        var i = 0
        while (i < s.length) {
            val c = s[i]
            when {
                c.isDigit() || c == '.' -> {
                    var j = i + 1
                    while (j < s.length && (s[j].isDigit() || s[j] == '.')) j++
                    val numStr = s.substring(i, j)
                    if (numStr.count { it == '.' } > 1) {
                        throw IllegalArgumentException("Invalid number format: $numStr")
                    }
                    out += Token.NumberToken(numStr.toDouble())
                    i = j
                    continue
                }
                c == 'p' && s.substring(i).startsWith("pi") -> {
                    out += Token.NumberToken(Math.PI)
                    i += 2
                    continue
                }
                c == 'e' && (i + 1 >= s.length || !s[i+1].isLetter()) -> {
                    // Handle 'e' as Euler's number if not followed by letters
                    out += Token.NumberToken(Math.E)
                    i++
                    continue
                }
                c.isLetter() -> {
                    var j = i + 1
                    while (j < s.length && s[j].isLetter()) j++
                    val name = s.substring(i, j)
                    out += when (name.lowercase()) {
                        "sin","cos","tan","asin","acos","atan","log","ln","sqrt","exp",
                        "cbrt","abs","sinh","cosh","tanh","asinh","acosh","atanh" -> Token.FunctionToken(name.lowercase())
                        else -> throw IllegalArgumentException("Unknown name: $name")
                    }
                    i = j
                    continue
                }
                c == '(' -> out += Token.LParen
                c == ')' -> out += Token.RParen
                c == '+' -> out += Token.OperatorToken("+", precedence = 1, rightAssoc = false, arity = 2)
                c == '−' || c == '-' -> out += Token.OperatorToken("-", precedence = 1, rightAssoc = false, arity = 2)
                c == '×' || c == '*' -> out += Token.OperatorToken("*", precedence = 2, rightAssoc = false, arity = 2)
                c == '÷' || c == '/' -> out += Token.OperatorToken("/", precedence = 2, rightAssoc = false, arity = 2)
                c == '^' -> out += Token.OperatorToken("^", precedence = 3, rightAssoc = true, arity = 2)
                c == '!' -> out += Token.PostfixToken("!")
                c == '%' -> out += Token.PostfixToken("%")
                else -> throw IllegalArgumentException("Unexpected char: $c")
            }
            i++
        }
        // Convert unary leading minus to unary operator
        return handleUnaryOperators(out)
    }

    private fun handleUnaryOperators(tokens: List<Token>): List<Token> {
        val out = mutableListOf<Token>()
        var prev: Token? = null
        for (t in tokens) {
            if (t is Token.OperatorToken && t.op == "-") {
                if (prev == null || prev is Token.OperatorToken || prev is Token.LParen) {
                    // This is a unary minus
                    val unaryMinus = Token.OperatorToken("neg", precedence = 4, rightAssoc = true, arity = 1)
                    out += unaryMinus
                    prev = unaryMinus
                    continue
                }
            }
            out += t
            prev = t
        }
        return out
    }

    // Shunting-yard
    private fun shuntingYard(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val stack = ArrayDeque<Token>()
        for (t in tokens) {
            when (t) {
                is Token.NumberToken -> output += t
                is Token.FunctionToken -> stack.push(t)
                is Token.PostfixToken -> output += t
                is Token.OperatorToken -> {
                    while (stack.isNotEmpty()) {
                        val top = stack.peek()
                        if (top is Token.OperatorToken &&
                            ((!t.rightAssoc && t.precedence <= top.precedence) ||
                                (t.rightAssoc && t.precedence < top.precedence))
                        ) {
                            output += stack.pop()
                        } else break
                    }
                    stack.push(t)
                }
                is Token.LParen -> stack.push(t)
                is Token.RParen -> {
                    while (stack.isNotEmpty() && stack.peek() !is Token.LParen) {
                        output += stack.pop()
                    }
                    if (stack.isEmpty() || stack.peek() !is Token.LParen) {
                        throw IllegalArgumentException("Mismatched parentheses")
                    }
                    stack.pop()
                    if (stack.isNotEmpty() && stack.peek() is Token.FunctionToken) {
                        output += stack.pop()
                    }
                }
            }
        }
        while (stack.isNotEmpty()) {
            val t = stack.pop()
            if (t is Token.LParen || t is Token.RParen) throw IllegalArgumentException("Mismatched parentheses")
            output += t
        }
        return output
    }

    // RPN evaluation
    private fun evalRpn(tokens: List<Token>, degrees: Boolean): Double {
        val stack = ArrayDeque<Double>()
        for (t in tokens) {
            when (t) {
                is Token.NumberToken -> stack.push(t.value)
                is Token.OperatorToken -> {
                    if (t.arity == 1) {
                        require(stack.isNotEmpty()) { "Missing operand for unary operator" }
                        val a = stack.pop()
                        val res = when (t.op) {
                            "neg" -> -a
                            else -> error("Unknown unary operator")
                        }
                        stack.push(res)
                    } else {
                        require(stack.size >= 2) { "Missing operand for binary operator" }
                        val b = stack.pop()
                        val a = stack.pop()
                        val res = when (t.op) {
                            "+" -> a + b
                            "-" -> a - b
                            "*" -> a * b
                            "/" -> a / b
                            "^" -> a.pow(b)
                            else -> error("Unknown binary operator")
                        }
                        stack.push(res)
                    }
                }
                is Token.PostfixToken -> {
                    require(stack.isNotEmpty()) { "Missing operand" }
                    val a = stack.pop()
                    val res = when (t.op) {
                        "%" -> a / 100.0
                        "!" -> factorial(a)
                        else -> error("postfix")
                    }
                    stack.push(res)
                }
                is Token.FunctionToken -> {
                    require(stack.isNotEmpty()) { "Missing operand for function" }
                    val res = when (t.name) {
                        "sqrt" -> sqrt(stack.pop())
                        "cbrt" -> cbrt(stack.pop())
                        "abs" -> abs(stack.pop())
                        "ln" -> ln(stack.pop())
                        "log" -> log10(stack.pop())
                        "exp" -> exp(stack.pop())
                        "sin" -> trig(::sin, stack.pop(), degrees)
                        "cos" -> trig(::cos, stack.pop(), degrees)
                        "tan" -> trig(::tan, stack.pop(), degrees)
                        "asin" -> invTrig(::asin, stack.pop(), degrees)
                        "acos" -> invTrig(::acos, stack.pop(), degrees)
                        "atan" -> invTrig(::atan, stack.pop(), degrees)
                        "sinh" -> sinh(stack.pop())
                        "cosh" -> cosh(stack.pop())
                        "tanh" -> tanh(stack.pop())
                        "asinh" -> asinh(stack.pop())
                        "acosh" -> acosh(stack.pop())
                        "atanh" -> atanh(stack.pop())
                        else -> throw IllegalArgumentException("Unknown function ${t.name}")
                    }
                    stack.push(res)
                }
                is Token.LParen, is Token.RParen -> error("Paren in RPN")
            }
        }
        require(stack.size == 1) { "Invalid expression" }
        return stack.pop()
    }

    private fun trig(fn: (Double) -> Double, x: Double, degrees: Boolean): Double {
        val rad = if (degrees) Math.toRadians(x) else x
        return fn(rad)
    }

    private fun invTrig(fn: (Double) -> Double, x: Double, degrees: Boolean): Double {
        val v = fn(x)
        return if (degrees) Math.toDegrees(v) else v
    }

    private fun factorial(x: Double): Double {
        if (x < 0) return Double.NaN
        val n = x.roundToInt()
        if (abs(x - n) > 1e-9) return Double.NaN
        var r = 1.0
        for (i in 2..n) r *= i
        return r
    }

    // Tokens
    private sealed interface Token {
        data class NumberToken(val value: Double) : Token
        data class OperatorToken(val op: String, val precedence: Int, val rightAssoc: Boolean, val arity: Int) : Token
        data class FunctionToken(val name: String) : Token
        data class PostfixToken(val op: String) : Token
        data object LParen : Token
        data object RParen : Token
    }
}
