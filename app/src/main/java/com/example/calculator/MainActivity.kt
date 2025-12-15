package com.example.calculator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Stack

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var previousExpressionTextView: TextView
    private var isNewOperation = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.result)
        previousExpressionTextView = findViewById(R.id.previous_expression)
        setupButtons()
    }

    private fun setupButtons() {
        val numberButtons = mapOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8",
            R.id.btn9 to "9", R.id.btn_dot to "."
        )

        numberButtons.forEach { (id, value) ->
            findViewById<Button>(id).setOnClickListener { onNumberClick(value) }
        }

        val opButtons = mapOf(
            R.id.btn_plus to "+", R.id.btn_sub to "-",
            R.id.btnX to "×", R.id.btn_div to "÷",
            R.id.btn_per to "%"
        )

        opButtons.forEach { (id, op) ->
            findViewById<Button>(id).setOnClickListener { onOperatorClick(op) }
        }

        findViewById<Button>(R.id.btn_res).setOnClickListener { onEqualClick() }
        findViewById<Button>(R.id.btn_C).setOnClickListener { onClearClick() }
        findViewById<ImageButton>(R.id.btn_del).setOnClickListener { onBackspaceClick() }
        findViewById<Button>(R.id.btn_paren).setOnClickListener { onBracketClick() }

        // Geometric feature added
        findViewById<Button>(R.id.btn_geometric).setOnClickListener {
            val intent = Intent(this, GeometricActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onNumberClick(value: String) {
        if (isNewOperation) {
            textView.text = ""
            isNewOperation = false
            textView.setTextColor(resources.getColor(R.color.white, null))
        }
        val currentText = textView.text.toString()
        if (currentText == "0" && value != ".") {
            textView.text = value
        } else {
            textView.append(value)
        }
    }

    private fun onOperatorClick(op: String) {
        isNewOperation = false
        val text = textView.text.toString()
        textView.setTextColor(resources.getColor(R.color.white, null))
        
        if (text.isEmpty()) {
            if (op == "-") textView.append(op)
            return
        }
        
        val lastChar = text.last().toString()
        
        if (lastChar == "%") {
            if (op == "×") {
                // Replace % with ×
                textView.text = text.dropLast(1) + op
            } else {
                // Append other operators
                textView.append(op)
            }
        } else if (lastChar in listOf("+", "-", "×", "÷")) {
             // Replace previous operator
             textView.text = text.dropLast(1) + op
        } else {
             textView.append(op)
        }
    }

    private fun onBracketClick() {
        isNewOperation = false
        textView.setTextColor(resources.getColor(R.color.white, null))
        val text = textView.text.toString()
        val openCount = text.count { it == '(' }
        val closeCount = text.count { it == ')' }

        if (text.isEmpty() || text.last().toString() in listOf("+", "-", "×", "÷", "(")) {
            textView.append("(")
        } else if (openCount > closeCount && text.last().toString() !in listOf("(", ".")) {
            textView.append(")")
        } else {
            textView.append("×(")
        }
    }

    private fun onClearClick() {
        textView.text = "0"
        previousExpressionTextView.text = ""
        textView.setTextColor(resources.getColor(R.color.white, null))
        isNewOperation = true
    }

    private fun onBackspaceClick() {
        val text = textView.text.toString()
        if (text == "0") return
        
        if (text.isNotEmpty()) {
            textView.text = text.dropLast(1)
            if (textView.text.isEmpty()) {
                textView.text = "0"
                isNewOperation = true
            }
        }
    }

    private fun onEqualClick() {
        try {
            val expressionRaw = textView.text.toString()
            val expression = expressionRaw
                .replace(Regex("%(?=[0-9.(])"), "/100*")
                .replace("%", "/100")
                .replace("×", "*")
                .replace("÷", "/")
            
            val result = evaluate(expression)
            
            // Format result
            val resultLong = result.toLong()
            val resultString = if (result == resultLong.toDouble()) {
                resultLong.toString()
            } else {
                result.toString()
            }
            
            previousExpressionTextView.text = expressionRaw
            textView.text = resultString
            textView.setTextColor(resources.getColor(R.color.result_color, null))
            isNewOperation = true
        } catch (e: Exception) {
            textView.text = "Error"
            textView.setTextColor(resources.getColor(R.color.orange, null)) // Error color
            isNewOperation = true
        }
    }

    // --- Evaluation Logic ---

    private fun evaluate(expression: String): Double {
        val tokens = tokenize(expression)
        val rpn = shuntingYard(tokens)
        return evaluateRPN(rpn)
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expression.length) {
            val c = expression[i]
            if (c.isDigit() || c == '.') {
                var j = i
                while (j < expression.length && (expression[j].isDigit() || expression[j] == '.')) {
                    j++
                }
                tokens.add(expression.substring(i, j))
                i = j
            } else if (c in listOf('+', '-', '*', '/', '(', ')')) {
                if (c == '-' && (tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/", "("))) {
                     tokens.add("u-")
                } else {
                    tokens.add(c.toString())
                }
                i++
            } else {
                i++ // skip spaces
            }
        }
        return tokens
    }

    private fun shuntingYard(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = Stack<String>()
        
        val precedence = mapOf(
            "+" to 1, "-" to 1,
            "*" to 2, "/" to 2,
            "u-" to 3
        )

        for (token in tokens) {
            if (token.first().isDigit() || (token.length > 1 && token.first() == '.')) {
                output.add(token)
            } else if (token == "(") {
                stack.push(token)
            } else if (token == ")") {
                while (stack.isNotEmpty() && stack.peek() != "(") {
                    output.add(stack.pop())
                }
                if (stack.isNotEmpty()) stack.pop() // Pop '('
            } else {
                while (stack.isNotEmpty() && stack.peek() != "(" &&
                    (precedence[stack.peek()] ?: 0) >= (precedence[token] ?: 0)) {
                    output.add(stack.pop())
                }
                stack.push(token)
            }
        }
        while (stack.isNotEmpty()) {
            output.add(stack.pop())
        }
        return output
    }

    private fun evaluateRPN(tokens: List<String>): Double {
        val stack = Stack<Double>()
        for (token in tokens) {
            if (token.first().isDigit() || (token.length > 1 && token.first() == '.')) {
                stack.push(token.toDouble())
            } else if (token == "u-") {
                val a = stack.pop()
                stack.push(-a)
            } else {
                val b = stack.pop()
                val a = if (stack.isNotEmpty()) stack.pop() else 0.0 
                when (token) {
                    "+" -> stack.push(a + b)
                    "-" -> stack.push(a - b)
                    "*" -> stack.push(a * b)
                    "/" -> stack.push(a / b)
                }
            }
        }
        return if (stack.isNotEmpty()) stack.pop() else 0.0
    }
}
