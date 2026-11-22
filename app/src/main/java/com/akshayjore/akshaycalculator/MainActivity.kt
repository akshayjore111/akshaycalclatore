package com.akshayjore.akshaycalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize(), color = Color(0xFFF8F7FC)) {
                    AkshayContractorApp()
                }
            }
        }
    }
}

@Composable
fun AkshayContractorApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Basic", "Units", "GST", "Site")

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                val active = selectedTab == index
                Button(
                    onClick = { selectedTab = index },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (active) Color(0xFF6750A4) else Color(0xFFEAE5F8),
                        contentColor = if (active) Color.White else Color(0xFF2B2930)
                    )
                ) {
                    Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        when (selectedTab) {
            0 -> BasicCalculatorTab()
            1 -> CivilUnitConverterTab()
            2 -> GstCalculatorTab()
            3 -> SiteEstimatorTab()
        }
    }
}

@Composable
fun BasicCalculatorTab() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }
    var lastHistory by remember { mutableStateOf("") }

    val rows = listOf(
        listOf("AC", "⌫", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("00", "0", ".", "=")
    )

    Column(Modifier.fillMaxSize().padding(14.dp), Arrangement.Bottom) {
        Card(
            Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1ECFF)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(Modifier.fillMaxSize().padding(20.dp), Arrangement.Bottom, Alignment.End) {
                Text("AKSHAY CALCULATOR", fontSize = 13.sp, color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
                if (lastHistory.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(lastHistory, fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.End)
                }
                Spacer(Modifier.height(10.dp))
                Text(expression.ifEmpty { "0" }, fontSize = 30.sp, textAlign = TextAlign.End)
                Spacer(Modifier.height(6.dp))
                Text(result, fontSize = 44.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
            }
        }
        Spacer(Modifier.height(14.dp))
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                row.forEach { label ->
                    val isAction = label == "="
                    val isClear = label == "AC"
                    val isOp = label in listOf("÷", "×", "−", "+", "%", "⌫")
                    Button(
                        onClick = {
                            when (label) {
                                "AC" -> { expression = ""; result = "0" }
                                "⌫" -> if (expression.isNotEmpty()) expression = expression.dropLast(1)
                                "=" -> {
                                    val evaluated = try { formatNum(evalBasic(expression)) } catch (_: Exception) { "Error" }
                                    if (evaluated != "Error" && expression.isNotEmpty()) {
                                        lastHistory = "$expression = $evaluated"
                                    }
                                    result = evaluated
                                }
                                "÷" -> expression += "/"
                                "×" -> expression += "*"
                                "−" -> expression += "-"
                                "%" -> expression += "/100"
                                else -> expression += label
                            }
                        },
                        modifier = Modifier.weight(1f).aspectRatio(1.1f),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isAction -> Color(0xFF6750A4)
                                isClear -> Color(0xFFFFDAD6)
                                isOp -> Color(0xFFE6DEFF)
                                else -> Color.White
                            },
                            contentColor = when {
                                isAction -> Color.White
                                isClear -> Color(0xFF93000A)
                                isOp -> Color(0xFF6750A4)
                                else -> Color(0xFF2B2930)
                            }
                        )
                    ) {
                        Text(label, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun CivilUnitConverterTab() {
    var category by remember { mutableIntStateOf(0) }
    var inputVal by remember { mutableStateOf("") }
    val num = inputVal.toDoubleOrNull() ?: 0.0
    val categories = listOf("Length", "Area", "Volume")

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Civil Unit Converter", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2B2930))
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEachIndexed { idx, name ->
                Button(
                    onClick = { category = idx },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (category == idx) Color(0xFF6750A4) else Color(0xFFEAE5F8),
                        contentColor = if (category == idx) Color.White else Color(0xFF2B2930)
                    )
                ) { Text(name, fontSize = 14.sp) }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = inputVal,
            onValueChange = { inputVal = it },
            label = { Text("Value daalein") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(14.dp))

        when (category) {
            0 -> {
                ResultCard("Feet -> Meter", "${formatNum(num * 0.3048)} m")
                ResultCard("Meter -> Feet", "${formatNum(num * 3.28084)} ft")
                ResultCard("Inch -> mm", "${formatNum(num * 25.4)} mm")
                ResultCard("mm -> Inch", "${formatNum(num / 25.4)} in")
                ResultCard("Feet -> Inch", "${formatNum(num * 12.0)} in")
            }
            1 -> {
                ResultCard("Sq.ft -> Sq.m", "${formatNum(num * 0.092903)} sq.m")
                ResultCard("Sq.m -> Sq.ft", "${formatNum(num * 10.7639)} sq.ft")
                ResultCard("Sq.ft -> Brass (100 sq.ft)", "${formatNum(num / 100.0)} Brass")
                ResultCard("Sq.m -> Sq.yd (Gaj)", "${formatNum(num * 1.19599)} Gaj")
            }
            2 -> {
                ResultCard("Cu.ft -> Brass (100 cu.ft)", "${formatNum(num / 100.0)} Brass")
                ResultCard("Brass -> Cu.ft", "${formatNum(num * 100.0)} cu.ft")
                ResultCard("Cu.ft -> Cu.m", "${formatNum(num * 0.0283168)} cu.m")
                ResultCard("Cu.m -> Cu.ft", "${formatNum(num * 35.3147)} cu.ft")
                ResultCard("Cu.m -> Brass", "${formatNum(num * 0.353147)} Brass")
            }
        }
    }
}

@Composable
fun GstCalculatorTab() {
    var amountText by remember { mutableStateOf("") }
    var gstRate by remember { mutableDoubleStateOf(18.0) }
    var isAddGst by remember { mutableStateOf(true) }
    val rates = listOf(5.0, 12.0, 18.0, 28.0)

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val baseAmount = if (isAddGst) amount else amount / (1.0 + gstRate / 100.0)
    val gstAmount = if (isAddGst) amount * gstRate / 100.0 else amount - baseAmount
    val totalAmount = if (isAddGst) amount + gstAmount else amount

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("GST Calculator", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Amount (Rs)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { isAddGst = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAddGst) Color(0xFF6750A4) else Color(0xFFEAE5F8),
                    contentColor = if (isAddGst) Color.White else Color(0xFF2B2930)
                )
            ) { Text("+ Add GST") }
            Button(
                onClick = { isAddGst = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isAddGst) Color(0xFF6750A4) else Color(0xFFEAE5F8),
                    contentColor = if (!isAddGst) Color.White else Color(0xFF2B2930)
                )
            ) { Text("- Remove GST") }
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            rates.forEach { rate ->
                Button(
                    onClick = { gstRate = rate },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (gstRate == rate) Color(0xFF6750A4) else Color(0xFFEAE5F8),
                        contentColor = if (gstRate == rate) Color.White else Color(0xFF2B2930)
                    )
                ) { Text("${rate.toInt()}%") }
            }
        }
        Spacer(Modifier.height(16.dp))
        ResultCard("Base Amount", "Rs ${formatNum(baseAmount)}")
        ResultCard("CGST (${formatNum(gstRate / 2)}%)", "Rs ${formatNum(gstAmount / 2)}")
        ResultCard("SGST (${formatNum(gstRate / 2)}%)", "Rs ${formatNum(gstAmount / 2)}")
        ResultCard("Total GST", "Rs ${formatNum(gstAmount)}")
        ResultCard("Final Bill Amount", "Rs ${formatNum(totalAmount)}", highlight = true)
    }
}

@Composable
fun SiteEstimatorTab() {
    var mode by remember { mutableIntStateOf(0) }
    var lengthText by remember { mutableStateOf("") }
    var widthText by remember { mutableStateOf("") }
    var rateText by remember { mutableStateOf("") }

    val l = lengthText.toDoubleOrNull() ?: 0.0
    val w = widthText.toDoubleOrNull() ?: 0.0
    val r = rateText.toDoubleOrNull() ?: 0.0
    val areaSqFt = l * w
    val areaSqM = areaSqFt * 0.092903

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Civil Site Estimator", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { mode = 0 },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mode == 0) Color(0xFF6750A4) else Color(0xFFEAE5F8),
                    contentColor = if (mode == 0) Color.White else Color(0xFF2B2930)
                )
            ) { Text("Paver Block") }
            Button(
                onClick = { mode = 1 },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mode == 1) Color(0xFF6750A4) else Color(0xFFEAE5F8),
                    contentColor = if (mode == 1) Color.White else Color(0xFF2B2930)
                )
            ) { Text("Painting") }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = lengthText,
            onValueChange = { lengthText = it },
            label = { Text("Length (Feet)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = widthText,
            onValueChange = { widthText = it },
            label = { Text(if (mode == 0) "Width (Feet)" else "Height (Feet)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = rateText,
            onValueChange = { rateText = it },
            label = { Text("Rate per Sq.ft (Optional, Rs)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(14.dp))

        ResultCard("Total Area", "${formatNum(areaSqFt)} sq.ft  (${formatNum(areaSqM)} sq.m)")
        if (mode == 0) {
            val blocks = ceil(areaSqFt * 3.5 * 1.05).toLong()
            ResultCard("Approx Paver Blocks (incl. 5% waste)", "$blocks pcs")
            ResultCard("Bedding Sand (2 inch / 50mm)", "${formatNum(areaSqFt * 0.167)} cu.ft")
        } else {
            val paintLitres = areaSqFt / 110.0
            val primerLitres = areaSqFt / 130.0
            ResultCard("Paint Required (2 Coats)", "${formatNum(paintLitres)} Litres")
            ResultCard("Primer Required (1 Coat)", "${formatNum(primerLitres)} Litres")
        }
        if (r > 0) {
            val cost = areaSqFt * r
            ResultCard("Estimated Cost", "Rs ${formatNum(cost)}", highlight = true)
            ResultCard("With 18% GST", "Rs ${formatNum(cost * 1.18)}")
        }
    }
}

@Composable
fun ResultCard(label: String, value: String, highlight: Boolean = false) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) Color(0xFF6750A4) else Color.White
        )
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 14.sp, color = if (highlight) Color.White else Color(0xFF555555))
            Text(
                value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (highlight) Color.White else Color(0xFF2B2930)
            )
        }
    }
}

fun formatNum(value: Double): String = if (value.isFinite()) {
    if (value == round(value)) value.toLong().toString() else "%.2f".format(value).trimEnd('0').trimEnd('.')
} else "Error"

fun evalBasic(source: String): Double {
    val expr = source.replace(" ", "").replace("−", "-")
    val parser = object {
        var idx = 0
        fun parse(): Double {
            var v = term()
            while (idx < expr.length) {
                if (expr[idx] == '+') { idx++; v += term() }
                else if (expr[idx] == '-') { idx++; v -= term() }
                else break
            }
            return v
        }
        fun term(): Double {
            var v = factor()
            while (idx < expr.length) {
                if (expr[idx] == '*') { idx++; v *= factor() }
                else if (expr[idx] == '/') { idx++; val d = factor(); if (d == 0.0) throw ArithmeticException(); v /= d }
                else break
            }
            return v
        }
        fun factor(): Double {
            if (idx < expr.length && expr[idx] == '+') idx++
            if (idx < expr.length && expr[idx] == '-') { idx++; return -factor() }
            if (idx >= expr.length) throw IllegalArgumentException()
            val start = idx
            while (idx < expr.length && (expr[idx].isDigit() || expr[idx] == '.')) idx++
            if (start == idx) throw IllegalArgumentException()
            return expr.substring(start, idx).toDouble()
        }
    }
    return parser.parse().also { if (parser.idx != expr.length) throw IllegalArgumentException() }
}
