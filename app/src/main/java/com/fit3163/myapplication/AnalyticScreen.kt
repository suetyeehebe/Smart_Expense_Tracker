//package com.fit3163.myapplication
//
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowForward
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.DrawScope
//import androidx.compose.ui.graphics.nativeCanvas
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import com.fit3163.myapplication.data.expenses.ExpensesViewModel
//import com.fit3163.myapplication.ui.theme.Black00
//import com.fit3163.myapplication.ui.theme.Purple40
//import com.fit3163.myapplication.ui.theme.ThemeMode
//import com.google.firebase.auth.FirebaseAuth
//import kotlin.math.cos
//import kotlin.math.min
//import kotlin.math.sin
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//import java.time.temporal.TemporalAdjusters
//import java.time.temporal.WeekFields
//import java.util.Locale
//
//data class ChartEntry(val value: Float, val label: String)
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AnalyticsScreen(
//    navController: NavHostController,
//    authViewModel: AuthViewModel,
//    currentTheme: ThemeMode,
//    onThemeChanged: (ThemeMode) -> Unit,
//    expensesViewModel: ExpensesViewModel
//) {
//    var showSheet by remember { mutableStateOf(false) }
//    val authState = authViewModel.authState.observeAsState()
//    val context = LocalContext.current
//    val name = FirebaseAuth.getInstance().currentUser?.displayName
//    var timeframe by remember { mutableStateOf("By Week") }
//    var currentDate by remember { mutableStateOf(LocalDate.now()) }
//
//    LaunchedEffect(authState.value) {
//        when (authState.value) {
//            is AuthState.Unauthenticated -> navController.navigate("login")
//            else -> Unit
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 12.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Analytics",
//                    style = MaterialTheme.typography.headlineMedium
//                )
//
//                TimeframeSelector(
//                    selectedOption = timeframe,
//                    onOptionSelected = { timeframe = it }
//                )
//            }
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            // Date navigation
//            DateNavigation(
//                timeframe = timeframe,
//                currentDate = currentDate,
//                onDateChanged = { currentDate = it }
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Bar Chart Section
//            Text(
//                text = "Expenses Over Time",
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier.fillMaxWidth(),
//                textAlign = TextAlign.Center
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            BarChart(
//                entries = listOf(
//                    ChartEntry(50f, "Week 1"),
//                    ChartEntry(80f, "Week 2"),
//                    ChartEntry(30f, "Week 3"),
//                    ChartEntry(100f, "Week 4")
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp)
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Pie Chart Section - Modified to use available space
//            Text(
//                text = "By Categories",
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier.fillMaxWidth(),
//                textAlign = TextAlign.Center
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Use weight to allow the pie chart to expand
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//            ) {
//                PieChart(
//                    entries = listOf(
//                        ChartEntry(200f, "Food & Drinks"),
//                        ChartEntry(190f, "Transport"),
//                        ChartEntry(100f, "Entertainment")
//                    ),
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun TimeframeSelector(
//    selectedOption: String,
//    onOptionSelected: (String) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//    val options = listOf("By Week", "By Month", "By Year")
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.End,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Box {
//            OutlinedButton(
//                onClick = { expanded = true },
//                shape = RoundedCornerShape(8.dp)
//            ) {
//                Text(selectedOption)
//            }
//
//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false }
//            ) {
//                options.forEach { option ->
//                    DropdownMenuItem(
//                        text = { Text(option) },
//                        onClick = {
//                            onOptionSelected(option)
//                            expanded = false
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun DateNavigation(
//    timeframe: String,
//    currentDate: LocalDate,
//    onDateChanged: (LocalDate) -> Unit
//) {
//    val dateFormatter = when (timeframe) {
//        "By Week" -> {
//            val firstDayOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(WeekFields.of(Locale.getDefault()).firstDayOfWeek))
//            val lastDayOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(WeekFields.of(Locale.getDefault()).firstDayOfWeek)).minusDays(1)
//            DateTimeFormatter.ofPattern("MMM d").format(firstDayOfWeek) + " - " +
//                    DateTimeFormatter.ofPattern("MMM d, yyyy").format(lastDayOfWeek)
//        }
//        "By Month" -> DateTimeFormatter.ofPattern("MMMM yyyy").format(currentDate)
//        else -> DateTimeFormatter.ofPattern("yyyy").format(currentDate) // By Year
//    }
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        IconButton(
//            onClick = {
//                val newDate = when (timeframe) {
//                    "By Week" -> currentDate.minusWeeks(1)
//                    "By Month" -> currentDate.minusMonths(1)
//                    else -> currentDate.minusYears(1)
//                }
//                onDateChanged(newDate)
//            }
//        ) {
//            Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
//        }
//
//        Text(
//            text = dateFormatter,
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//
//        IconButton(
//            onClick = {
//                val newDate = when (timeframe) {
//                    "By Week" -> currentDate.plusWeeks(1)
//                    "By Month" -> currentDate.plusMonths(1)
//                    else -> currentDate.plusYears(1)
//                }
//                onDateChanged(newDate)
//            }
//        ) {
//            Icon(Icons.Default.ArrowForward, contentDescription = "Next")
//        }
//    }
//}
//
//@Composable
//fun BarChart(
//    entries: List<ChartEntry>,
//    modifier: Modifier = Modifier,
//    barColor: Color = MaterialTheme.colorScheme.primary
//) {
//    Box(
//        modifier = modifier
//            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
//            .padding(8.dp)
//    ) {
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            if (entries.isEmpty()) return@Canvas
//
//            val maxValue = entries.maxOf { it.value }
//            val barWidth = size.width / (entries.size * 2 - 1).toFloat()
//            val spacing = barWidth
//
//            entries.forEachIndexed { index, entry ->
//                val barHeight = (entry.value / maxValue) * (size.height - 40.dp.toPx())
//                val left = index * (barWidth + spacing)
//                val top = size.height - barHeight - 20.dp.toPx()
//
//                drawRect(
//                    color = barColor,
//                    topLeft = Offset(left, top),
//                    size = Size(barWidth, barHeight)
//                )
//
//                // Draw label
//                drawContext.canvas.nativeCanvas.apply {
//                    drawText(
//                        entry.label,
//                        left + barWidth / 2,
//                        size.height - 5.dp.toPx(),
//                        android.graphics.Paint().apply {
//                            color = android.graphics.Color.BLACK
//                            textSize = 12.sp.toPx()
//                            textAlign = android.graphics.Paint.Align.CENTER
//                        }
//                    )
//
//                    // Draw value
//                    drawText(
//                        "$${entry.value.toInt()}",
//                        left + barWidth / 2,
//                        top - 5.dp.toPx(),
//                        android.graphics.Paint().apply {
//                            color = android.graphics.Color.BLACK
//                            textSize = 12.sp.toPx()
//                            textAlign = android.graphics.Paint.Align.CENTER
//                        }
//                    )
//                }
//            }
//
//            // Draw horizontal axis
//            drawLine(
//                color = Color.Black,
//                start = Offset(0f, size.height - 20.dp.toPx()),
//                end = Offset(size.width, size.height - 20.dp.toPx()),
//                strokeWidth = 2.dp.toPx()
//            )
//        }
//    }
//}
//
//@Composable
//fun PieChart(
//    entries: List<ChartEntry>,
//    modifier: Modifier = Modifier,
//    colors: List<Color> = listOf(
//        Color(0xFF03DAC5), // Teal
//        Color(0xFFBB86FC), // Purple light
//        Color(0xFF6200EE), // Purple dark
//        Color(0xFF018786), // Teal dark
//        Color(0xFF3700B3), // Purple darker
//        Color(0xFF03DAC6)  // Teal light
//    )
//) {
//    Box(
//        modifier = modifier
//            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
//            .padding(16.dp)
//    ) {
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            if (entries.isEmpty()) return@Canvas
//
//            val total = entries.sumOf { it.value.toDouble() }.toFloat()
//            var startAngle = 0f
//            val radius = min(size.width, size.height) / 2 * 0.8f
//            val center = Offset(size.width / 2, size.height / 2)
//
//            entries.forEachIndexed { index, entry ->
//                val sweepAngle = (entry.value / total) * 360f
//                var color = colors[index % colors.size]
//
//                drawArc(
//                    color = color,
//                    startAngle = startAngle,
//                    sweepAngle = sweepAngle,
//                    useCenter = true,
//                    size = Size(radius * 2, radius * 2),
//                    topLeft = Offset(center.x - radius, center.y - radius)
//                )
//
//                // Draw label outside the pie chart
//                val angle = startAngle + sweepAngle / 2
//                val labelRadius = radius + 20.dp.toPx()
//                val x = center.x + labelRadius * cos(Math.toRadians(angle.toDouble())).toFloat()
//                val y = center.y + labelRadius * sin(Math.toRadians(angle.toDouble())).toFloat()
//
//                drawContext.canvas.nativeCanvas.apply {
//                    drawText(
//                        "${entry.label}: $${entry.value.toInt()}",
//                        x,
//                        y,
//                        android.graphics.Paint().apply {
//                            color = Black00
//                            textSize = 12.sp.toPx()
//                            textAlign = android.graphics.Paint.Align.CENTER
//                        }
//                    )
//                }
//
//                startAngle += sweepAngle
//            }
//        }
//    }
//}

package com.fit3163.myapplication

import android.graphics.Color
import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.fit3163.myapplication.data.analytics.AnalyticsUiState
import com.fit3163.myapplication.data.analytics.AnalyticsViewModel
import com.fit3163.myapplication.data.analytics.BarPoint
import com.fit3163.myapplication.data.analytics.TimeGranularity
import com.fit3163.myapplication.data.expenses.ExpensesViewModel
import com.fit3163.myapplication.ui.theme.ThemeMode
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun AnalyticsScreen(
    navController: NavController,
    expensesVm: ExpensesViewModel = viewModel()
) {
    // Bridge: create the analytics VM using the expensesVm’s allItems
    val analyticsVm = remember {
        AnalyticsViewModel(expensesVm.allItems)
    }
    val ui by analyticsVm.ui.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 88.dp), // keep above bottom bar
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title + filter
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Analytics", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                GranularityDropdown(ui.granularity) { analyticsVm.setGranularity(it) }
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                IconButton(onClick = { analyticsVm.prev() }) { Icon(Icons.Default.ChevronLeft, null) }
                Text(ui.periodLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = { analyticsVm.next() }) { Icon(Icons.Default.ChevronRight, null) }
            }
        }
//        // period controls
//        Spacer(Modifier.height(8.dp))
//        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
//            IconButton(onClick = { analyticsVm.prev() }) { Icon(Icons.Default.ChevronLeft, null) }
//            Text(ui.periodLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
//            IconButton(onClick = { analyticsVm.next() }) { Icon(Icons.Default.ChevronRight, null) }
//        }

        item {
            ChartCard(title = "Expenses") { Bars(ui.bars) }
        }

//        Spacer(Modifier.height(8.dp))
//        ChartCard(title = "Expenses") { Bars(ui.bars) }

        item{
            ChartCard(title = "By categories") { Donut(ui) }
        }
//        Spacer(Modifier.height(12.dp))
//        ChartCard(title = "By categories") { Donut(ui) }

        items(ui.byCategory, key = { it.category.name }) { slice ->
            CategoryRow(
                label = slice.category.label,
                amount = slice.total,
                percent = slice.percent,
                emoji = slice.category.icon
            )
        }
//        Spacer(Modifier.height(8.dp))
//        ui.byCategory.forEach {
//            CategoryRow(label = it.category.label, amount = it.total, percent = it.percent, emoji = it.category.icon)
//            Spacer(Modifier.height(6.dp))
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GranularityDropdown(selected: TimeGranularity, onChange: (TimeGranularity) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val label = when (selected) {
        TimeGranularity.WEEK -> "By Week"
        TimeGranularity.MONTH -> "By Month"
        TimeGranularity.YEAR -> "By Year"
    }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor().width(140.dp).height(54.dp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("By Week") }, onClick = { onChange(TimeGranularity.WEEK); expanded = false })
            DropdownMenuItem(text = { Text("By Month") }, onClick = { onChange(TimeGranularity.MONTH); expanded = false })
            DropdownMenuItem(text = { Text("By Year") }, onClick = { onChange(TimeGranularity.YEAR); expanded = false })
        }
    }
}

@Composable private fun ChartCard(title: String, content: @Composable () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(220.dp)) { content() }
        }
    }
}

/* ---------------- BAR ---------------- */

@Composable private fun Bars(points: List<BarPoint>) {
    val entries = remember(points) { points.mapIndexed { i, p -> BarEntry(i.toFloat(), p.value) } }
    val labels = remember(points) { points.map { it.label } }
    val dataSet = remember(entries) {
        BarDataSet(entries, "").apply {
            valueTextSize = 10f

            valueFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry?): String {
                    return if (barEntry != null && barEntry.y > 0f) {
                        barEntry.y.toInt().toString()
                    } else {
                        "" // empty → no label
                    }
                }
            }
        }
    }
    val data = remember(dataSet) { BarData(dataSet).apply { barWidth = 0.5f } }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
        BarChart(context).apply {
            description.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String = labels.getOrNull(value.toInt()) ?: ""
            }
            legend.isEnabled = false
            this.data = data
            invalidate()
        }
    }, update = { chart ->
        chart.data = data
        chart.invalidate()
    })
}

/* ---------------- DONUT ---------------- */

@Composable private fun Donut(ui: AnalyticsUiState) {
    val categoryColors = listOf(
        Color.parseColor("#1F77B4"), // Food & Drinks
        Color.parseColor("#FF7F0E"), // Transport
        Color.parseColor("#2CA02C"), // Entertainment
        Color.parseColor("#D62728"), // Groceries
        Color.parseColor("#9467BD"), // Utilities
        Color.parseColor("#8C564B"), // Shopping
        Color.parseColor("#E377C2"), // Healthcare
        Color.parseColor("#7F7F7F"), // Education
        Color.parseColor("#BCBD22"), // Travel
        Color.parseColor("#17BECF")  // Others
    )

    val entries = remember(ui.byCategory) {
        ui.byCategory.map { PieEntry(it.total.toFloat(), it.category.icon) }
    }
    val set = remember(entries) {
        PieDataSet(entries, "").apply {
            sliceSpace = 2f
            valueTextSize = 12f
            valueTypeface = Typeface.DEFAULT_BOLD
            setDrawValues(false)
            colors = categoryColors
            // leave MP default colors or inject your palette
        }
    }
    val data = remember(set) { PieData(set) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
        PieChart(context).apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setUsePercentValues(false)
            setDrawEntryLabels(false)
//
            legend.apply {
                isEnabled = true
                //isWordWrapEnabled = true
                textSize = 18f   // bigger so emojis look clear
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
//                xEntrySpace = 8f
//                yEntrySpace = 6f
            }

            isRotationEnabled = false
            this.data = data
            invalidate()
        }
    }, update = { chart ->
        chart.data = data
        chart.invalidate()
    })
}

/* ---------------- ROW ---------------- */

@Composable
private fun CategoryRow(label: String, amount: Double, percent: Int, emoji: String) {
    Card(shape = RoundedCornerShape(12.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row { Text(emoji, fontSize = 24.sp); Spacer(Modifier.width(8.dp)); Text(label, fontSize = 24.sp) }
            Column{
                Text("RM ${"%.0f".format(amount)}", fontWeight = FontWeight.Bold)
                Text("${percent}%")
            }
        }
    }
}