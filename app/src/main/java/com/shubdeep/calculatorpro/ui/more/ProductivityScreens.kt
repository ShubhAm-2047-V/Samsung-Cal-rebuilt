package com.shubdeep.calculatorpro.ui.more

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubdeep.calculatorpro.domain.productivity.*
import com.shubdeep.calculatorpro.ui.theme.*

@Composable
fun PercentageScreen(modifier: Modifier = Modifier) {
    var pInput1 by remember { mutableStateOf("25") }
    var totalInput1 by remember { mutableStateOf("800") }

    var partInput2 by remember { mutableStateOf("50") }
    var totalInput2 by remember { mutableStateOf("200") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Percentage Calculator", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
        Spacer(modifier = Modifier.height(16.dp))

        // Mode 1: What is X% of Y?
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("What is X% of Y?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = pInput1,
                        onValueChange = { pInput1 = it },
                        label = { Text("Percentage (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SamsungOrange)
                    )
                    OutlinedTextField(
                        value = totalInput1,
                        onValueChange = { totalInput1 = it },
                        label = { Text("Total Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SamsungOrange)
                    )
                }
                val res1 = PercentageCalculator.percentOf(pInput1.toDoubleOrNull() ?: 0.0, totalInput1.toDoubleOrNull() ?: 0.0)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Result: $res1", color = SamsungOrange, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mode 2: X is what percent of Y?
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("X is what % of Y?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = partInput2,
                        onValueChange = { partInput2 = it },
                        label = { Text("Part (X)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SamsungOrange)
                    )
                    OutlinedTextField(
                        value = totalInput2,
                        onValueChange = { totalInput2 = it },
                        label = { Text("Total (Y)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SamsungOrange)
                    )
                }
                val res2 = PercentageCalculator.isWhatPercentOf(partInput2.toDoubleOrNull() ?: 0.0, totalInput2.toDoubleOrNull() ?: 0.0)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Result: ${"%.2f".format(res2)}%", color = SamsungOrange, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TipSplitScreen(modifier: Modifier = Modifier) {
    var billInput by remember { mutableStateOf("1200") }
    var tipPercentInput by remember { mutableStateOf("10") }
    var peopleInput by remember { mutableStateOf("4") }

    val bill = billInput.toDoubleOrNull() ?: 0.0
    val tipP = tipPercentInput.toDoubleOrNull() ?: 0.0
    val count = peopleInput.toIntOrNull() ?: 1

    val result = remember(bill, tipP, count) {
        TipCalculator.calculate(bill, tipP, count)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Tip & Split Bill", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = billInput,
                    onValueChange = { billInput = it },
                    label = { Text("Bill Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SamsungOrange)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = tipPercentInput,
                    onValueChange = { tipPercentInput = it },
                    label = { Text("Tip Percentage (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SamsungOrange)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = peopleInput,
                    onValueChange = { peopleInput = it },
                    label = { Text("Number of People") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SamsungOrange)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Total Tip: ₹ ${result.tipAmount}", color = TextSecondaryDark, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Total Bill: ₹ ${result.totalBill}", color = TextPrimaryDark, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = SurfaceHighlightDark)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Per Person Share", color = TextSecondaryDark, fontSize = 13.sp)
                Text("₹ ${result.perPersonAmount}", color = SamsungOrange, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ConstantsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) ScientificConstantsCatalog.CONSTANTS else {
            ScientificConstantsCatalog.CONSTANTS.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.symbol.contains(query, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Scientific Constants", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search physical constants...", color = TextSecondaryDark) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SamsungOrange),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { c ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            clipboard?.setPrimaryClip(ClipData.newPlainText(c.name, c.value))
                            Toast.makeText(context, "Copied ${c.value} to clipboard", Toast.LENGTH_SHORT).show()
                        },
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "${c.name} (${c.symbol})", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 15.sp)
                            Text(text = "${c.value} ${c.unit}", color = SamsungOrange, fontSize = 13.sp)
                        }
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
