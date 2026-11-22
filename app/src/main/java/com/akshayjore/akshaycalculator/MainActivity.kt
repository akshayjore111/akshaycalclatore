package com.akshayjore.akshaycalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AkshayCalculatorTheme {
                Surface(Modifier.fillMaxSize()) {
                    CalculatorApp()
                }
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }
    var radians by remember { mutableStateOf(true) }
    val rows = listOf(
        listOf("RAD", "(", ")", "⌫"),
        listOf("sin", "cos", "tan", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("√", "0", ".", "=")
    )

    Column(Modifier.fillMaxSize().padding(16.dp), Arrangement.Bottom) {
        Card(
            Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1ECFF)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.fillMaxSize().padding(20.dp), Arrangement.Bottom, Alignment.End) {
                Text(if (radians) "AKSHAY CALCULATOR • RAD" else "AKSHAY CALCULATOR • DEG", fontSize = 14.sp)
                Spacer(Modifier.height(12.dp))
                Text(expression, fontSize = 28.sp, textAlign = TextAlign.End)
                Text(result, fontSize = 42.sp, textAlign = TextAlign.End)
            }
        }
        Spacer(Modifier.height(16.dp))
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                row.forEach { label ->
                    CalculatorButton(label, Modifier.weight(1f).aspectRatio(1f)) {
                        when (label) {
                            "⌫" -> if (expression.isNotEmpty()) expression = expression.dropLast(1)
                            "=" -> result = try { formatResult(evaluate(expression, radians)) } catch (_: Exception) { "Error" }
                            "RAD" -> radians = !radians
                            "√" -> expression += "sqrt("
                            "sin" -> expression += "sin("
                            "cos" -> expression += "cos("
                            "tan" -> expression += "tan("
                            "÷" -> expression += "/"
                            "×" -> expression += "*"
                            "−" -> expression += "-"
                            else -> expression += label
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun CalculatorButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    val action = label == "="
    val operator = label in listOf("÷", "×", "−", "+", "RAD")
    Button(
        onClick,
        modifier,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (action) Color(0xFF6750A4) else if (operator) Color(0xFFE6DEFF) else Color(0xFFF3F1F7),
            contentColor = if (action) Color.White else if (operator) Color(0xFF6750A4) else Color(0xFF2B2930)
        )
    ) {
        Text(label, fontSize = 19.sp)
    }
}

fun formatResult(value: Double): String = if (value.isFinite()) {
    if (value == round(value)) value.toLong().toString() else "%.10f".format(value).trimEnd('0').trimEnd('.')
} else "Error"

fun evaluate(source: String, radiansMode: Boolean): Double {
    val expression = source.replace(" ", "").replace("−", "-")
    val parser = object {
        var index = 0
        fun parse(): Double {
            var value = parseTerm()
            while (index < expression.length) {
                if (expression[index] == '+') { index++; value += parseTerm() }
                else if (expression[index] == '-') { index++; value -= parseTerm() }
                else break
            }
            return value
        }
        fun parseTerm(): Double {
            var value = parseFactor()
            while (index < expression.length) {
                if (expression[index] == '*') { index++; value *= parseFactor() }
                else if (expression[index] == '/') { index++; val d = parseFactor(); if (d == 0.0) throw ArithmeticException(); value /= d }
                else break
            }
            return value
        }
        fun parseFactor(): Double {
            if (index < expression.length && expression[index] == '+') index++
            if (index < expression.length && expression[index] == '-') { index++; return -parseFactor() }
            if (index >= expression.length) throw IllegalArgumentException()
            if (expression[index] == '(') {
                index++
                val value = parse()
                if (index >= expression.length || expression[index] != ')') throw IllegalArgumentException()
                index++
                return value
            }
            for (function in listOf("sin", "cos", "tan", "sqrt")) {
                if (expression.startsWith(function, index)) {
                    index += function.length
                    if (index >= expression.length || expression[index] != '(') throw IllegalArgumentException()
                    index++
                    val value = parse()
                    if (index >= expression.length || expression[index] != ')') throw IllegalArgumentException()
                    index++
                    return when (function) {
                        "sqrt" -> { if (value < 0) throw IllegalArgumentException(); sqrt(value) }
                        "sin" -> sin(if (radiansMode) value else Math.toRadians(value))
                        "cos" -> cos(if (radiansMode) value else Math.toRadians(value))
                        "tan" -> tan(if (radiansMode) value else Math.toRadians(value))
                        else -> value
                    }
                }
            }
            val start = index
            while (index < expression.length && (expression[index].isDigit() || expression[index] == '.')) index++
            if (start == index) throw IllegalArgumentException()
            return expression.substring(start, index).toDouble()
        }
    }
    return parser.parse().also { if (parser.index != expression.length) throw IllegalArgumentException() }
}

@Composable
fun AkshayCalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}
