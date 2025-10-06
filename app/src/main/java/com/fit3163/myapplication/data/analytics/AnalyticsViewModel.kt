package com.fit3163.myapplication.data.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit3163.myapplication.data.expenses.Category
import com.fit3163.myapplication.data.expenses.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

enum class TimeGranularity { WEEK, MONTH, YEAR }

data class CategorySlice(val category: Category, val total: Double, val percent: Int)
data class BarPoint(val label: String, val value: Float)

data class AnalyticsUiState(
    val granularity: TimeGranularity = TimeGranularity.MONTH,
    val anchorDate: LocalDate = LocalDate.now(), // “current” week/month/year depends on granularity
    val periodLabel: String = "",
    val bars: List<BarPoint> = emptyList(),
    val byCategory: List<CategorySlice> = emptyList(),
    val total: Double = 0.0
)

class AnalyticsComputer(private val all: List<Expense>) {
    fun compute(gran: TimeGranularity, anchor: LocalDate): AnalyticsUiState {
        val (start, endExclusive, label) = when (gran) {
            TimeGranularity.WEEK -> {
                val wf = WeekFields.of(Locale.getDefault())
                val start = anchor.with(wf.dayOfWeek(), 1)              // Monday
                val end   = start.plusDays(7)
                Triple(start, end, "${start} - ${start.plusDays(6)}")
            }
            TimeGranularity.MONTH -> {
                val ym = YearMonth.from(anchor)
                val start = ym.atDay(1)
                val end   = ym.plusMonths(1).atDay(1)
                Triple(start, end, "${ym.month.name.lowercase().replaceFirstChar { it.titlecase() }} ${ym.year}")
            }
            TimeGranularity.YEAR -> {
                val y = anchor.year
                Triple(LocalDate.of(y, 1, 1), LocalDate.of(y + 1, 1, 1), y.toString())
            }
        }

        val inRange = all.filter { !it.date.isBefore(start) && it.date.isBefore(endExclusive) }
        val total = inRange.sumOf { it.amount }

        // Bars
        val bars: List<BarPoint> = when (gran) {
            TimeGranularity.WEEK -> {
                val days = (0..6).map { start.plusDays(it.toLong()) }
                days.map { d ->
                    val sum = inRange.filter { it.date == d }.sumOf { it.amount }
                    BarPoint(d.dayOfWeek.name.take(1), sum.toFloat())   // M T W T F S S
                }
            }
            TimeGranularity.MONTH -> {
                // Weeks-in-month (1..5)
                val wf = WeekFields.of(Locale.getDefault())
                val groups = inRange.groupBy { it.date.get(wf.weekOfMonth()) }
                (1..5).map { wk ->
                    BarPoint("W$wk", (groups[wk]?.sumOf { it.amount } ?: 0.0).toFloat())
                }
            }
            TimeGranularity.YEAR -> {
                val groups = inRange.groupBy { it.date.month }
                Month.values().map { m ->
                    BarPoint(m.name.take(1), (groups[m]?.sumOf { it.amount } ?: 0.0).toFloat())
                }
            }
        }

        // Categories
        val byCatRaw = inRange.groupBy { it.category }
            .map { (cat, list) -> cat to list.sumOf { it.amount } }
            .sortedByDescending { it.second }
        val byCategory = byCatRaw.map { (cat, sum) ->
            CategorySlice(cat, sum, if (total <= 0) 0 else ((sum / total) * 100).toInt())
        }

        return AnalyticsUiState(
            granularity = gran,
            anchorDate = anchor,
            periodLabel = label,
            bars = bars,
            byCategory = byCategory,
            total = total
        )
    }
}

class AnalyticsViewModel(
    // inject a flow of all expenses from your ExpensesViewModel
    private val allItems: StateFlow<List<Expense>>
) : ViewModel() {

    private val gran = MutableStateFlow(TimeGranularity.MONTH)
    private val anchor = MutableStateFlow(LocalDate.now())

    val ui: StateFlow<AnalyticsUiState> =
        combine(allItems, gran, anchor) { items, g, a ->
            AnalyticsComputer(items).compute(g, a)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AnalyticsUiState())

    fun setGranularity(g: TimeGranularity) { gran.value = g }
    fun prev() = when (gran.value) {
        TimeGranularity.WEEK  -> anchor.update { it.minusWeeks(1) }
        TimeGranularity.MONTH -> anchor.update { it.minusMonths(1) }
        TimeGranularity.YEAR  -> anchor.update { it.minusYears(1) }
    }
    fun next() = when (gran.value) {
        TimeGranularity.WEEK  -> anchor.update { it.plusWeeks(1) }
        TimeGranularity.MONTH -> anchor.update { it.plusMonths(1) }
        TimeGranularity.YEAR  -> anchor.update { it.plusYears(1) }
    }
}