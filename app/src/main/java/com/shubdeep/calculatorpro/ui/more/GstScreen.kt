package com.shubdeep.calculatorpro.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubdeep.calculatorpro.domain.productivity.GstCalculationMode
import com.shubdeep.calculatorpro.domain.productivity.GstCalculator
import com.shubdeep.calculatorpro.domain.productivity.TransactionType
import com.shubdeep.calculatorpro.ui.theme.*
import java.math.BigDecimal

@Composable
fun GstScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var amountInput by remember { mutableStateOf("10000") }
    var selectedSlab by remember { mutableDoubleStateOf(18.0) }
    var customRateInput by remember { mutableStateOf("") }
    var calculationMode by remember { mutableStateOf(GstCalculationMode.ADD_GST) }
    var transactionType by remember { mutableStateOf(TransactionType.INTRA_STATE) }

    val effectiveRate = if (selectedSlab == -1.0) {
        customRateInput.toDoubleOrNull() ?: 0.0
    } else {
        selectedSlab
    }

    val amountBd = remember(amountInput) {
        amountInput.toBigDecimalOrNull() ?: BigDecimal.ZERO
    }

    val result = remember(amountBd, effectiveRate, calculationMode, transactionType) {
        GstCalculator.calculate(amountBd, effectiveRate, calculationMode, transactionType)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Indian GST Calculator",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
        )
        Text(
            text = "Goods and Services Tax calculations for India",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Calculation Mode Selector (Add GST vs Extract GST)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = calculationMode == GstCalculationMode.ADD_GST,
                onClick = { calculationMode = GstCalculationMode.ADD_GST },
                label = { Text("Add GST (Exclusive)", fontWeight = FontWeight.SemiBold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SamsungOrange,
                    selectedLabelColor = PureBlack,
                    containerColor = SurfaceDark,
                    labelColor = TextPrimaryDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = calculationMode == GstCalculationMode.EXTRACT_GST,
                onClick = { calculationMode = GstCalculationMode.EXTRACT_GST },
                label = { Text("Extract GST (Inclusive)", fontWeight = FontWeight.SemiBold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SamsungOrange,
                    selectedLabelColor = PureBlack,
                    containerColor = SurfaceDark,
                    labelColor = TextPrimaryDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Amount Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (calculationMode == GstCalculationMode.ADD_GST) "Base Price (₹)" else "GST Inclusive Price (₹)",
                    color = TextSecondaryDark,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it.filter { c -> c.isDigit() || c == '.' } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = LocalTextStyle.current.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SamsungOrange,
                        unfocusedBorderColor = SurfaceHighlightDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // GST Rate Slabs
        Text("GST Rate Slabs", color = TextSecondaryDark, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            GstCalculator.COMMON_GST_SLABS.forEach { slab ->
                val isSelected = selectedSlab == slab
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedSlab = slab },
                    label = { Text("${slab.toInt()}%", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SamsungOrange,
                        selectedLabelColor = PureBlack,
                        containerColor = SurfaceDark,
                        labelColor = TextPrimaryDark
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = null,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Transaction Type: Intra-State vs Inter-State
        Text("Transaction Jurisdiction", color = TextSecondaryDark, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = transactionType == TransactionType.INTRA_STATE,
                onClick = { transactionType = TransactionType.INTRA_STATE },
                label = { Text("Intra-State (CGST + SGST)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SamsungOrange,
                    selectedLabelColor = PureBlack,
                    containerColor = SurfaceDark,
                    labelColor = TextPrimaryDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = transactionType == TransactionType.INTER_STATE,
                onClick = { transactionType = TransactionType.INTER_STATE },
                label = { Text("Inter-State (IGST)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SamsungOrange,
                    selectedLabelColor = PureBlack,
                    containerColor = SurfaceDark,
                    labelColor = TextPrimaryDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // GST Breakdown Results Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Calculation Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                Spacer(modifier = Modifier.height(12.dp))

                GstResultRow("Net / Base Amount:", "₹ ${result.basePrice.toPlainString()}")
                GstResultRow("Total GST Amount (${effectiveRate}%):", "₹ ${result.gstAmount.toPlainString()}", isHighlight = true)

                if (transactionType == TransactionType.INTRA_STATE) {
                    val halfRate = effectiveRate / 2.0
                    GstResultRow("  ├─ CGST (${halfRate}%):", "₹ ${result.cgstAmount.toPlainString()}")
                    GstResultRow("  └─ SGST (${halfRate}%):", "₹ ${result.sgstAmount.toPlainString()}")
                } else {
                    GstResultRow("  └─ IGST (${effectiveRate}%):", "₹ ${result.igstAmount.toPlainString()}")
                }

                HorizontalDivider(color = SurfaceHighlightDark, modifier = Modifier.padding(vertical = 10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Final Price:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    Text("₹ ${result.totalPrice.toPlainString()}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SamsungOrange)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Disclaimer: Applicable GST rate depends on the product classification (HSN/SAC) and relevant government tax rules. This calculator provides numerical estimations.",
            color = TextTertiaryDark,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun GstResultRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = if (isHighlight) SamsungOrangeLight else TextSecondaryDark, fontSize = 14.sp)
        Text(text = value, color = if (isHighlight) SamsungOrange else TextPrimaryDark, fontSize = 15.sp, fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium)
    }
}
