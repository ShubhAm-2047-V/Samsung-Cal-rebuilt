package com.shubdeep.calculatorpro.ui.customizer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubdeep.calculatorpro.ui.theme.LocalSamsungColors

enum class CustomizerTab {
    ACADEMIC,
    COMMERCIAL,
    AI_CONSULTANT
}

data class CustomOption(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val price: Int,
    val desc: String? = null,
    val badgeColor: Color = Color(0xFF10B981)
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "Now"
)

fun formatINR(amount: Int): String {
    val s = amount.toString()
    if (s.length <= 3) return "₹$s"
    val lastThree = s.substring(s.length - 3)
    val otherNumbers = s.substring(0, s.length - 3)
    val formatted = otherNumbers.replace(Regex("\\B(?=(\\d{2})+(?!\\d))"), ",")
    return "₹$formatted,$lastThree"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShubDeepCustomizerScreen(
    onNavigateBack: () -> Unit
) {
    val palette = LocalSamsungColors.current
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(CustomizerTab.ACADEMIC) }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ShubDeep Lab",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = palette.textPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF10B981).copy(alpha = 0.16f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VAULT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = palette.textPrimary
                        )
                    }
                },
                actions = {
                    // Lock button to quickly re-lock the vault
                    FilledTonalButton(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = palette.surfaceElevated,
                            contentColor = palette.keyClearText
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Lock", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = palette.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Segmented Tab Selector (Academic, Commercial, AI Consultant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(palette.surfaceElevated)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TabPill(
                    label = "Academic",
                    icon = Icons.Default.School,
                    selected = selectedTab == CustomizerTab.ACADEMIC,
                    onClick = { selectedTab = CustomizerTab.ACADEMIC },
                    modifier = Modifier.weight(1f)
                )
                TabPill(
                    label = "Commercial",
                    icon = Icons.Default.Business,
                    selected = selectedTab == CustomizerTab.COMMERCIAL,
                    onClick = { selectedTab = CustomizerTab.COMMERCIAL },
                    modifier = Modifier.weight(1f)
                )
                TabPill(
                    label = "AI Assistant",
                    icon = Icons.Default.AutoAwesome,
                    selected = selectedTab == CustomizerTab.AI_CONSULTANT,
                    onClick = { selectedTab = CustomizerTab.AI_CONSULTANT },
                    modifier = Modifier.weight(1f)
                )
            }

            // Tab Content
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(tween(220)) togetherWith fadeOut(tween(180))
                },
                label = "tabContent"
            ) { tab ->
                when (tab) {
                    CustomizerTab.ACADEMIC -> AcademicEstimatorView(context)
                    CustomizerTab.COMMERCIAL -> CommercialEstimatorView(context)
                    CustomizerTab.AI_CONSULTANT -> AiConsultantView()
                }
            }
        }
    }
}

@Composable
private fun TabPill(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSamsungColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) palette.teal else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else palette.textSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color.White else palette.textSecondary
            )
        }
    }
}

// -------------------------------------------------------------------
// 1. ACADEMIC ESTIMATOR VIEW
// -------------------------------------------------------------------
@Composable
private fun AcademicEstimatorView(context: Context) {
    val palette = LocalSamsungColors.current
    var currentStep by remember { mutableIntStateOf(0) }

    var selectedCourse by remember { mutableStateOf("engineering") }
    var selectedPackage by remember { mutableStateOf("skilled") }
    var selectedTechs by remember { mutableStateOf(setOf("react", "nodejs", "sql")) }
    var selectedAddons by remember { mutableStateOf(setOf("ppt", "report")) }
    var selectedTimeline by remember { mutableStateOf("normal") }
    var showBreakdownDialog by remember { mutableStateOf(false) }

    val courses = remember {
        listOf(
            CustomOption("diploma", "Diploma", Icons.Default.Laptop, 0, "Polytechnic students", Color(0xFFEAB308)),
            CustomOption("engineering", "Engineering (B.E/B.Tech)", Icons.Default.Code, 3999, "Computer Science, IT & Electronics", Color(0xFF10B981)),
            CustomOption("mtech", "M.Tech / Research", Icons.Default.Memory, 7499, "Advanced algorithmic architectures", Color(0xFF00E5FF)),
            CustomOption("bca-mca", "BCA / MCA", Icons.Default.Storage, 2999, "Application systems", Color(0xFF8B5CF6)),
            CustomOption("ai-ml", "AI / Machine Learning", Icons.Default.Psychology, 5999, "Deep learning & computer vision", Color(0xFFEC4899)),
            CustomOption("android", "Android App Dev", Icons.Default.PhoneAndroid, 4999, "Native & hybrid mobile apps", Color(0xFF06B6D4)),
            CustomOption("phd", "PhD / Thesis", Icons.Default.School, 9999, "Comprehensive doctoral research", Color(0xFFF97316)),
            CustomOption("web-dev", "Full-Stack Web Dev", Icons.Default.Language, 3999, "Modern web platforms", Color(0xFF6366F1)),
            CustomOption("blockchain", "Blockchain / Web3", Icons.Default.CurrencyBitcoin, 6999, "Smart contracts & DApps", Color(0xFF14B8A6))
        )
    }

    val packages = remember {
        listOf(
            CustomOption("starter", "Starter Package", Icons.Default.StarOutline, 1999, "Core functional MVP & setup"),
            CustomOption("skilled", "Skilled Package", Icons.Default.StarHalf, 3499, "Intermediate features + documentation"),
            CustomOption("professional", "Professional Package", Icons.Default.Star, 4599, "Complete full-stack + viva guide"),
            CustomOption("custom", "Fully Custom Build", Icons.Default.Build, 5999, "Tailored ground-up solution")
        )
    }

    val techStacks = remember {
        listOf(
            CustomOption("react", "React.js Frontend", Icons.Default.Dashboard, 2499),
            CustomOption("nextjs", "Next.js Full-Stack", Icons.Default.Bolt, 2999),
            CustomOption("nodejs", "Node.js REST API", Icons.Default.Dns, 2499),
            CustomOption("python", "Python + Flask/FastAPI", Icons.Default.Code, 1999),
            CustomOption("sql", "PostgreSQL / MySQL", Icons.Default.Storage, 1499),
            CustomOption("mongodb", "MongoDB NoSQL", Icons.Default.DataUsage, 1499),
            CustomOption("flutter", "Flutter Mobile App", Icons.Default.Layers, 5499),
            CustomOption("kotlin", "Kotlin Android Native", Icons.Default.PhoneAndroid, 5499),
            CustomOption("ai_model", "PyTorch / TensorFlow Model", Icons.Default.Psychology, 6499),
            CustomOption("docker", "Docker Containerization", Icons.Default.DirectionsBoat, 2999),
            CustomOption("firebase", "Firebase Cloud Backend", Icons.Default.LocalFireDepartment, 1499)
        )
    }

    val addons = remember {
        listOf(
            CustomOption("ppt", "PPT Presentation Slides", Icons.Default.Slideshow, 699, "Professional 15-20 slide deck"),
            CustomOption("report", "Comprehensive Thesis Report", Icons.Default.Description, 1299, "Full IEEE/College formatted report"),
            CustomOption("viva", "Viva Voce Preparation Kit", Icons.Default.MenuBook, 499, "Top 50 Q&A with examiner tips"),
            CustomOption("setup", "Remote Setup & Execution", Icons.Default.VerifiedUser, 999, "TeamViewer / AnyDesk live demo"),
            CustomOption("success", "Complete Project Success Pack", Icons.Default.WorkspacePremium, 1999, "All 4 add-ons bundled together (Save ₹1,497!)")
        )
    }

    val timelines = remember {
        listOf(
            CustomOption("urgent", "Express (1–3 Days)", Icons.Default.Bolt, 1999, "Top priority rush delivery"),
            CustomOption("normal", "Standard (4–7 Days)", Icons.Default.Schedule, 999, "Balanced development timeline"),
            CustomOption("relaxed", "Relaxed (8–14 Days)", Icons.Default.CheckCircle, 0, "Standard pacing (No surcharge)"),
            CustomOption("flexible", "Flexible Timeline", Icons.Default.AllInclusive, 0, "Flexible delivery date")
        )
    }

    // Dynamic Total Calculation
    val coursePrice = courses.find { it.id == selectedCourse }?.price ?: 0
    val packagePrice = if (selectedCourse == "diploma") {
        packages.find { it.id == selectedPackage }?.price ?: 0
    } else 0
    val techPrice = selectedTechs.sumOf { id -> techStacks.find { it.id == id }?.price ?: 0 }
    val addonPrice = selectedAddons.sumOf { id -> addons.find { it.id == id }?.price ?: 0 }
    val timelinePrice = timelines.find { it.id == selectedTimeline }?.price ?: 0

    val totalPrice = coursePrice + packagePrice + techPrice + addonPrice + timelinePrice

    val stepTitles = listOf("Course", "Package", "Tech Stack", "Add-Ons", "Timeline")

    Column(modifier = Modifier.fillMaxSize()) {
        // Step Indicator Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            stepTitles.forEachIndexed { index, title ->
                val active = index == currentStep
                val done = index < currentStep
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    active -> palette.teal
                                    done -> palette.teal.copy(alpha = 0.35f)
                                    else -> palette.surfaceElevated
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (done) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) Color.White else palette.textSecondary
                            )
                        }
                    }
                    if (index < stepTitles.size - 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .width(16.dp)
                                .height(2.dp)
                                .background(if (done) palette.teal else palette.divider)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }

        // Active Step Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                item {
                    Text(
                        text = when (currentStep) {
                            0 -> "Step 1: Pick Your Field / Course"
                            1 -> "Step 2: Choose Project Scope"
                            2 -> "Step 3: Select Tech Stack"
                            3 -> "Step 4: Academic Add-Ons"
                            else -> "Step 5: Desired Delivery Timeline"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.textPrimary
                    )
                }

                when (currentStep) {
                    0 -> items(courses) { course ->
                        OptionCard(
                            option = course,
                            selected = selectedCourse == course.id,
                            onClick = { selectedCourse = course.id }
                        )
                    }
                    1 -> items(packages) { pkg ->
                        OptionCard(
                            option = pkg,
                            selected = selectedPackage == pkg.id,
                            onClick = { selectedPackage = pkg.id }
                        )
                    }
                    2 -> items(techStacks) { tech ->
                        OptionCard(
                            option = tech,
                            selected = selectedTechs.contains(tech.id),
                            onClick = {
                                selectedTechs = if (selectedTechs.contains(tech.id)) {
                                    selectedTechs - tech.id
                                } else {
                                    selectedTechs + tech.id
                                }
                            }
                        )
                    }
                    3 -> items(addons) { addon ->
                        OptionCard(
                            option = addon,
                            selected = selectedAddons.contains(addon.id),
                            onClick = {
                                selectedAddons = if (selectedAddons.contains(addon.id)) {
                                    selectedAddons - addon.id
                                } else {
                                    selectedAddons + addon.id
                                }
                            }
                        )
                    }
                    4 -> items(timelines) { timeline ->
                        OptionCard(
                            option = timeline,
                            selected = selectedTimeline == timeline.id,
                            onClick = { selectedTimeline = timeline.id }
                        )
                    }
                }
            }
        }

        // Bottom Sticky Total & Action Bar
        Surface(
            color = palette.surfaceElevated,
            shadowElevation = 16.dp,
            border = BorderStroke(1.dp, palette.divider),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.clickable { showBreakdownDialog = true }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ESTIMATED TOTAL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.textSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Breakdown",
                            tint = palette.teal,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Text(
                        text = formatINR(totalPrice),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = palette.teal
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, palette.divider)
                        ) {
                            Text(text = "Back", color = palette.textPrimary)
                        }
                    }

                    if (currentStep < 4) {
                        Button(
                            onClick = { currentStep++ },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = palette.teal)
                        ) {
                            Text(text = "Next", color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = {
                                val message = "Hi ShubDeep Lab! I configured my project on Calculator Pro Vault:\n" +
                                        "• Course: $selectedCourse\n" +
                                        "• Tech: ${selectedTechs.joinToString(", ")}\n" +
                                        "• Add-ons: ${selectedAddons.joinToString(", ")}\n" +
                                        "• Timeline: $selectedTimeline\n" +
                                        "• Total: ${formatINR(totalPrice)}"
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://wa.me/?text=${Uri.encode(message)}")
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "WhatsApp Quote", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (showBreakdownDialog) {
        AlertDialog(
            onDismissRequest = { showBreakdownDialog = false },
            title = {
                Text(
                    text = "Project Estimate Breakdown",
                    fontWeight = FontWeight.Bold,
                    color = palette.textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BreakdownRow(label = "Field: $selectedCourse", amount = coursePrice)
                    if (packagePrice > 0) {
                        BreakdownRow(label = "Package: $selectedPackage", amount = packagePrice)
                    }
                    BreakdownRow(label = "Tech Stack (${selectedTechs.size} items)", amount = techPrice)
                    BreakdownRow(label = "Add-Ons (${selectedAddons.size} items)", amount = addonPrice)
                    BreakdownRow(label = "Timeline: $selectedTimeline", amount = timelinePrice)
                    HorizontalDivider(color = palette.divider)
                    BreakdownRow(label = "Total Investment", amount = totalPrice, isBold = true)
                }
            },
            confirmButton = {
                TextButton(onClick = { showBreakdownDialog = false }) {
                    Text(text = "Got It", color = palette.teal)
                }
            },
            containerColor = palette.surfaceElevated
        )
    }
}

// -------------------------------------------------------------------
// 2. COMMERCIAL ESTIMATOR VIEW
// -------------------------------------------------------------------
@Composable
private fun CommercialEstimatorView(context: Context) {
    val palette = LocalSamsungColors.current

    var projectType by remember { mutableStateOf("app") } // "app" or "web"
    var platform by remember { mutableStateOf("both") }
    var selectedFeatures by remember { mutableStateOf(setOf("auth", "payment", "push", "analytics")) }
    var timeline by remember { mutableStateOf("standard") }

    val features = remember {
        listOf(
            CustomOption("auth", "User Auth & Roles", Icons.Default.Lock, 2999, "Email/password, JWT, session manager"),
            CustomOption("social", "Social Login (Google/Apple)", Icons.Default.Group, 4999, "One-tap OAuth integration"),
            CustomOption("otp", "SMS / WhatsApp OTP", Icons.Default.Pin, 3999, "2-Factor Authentication security"),
            CustomOption("payment", "Payment Gateway (Razorpay/Stripe)", Icons.Default.Payment, 7999, "Cards, UPI, NetBanking, recurring"),
            CustomOption("push", "Push Notifications", Icons.Default.Notifications, 3999, "Targeted Firebase FCM campaigns"),
            CustomOption("chat", "In-App Live Chat", Icons.Default.Chat, 9999, "Real-time socket messaging"),
            CustomOption("maps", "Live Maps & GPS Tracking", Icons.Default.Map, 7999, "Google Maps SDK & route distance"),
            CustomOption("offline", "Offline Database Sync", Icons.Default.WifiOff, 9999, "SQLite / Room local caching"),
            CustomOption("analytics", "Analytics & Event Tracking", Icons.Default.Analytics, 2999, "Mixpanel, GA4 & user metrics"),
            CustomOption("ai", "OpenAI / Claude AI Integration", Icons.Default.AutoAwesome, 14999, "Generative AI custom workflows")
        )
    }

    val basePrice = if (projectType == "app") 19999 else 14999
    val platformPrice = when (platform) {
        "android" -> 4999
        "ios" -> 7999
        else -> 12999
    }
    val featurePrice = selectedFeatures.sumOf { id -> features.find { it.id == id }?.price ?: 0 }
    val commercialTotal = basePrice + platformPrice + featurePrice

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text(
                text = "Commercial App & Software Estimator",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = palette.textPrimary
            )
            Text(
                text = "Enterprise-grade pricing for clients, businesses & startups",
                fontSize = 13.sp,
                color = palette.textSecondary
            )
        }

        item {
            Text(text = "Project Scope", fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
                CommercialPill(
                    title = "Mobile Application",
                    subtitle = "Android & iOS native",
                    selected = projectType == "app",
                    onClick = { projectType = "app" },
                    modifier = Modifier.weight(1f)
                )
                CommercialPill(
                    title = "Web SaaS / Portal",
                    subtitle = "Responsive cloud app",
                    selected = projectType == "web",
                    onClick = { projectType = "web" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(text = "Target Platforms", fontWeight = FontWeight.SemiBold, color = palette.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                PlatformChip(label = "Android Only", selected = platform == "android", onClick = { platform = "android" })
                PlatformChip(label = "iOS Only", selected = platform == "ios", onClick = { platform = "ios" })
                PlatformChip(label = "Cross-Platform (Both)", selected = platform == "both", onClick = { platform = "both" })
            }
        }

        item {
            Text(
                text = "Select Enterprise Modules (${selectedFeatures.size} selected)",
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary
            )
        }

        items(features) { feat ->
            OptionCard(
                option = feat,
                selected = selectedFeatures.contains(feat.id),
                onClick = {
                    selectedFeatures = if (selectedFeatures.contains(feat.id)) {
                        selectedFeatures - feat.id
                    } else {
                        selectedFeatures + feat.id
                    }
                }
            )
        }

        item {
            // Commercial Summary Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = palette.surfaceElevated),
                border = BorderStroke(1.2.dp, palette.teal.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "COMMERCIAL ESTIMATE SUMMARY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.teal,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatINR(commercialTotal),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = palette.textPrimary
                    )
                    Text(
                        text = "Includes full source code, deployment, API documentation & 30-day warranty.",
                        fontSize = 12.sp,
                        color = palette.textSecondary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Button(
                        onClick = {
                            val msg = "Hi ShubDeep Lab! I request a commercial quote:\n" +
                                    "• Type: $projectType\n" +
                                    "• Platform: $platform\n" +
                                    "• Modules: ${selectedFeatures.joinToString(", ")}\n" +
                                    "• Estimated Budget: ${formatINR(commercialTotal)}"
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://wa.me/?text=${Uri.encode(msg)}")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Connect on WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------
// 3. AI CONSULTANT VIEW
// -------------------------------------------------------------------
@Composable
private fun AiConsultantView() {
    val palette = LocalSamsungColors.current
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Hello! I am your ShubDeep Lab AI Consultant. Ask me for recommended tech stacks, college project ideas, or custom software cost estimates!",
                isUser = false
            )
        )
    }

    val quickQuestions = listOf(
        "Suggest top project ideas for AI/ML final year",
        "How much does a Flutter + Firebase app cost?",
        "Difference between Starter vs Skilled package?"
    )

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        // Chat messages list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }
        }

        // Quick suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickQuestions.forEach { question ->
                SuggestionChip(
                    onClick = {
                        messages.add(ChatMessage(text = question, isUser = true))
                        val reply = getAiResponse(question)
                        messages.add(ChatMessage(text = reply, isUser = false))
                    },
                    label = { Text(text = question, fontSize = 11.sp) }
                )
            }
        }

        // Input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask anything about your project...", fontSize = 13.sp) },
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = palette.teal,
                    unfocusedBorderColor = palette.divider
                ),
                maxLines = 2
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val query = inputText.trim()
                        messages.add(ChatMessage(text = query, isUser = true))
                        inputText = ""
                        val reply = getAiResponse(query)
                        messages.add(ChatMessage(text = reply, isUser = false))
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(palette.teal)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val palette = LocalSamsungColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser) palette.teal else palette.surfaceElevated,
            border = if (message.isUser) null else BorderStroke(1.dp, palette.divider),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = if (message.isUser) Color.White else palette.textPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private fun getAiResponse(query: String): String {
    val q = query.lowercase()
    return when {
        q.contains("ai") || q.contains("ml") || q.contains("machine learning") ->
            "For AI/ML final year projects, top industry picks are:\n1. Real-time Disease Diagnosis using ResNet/YOLO\n2. AI Financial Fraud Detection with Anomaly Transformers\n3. Multimodal Voice + Vision Assistant using PyTorch & FastAPI.\nEstimated cost: ₹5,999 - ₹8,999 with dataset preparation & model training included."

        q.contains("flutter") || q.contains("firebase") || q.contains("app") ->
            "A Flutter + Firebase cross-platform mobile app with authentication, Cloud Firestore, push notifications, and payment gateway starts at ₹9,999 (Academic) or ₹24,999 (Commercial Production)."

        q.contains("starter") || q.contains("skilled") || q.contains("package") ->
            "The Starter package (₹1,999) covers working prototype and source code. The Skilled package (₹3,499) adds full documentation, viva kit, and database integration. The Professional package (₹4,599) includes live remote demo setup!"

        else ->
            "Thanks for reaching out! In ShubDeep Lab, we provide end-to-end engineering projects, full source code, live demos, and complete thesis documentation. Feel free to use the WhatsApp button to connect directly with our engineering team!"
    }
}

// -------------------------------------------------------------------
// HELPER UI COMPONENTS
// -------------------------------------------------------------------
@Composable
private fun OptionCard(
    option: CustomOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    val palette = LocalSamsungColors.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) palette.surfaceElevated else palette.background,
        border = BorderStroke(
            width = if (selected) 1.8.dp else 1.dp,
            color = if (selected) palette.teal else palette.divider
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (selected) palette.teal.copy(alpha = 0.18f) else palette.surfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = if (selected) palette.teal else palette.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary
                )
                if (option.desc != null) {
                    Text(
                        text = option.desc,
                        fontSize = 11.sp,
                        color = palette.textSecondary,
                        maxLines = 1
                    )
                }
            }

            if (option.price > 0) {
                Text(
                    text = "+${formatINR(option.price)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) palette.teal else palette.textSecondary
                )
            } else {
                Text(
                    text = "Included",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF10B981)
                )
            }
        }
    }
}

@Composable
private fun CommercialPill(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalSamsungColors.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) palette.teal.copy(alpha = 0.12f) else palette.surfaceElevated,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) palette.teal else palette.divider
        ),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) palette.teal else palette.textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = palette.textSecondary
            )
        }
    }
}

@Composable
private fun PlatformChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val palette = LocalSamsungColors.current
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label, fontSize = 12.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = palette.teal,
            selectedLabelColor = Color.White
        )
    )
}

@Composable
private fun BreakdownRow(label: String, amount: Int, isBold: Boolean = false) {
    val palette = LocalSamsungColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = if (isBold) 14.sp else 13.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = palette.textPrimary
        )
        Text(
            text = formatINR(amount),
            fontSize = if (isBold) 15.sp else 13.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isBold) palette.teal else palette.textPrimary
        )
    }
}
