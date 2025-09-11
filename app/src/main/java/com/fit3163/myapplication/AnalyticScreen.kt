package com.fit3163.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.fit3163.myapplication.ui.theme.Black00
import com.fit3163.myapplication.ui.theme.Purple40
import com.fit3163.myapplication.ui.theme.ThemeMode
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

data class ChartEntry(val value: Float, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    currentTheme: ThemeMode,
    onThemeChanged: (ThemeMode) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val name = FirebaseAuth.getInstance().currentUser?.displayName
    var timeframe by remember { mutableStateOf("By Week") }
    var currentDate by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Analytics",
                    style = MaterialTheme.typography.headlineMedium
                )

                TimeframeSelector(
                    selectedOption = timeframe,
                    onOptionSelected = { timeframe = it }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Date navigation
            DateNavigation(
                timeframe = timeframe,
                currentDate = currentDate,
                onDateChanged = { currentDate = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bar Chart Section
            Text(
                text = "Expenses Over Time",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            BarChart(
                entries = listOf(
                    ChartEntry(50f, "Week 1"),
                    ChartEntry(80f, "Week 2"),
                    ChartEntry(30f, "Week 3"),
                    ChartEntry(100f, "Week 4")
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Pie Chart Section - Modified to use available space
            Text(
                text = "By Categories",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Use weight to allow the pie chart to expand
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                PieChart(
                    entries = listOf(
                        ChartEntry(200f, "Food & Drinks"),
                        ChartEntry(190f, "Transport"),
                        ChartEntry(100f, "Entertainment")
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun TimeframeSelector(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("By Week", "By Month", "By Year")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(selectedOption)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DateNavigation(
    timeframe: String,
    currentDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit
) {
    val dateFormatter = when (timeframe) {
        "By Week" -> {
            val firstDayOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(WeekFields.of(Locale.getDefault()).firstDayOfWeek))
            val lastDayOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(WeekFields.of(Locale.getDefault()).firstDayOfWeek)).minusDays(1)
            DateTimeFormatter.ofPattern("MMM d").format(firstDayOfWeek) + " - " +
                    DateTimeFormatter.ofPattern("MMM d, yyyy").format(lastDayOfWeek)
        }
        "By Month" -> DateTimeFormatter.ofPattern("MMMM yyyy").format(currentDate)
        else -> DateTimeFormatter.ofPattern("yyyy").format(currentDate) // By Year
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                val newDate = when (timeframe) {
                    "By Week" -> currentDate.minusWeeks(1)
                    "By Month" -> currentDate.minusMonths(1)
                    else -> currentDate.minusYears(1)
                }
                onDateChanged(newDate)
            }
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
        }

        Text(
            text = dateFormatter,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(
            onClick = {
                val newDate = when (timeframe) {
                    "By Week" -> currentDate.plusWeeks(1)
                    "By Month" -> currentDate.plusMonths(1)
                    else -> currentDate.plusYears(1)
                }
                onDateChanged(newDate)
            }
        ) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next")
        }
    }
}

@Composable
fun BarChart(
    entries: List<ChartEntry>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (entries.isEmpty()) return@Canvas

            val maxValue = entries.maxOf { it.value }
            val barWidth = size.width / (entries.size * 2 - 1).toFloat()
            val spacing = barWidth

            entries.forEachIndexed { index, entry ->
                val barHeight = (entry.value / maxValue) * (size.height - 40.dp.toPx())
                val left = index * (barWidth + spacing)
                val top = size.height - barHeight - 20.dp.toPx()

                drawRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight)
                )

                // Draw label
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        entry.label,
                        left + barWidth / 2,
                        size.height - 5.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 12.sp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )

                    // Draw value
                    drawText(
                        "$${entry.value.toInt()}",
                        left + barWidth / 2,
                        top - 5.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 12.sp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }

            // Draw horizontal axis
            drawLine(
                color = Color.Black,
                start = Offset(0f, size.height - 20.dp.toPx()),
                end = Offset(size.width, size.height - 20.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
fun PieChart(
    entries: List<ChartEntry>,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFF03DAC5), // Teal
        Color(0xFFBB86FC), // Purple light
        Color(0xFF6200EE), // Purple dark
        Color(0xFF018786), // Teal dark
        Color(0xFF3700B3), // Purple darker
        Color(0xFF03DAC6)  // Teal light
    )
) {
    Box(
        modifier = modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (entries.isEmpty()) return@Canvas

            val total = entries.sumOf { it.value.toDouble() }.toFloat()
            var startAngle = 0f
            val radius = min(size.width, size.height) / 2 * 0.8f
            val center = Offset(size.width / 2, size.height / 2)

            entries.forEachIndexed { index, entry ->
                val sweepAngle = (entry.value / total) * 360f
                var color = colors[index % colors.size]

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(radius * 2, radius * 2),
                    topLeft = Offset(center.x - radius, center.y - radius)
                )

                // Draw label outside the pie chart
                val angle = startAngle + sweepAngle / 2
                val labelRadius = radius + 20.dp.toPx()
                val x = center.x + labelRadius * cos(Math.toRadians(angle.toDouble())).toFloat()
                val y = center.y + labelRadius * sin(Math.toRadians(angle.toDouble())).toFloat()

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "${entry.label}: $${entry.value.toInt()}",
                        x,
                        y,
                        android.graphics.Paint().apply {
                            color = Black00
                            textSize = 12.sp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }

                startAngle += sweepAngle
            }
        }
    }
}
