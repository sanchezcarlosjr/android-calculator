package com.sanchezcarlosjr.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.sqrt

open class Token(val type: Char, val value: Double = 0.0)

class Operand(value: Double) : Token('0', value)

abstract class Operator(type: Char) : Token(type) {
    open val PRECEDENCE = -1
    open val ARITY = 0
    protected var operands = arrayListOf<Token>()
    fun add(token: Token) {
        operands.add(token)
    }

    fun reset() {
        operands = arrayListOf()
    }

    abstract fun operate(): Double
}

class Plus : Operator('+') {
    override val PRECEDENCE = 0
    override val ARITY = 2
    override fun operate(): Double {
        return operands.sumOf { it.value }
    }
}

class Multiplication : Operator('*') {
    override val PRECEDENCE = 1
    override val ARITY = 2
    override fun operate(): Double {
        return operands.map { it.value }.reduce { acc, i -> acc * i }
    }
}

class Mod : Operator('%') {
    override val PRECEDENCE = 0
    override val ARITY = 2
    override fun operate(): Double {
        return operands[0].value.toInt().mod(operands[1].value.toInt()).toDouble()
    }
}

class Rest : Operator('-') {
    override val PRECEDENCE = 0
    override val ARITY = 2
    override fun operate(): Double {
        return operands[1].value - operands[0].value
    }
}

class Division : Operator('/') {
    override val PRECEDENCE = 1
    override val ARITY = 2
    override fun operate(): Double {
        if (operands[0].value == 0.0)
            throw Exception("0")
        return operands[1].value / operands[0].value
    }
}

class Sqrt : Operator('S') {
    override val PRECEDENCE = 1
    override val ARITY = 1
    override fun operate(): Double {
        return sqrt(operands[0].value)
    }
}

class CalculatorViewModel : ViewModel() {
    private val _result = MutableLiveData(0.0)
    private val _expression = MutableLiveData("")
    private val operators = mapOf<Char, Token>(
        '+' to Plus(),
        '-' to Rest(),
        '*' to Multiplication(),
        '/' to Division(),
        '%' to Mod(),
        'S' to Sqrt()
    )

    val result: LiveData<Double> = _result
    val expression: LiveData<String> = _expression

    fun stream(token: String) {
        try {
            val (expression, result) = when (token) {
                "=" -> Pair(_expression.value, calculate())
                "A" -> Pair("", 0.0)
                else -> Pair(_expression.value + token, _result.value)
            }
            _expression.value = expression
            _result.value = result
        } catch (e: Exception) {
            _expression.value = "NaN"
            _result.value = 0.0
        }
    }

    private fun calculate(): Double {
        val tokenStream = scan()
        val postfixExpression = parse(tokenStream)
        return evaluate(postfixExpression)
    }

    private fun evaluate(postfixExpression: MutableList<Token>): Double {
        val stack = ArrayDeque<Token>()
        for (token in postfixExpression) {
            when (token) {
                is Operand -> stack.addLast(token)
                is Operator -> {
                    for (i in 1..token.ARITY) {
                        token.add(stack.removeLast())
                    }
                    stack.addLast(Token('0', token.operate()))
                    token.reset()
                }
            }
        }
        return stack.first().value
    }

    private fun scan(): MutableList<Token> {
        val tokenStream = mutableListOf<Token>()
        var state = 0
        var carry = 10
        for (lexeme in _expression.value!!) {
            if (state == 0) {
                carry = 10
            }
            when {
                lexeme in operators -> {
                    operators[lexeme]?.let { tokenStream.add(it) }
                    state = 0
                }
                lexeme == '.' -> {
                    state = 2
                }
                lexeme.digitToIntOrNull() in 0..9 && state == 0 -> {
                    tokenStream.add(Operand(lexeme.digitToInt().toDouble()))
                    state = 1
                }
                lexeme.digitToIntOrNull() in 0..9 && state == 1 -> {
                    tokenStream[tokenStream.size - 1] = Operand(
                        tokenStream[tokenStream.size - 1].value + 10 * lexeme.digitToInt()
                            .toDouble()
                    )
                    state = 1
                }
                lexeme.digitToIntOrNull() in 0..9 && state == 2 -> {
                    tokenStream[tokenStream.size - 1] = Operand(
                        tokenStream[tokenStream.size - 1].value + lexeme.digitToInt()
                            .toDouble() / carry
                    )
                    carry *= 10
                    state = 2
                }
                else -> Token('N')
            }
        }
        return tokenStream
    }

    private fun parse(tokenStream: MutableList<Token>): MutableList<Token> {
        val stack = ArrayDeque<Token>()
        val postfixExpression = mutableListOf<Token>()
        for (token in tokenStream) {
            when {
                token is Operand -> postfixExpression.add(token)
                token is Operator && stack.isEmpty() -> stack.addLast(token)
                token is Operator && !stack.isEmpty() -> {
                    while (!stack.isEmpty() && (stack.last() as Operator).PRECEDENCE >= token.PRECEDENCE) {
                        postfixExpression.add(stack.removeLast())
                    }
                    stack.addLast(token)
                }
            }
        }
        while (!stack.isEmpty()) {
            postfixExpression.add(stack.removeLast())
        }
        return postfixExpression
    }

}
