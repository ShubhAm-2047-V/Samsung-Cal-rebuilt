package com.shubdeep.calculatorpro.ui.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubdeep.calculatorpro.ui.theme.*

@Composable
fun MoreScreen(
    onNavigateBack: () -> Unit,
    onNavigateToGst: () -> Unit,
    onNavigateToEmi: () -> Unit,
    onNavigateToPercentage: () -> Unit,
    onNavigateToTip: () -> Unit,
    onNavigateToConstants: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSamsungColors.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = palette.textPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "Productivity Tools",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary
                )
                Text(
                    text = "Specialized calculators & preferences",

                    fontSize = 12.sp,
                    color = TextSecondaryDark
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text("Finance & Business", color = SamsungOrangeLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        ToolItemCard(
            title = "Indian GST Calculator",
            subtitle = "Add / extract GST (0%, 5%, 12%, 18%, 28%) with CGST & SGST",
            icon = Icons.Default.ReceiptLong,
            onClick = onNavigateToGst
        )

        Spacer(modifier = Modifier.height(10.dp))

        ToolItemCard(
            title = "Loan EMI Calculator",
            subtitle = "Calculate monthly EMI, total interest, and loan amortization",
            icon = Icons.Default.AccountBalance,
            onClick = onNavigateToEmi
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text("Everyday Math", color = SamsungOrangeLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        ToolItemCard(
            title = "Percentage Calculator",
            subtitle = "X% of Y, percentage increase, decrease, and proportions",
            icon = Icons.Default.Percent,
            onClick = onNavigateToPercentage
        )

        Spacer(modifier = Modifier.height(10.dp))

        ToolItemCard(
            title = "Tip & Split Bill",
            subtitle = "Calculate dining tips and split expenses evenly per person",
            icon = Icons.Default.Group,
            onClick = onNavigateToTip
        )

        Spacer(modifier = Modifier.height(10.dp))

        ToolItemCard(
            title = "Scientific Constants",
            subtitle = "Physics and chemistry fundamental constants (c, h, G, N_A)",
            icon = Icons.Default.Biotech,
            onClick = onNavigateToConstants
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text("System & Application", color = SamsungOrangeLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        ToolItemCard(
            title = "Settings",
            subtitle = "Theme, number format, precision, cache & app info",
            icon = Icons.Default.Settings,
            onClick = onNavigateToSettings
        )
    }
}

@Composable
private fun ToolItemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceElevatedDark,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = SamsungOrange, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryDark)
                Text(text = subtitle, fontSize = 12.sp, color = TextSecondaryDark)
            }

            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
        }
    }
}
