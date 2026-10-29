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
import com.shubdeep.calculatorpro.domain.productivity.EmiCalculator
import com.shubdeep.calculatorpro.ui.theme.*

@Composable
fun EmiScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var principalInput by remember { mutableStateOf("1000000") } // ₹ 10 Lakhs
    var interestRateInput by remember { mutableStateOf("8.5") }
    var tenureInput by remember { mutableStateOf("20") }
    var isTenureInYears by remember { mutableStateOf(true) }

    val principal = principalInput.toDoubleOrNull() ?: 0.0
    val rate = interestRateInput.toDoubleOrNull() ?: 0.0
    val tenureVal = tenureInput.toIntOrNull() ?: 1
    val tenureMonths = if (isTenureInYears) tenureVal * 12 else tenureVal

    val emiResult = remember(principal, rate, tenureMonths) {
        EmiCalculator.calculate(principal, rate, tenureMonths)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Loan EMI Calculator",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
        )
        Text(
            text = "Equated Monthly Installment calculation",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Principal
                Text("Loan Amount (₹)", color = TextSecondaryDark, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = principalInput,
                    onValueChange = { principalInput = it.filter { c -> c.isDigit() } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SamsungOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Annual Interest Rate
                Text("Interest Rate (% per annum)", color = TextSecondaryDark, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = interestRateInput,
                    onValueChange = { interestRateInput = it.filter { c -> c.isDigit() || c == '.' } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SamsungOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tenure & Unit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Loan Tenure", color = TextSecondaryDark, fontSize = 12.sp)
                    Row {
                        FilterChip(
                            selected = isTenureInYears,
                            onClick = { isTenureInYears = true },
                            label = { Text("Years", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SamsungOrange,
                                selectedLabelColor = PureBlack
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = !isTenureInYears,
                            onClick = { isTenureInYears = false },
                            label = { Text("Months", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SamsungOrange,
                                selectedLabelColor = PureBlack
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = tenureInput,
                    onValueChange = { tenureInput = it.filter { c -> c.isDigit() } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SamsungOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // EMI Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Monthly EMI", color = TextSecondaryDark, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "₹ ${emiResult.monthlyEmi.toPlainString()}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = SamsungOrange
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = SurfaceHighlightDark)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Principal Amount:", color = TextSecondaryDark, fontSize = 14.sp)
                    Text("₹ ${emiResult.principal.toPlainString()}", color = TextPrimaryDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Interest Payable:", color = TextSecondaryDark, fontSize = 14.sp)
                    Text("₹ ${emiResult.totalInterest.toPlainString()}", color = SamsungOrangeLight, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Payment (Principal + Interest):", color = TextSecondaryDark, fontSize = 14.sp)
                    Text("₹ ${emiResult.totalPayment.toPlainString()}", color = TextPrimaryDark, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
