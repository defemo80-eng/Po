package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val keyFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val timeFormat: SimpleDateFormat
        get() = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val fullDateFormat: SimpleDateFormat
        get() = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))

    private val dayMonthFormat: SimpleDateFormat
        get() = SimpleDateFormat("dd MMM", Locale("es", "ES"))

    private val weekdayShortFormat: SimpleDateFormat
        get() = SimpleDateFormat("EEE", Locale("es", "ES"))

    fun todayKey(nowMillis: Long = System.currentTimeMillis()): String =
        keyFormat.format(Date(nowMillis))

    fun formatDateKey(calendar: Calendar): String = keyFormat.format(calendar.time)

    fun parseDateKey(dateKey: String): Calendar {
        val cal = Calendar.getInstance()
        val parsed = runCatching { keyFormat.parse(dateKey) }.getOrNull()
        if (parsed != null) {
            cal.time = parsed
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    fun daysBetweenInclusiveExclusive(startDateKey: String, endDateKey: String): List<String> {
        val start = parseDateKey(startDateKey)
        val end = parseDateKey(endDateKey)
        if (!start.before(end)) return emptyList()
        val result = mutableListOf<String>()
        val cursor = start.clone() as Calendar
        cursor.add(Calendar.DAY_OF_YEAR, 1)
        var guard = 0
        while (!cursor.after(end) && guard < 3650) {
            result.add(formatDateKey(cursor))
            cursor.add(Calendar.DAY_OF_YEAR, 1)
            guard++
        }
        return result
    }

    fun recentDateKeys(days: Int, endDateKey: String = todayKey()): List<String> {
        val end = parseDateKey(endDateKey)
        val result = mutableListOf<String>()
        for (offset in (days - 1) downTo 0) {
            val c = end.clone() as Calendar
            c.add(Calendar.DAY_OF_YEAR, -offset)
            result.add(formatDateKey(c))
        }
        return result
    }

    fun formatTime(timestampMillis: Long): String = timeFormat.format(Date(timestampMillis))

    fun formatDisplayDate(timestampMillis: Long): String =
        fullDateFormat.format(Date(timestampMillis))

    fun formatDisplayDateFromKey(dateKey: String): String =
        fullDateFormat.format(parseDateKey(dateKey).time)

    fun formatShortDay(dateKey: String): String =
        weekdayShortFormat.format(parseDateKey(dateKey).time).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString()
        }

    fun formatDayMonth(dateKey: String): String =
        dayMonthFormat.format(parseDateKey(dateKey).time)

    fun formatDistance(distanceMeters: Double): String {
        val safe = distanceMeters.coerceAtLeast(0.0)
        return if (safe < 1000.0) {
            "${safe.toInt()} m"
        } else {
            String.format(Locale.getDefault(), "%.2f km", safe / 1000.0)
        }
    }

    fun formatDurationMinutes(seconds: Long): String {
        if (seconds <= 0L) return "0 min"
        if (seconds < 60L) return "${seconds} s"
        val mins = seconds / 60
        val hrs = mins / 60
        val remMins = mins % 60
        return if (hrs > 0) "${hrs}h ${remMins}m" else "${mins} min"
    }

    fun formatTimerClock(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return String.format(Locale.US, "%02d:%02d", m, s)
    }

    fun monthYearTitle(year: Int, monthZeroBased: Int): String {
        val c = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, monthZeroBased)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
            .format(c.time)
            .replaceFirstChar { it.titlecase(Locale("es", "ES")) }
    }
}
