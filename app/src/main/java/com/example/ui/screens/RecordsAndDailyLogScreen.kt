package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.local.dao.MealWithPhotos
import com.example.data.local.entity.CoinTransactionEntity
import com.example.data.local.entity.CompletedExerciseEntity
import com.example.data.local.entity.DailyHistoryEntity
import com.example.data.local.entity.DailyStepsEntity
import com.example.ui.components.WeeklyStepsBarChart
import com.example.ui.theme.DeFittBlack
import com.example.ui.theme.DeFittBorder
import com.example.ui.theme.DeFittGold
import com.example.ui.theme.DeFittSurfaceCard
import com.example.ui.theme.DeFittTextSecondary
import com.example.ui.theme.DeFittWhite
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenContainer
import com.example.util.DateUtils
import java.io.File
import java.util.Calendar
import java.util.Locale

@Composable
fun RecordsAndDailyLogScreen(
    todayKey: String,
    selectedDateKey: String,
    selectedDateHistory: DailyHistoryEntity?,
    selectedDateMeals: List<MealWithPhotos>,
    selectedDateExercises: List<CompletedExerciseEntity>,
    selectedDateCoins: List<CoinTransactionEntity>,
    allHistory: List<DailyHistoryEntity>,
    allDailySteps: List<DailyStepsEntity>,
    stepGoal: Int,
    onSelectDate: (String) -> Unit,
    onExportBackupUri: (android.net.Uri) -> Unit,
    onImportBackupUri: (android.net.Uri) -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Registro Diario, 1 = Calendario, 2 = Estadísticas

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) onExportBackupUri(uri)
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) onImportBackupUri(uri)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeFittBlack),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "REGISTROS E HISTORIAL",
                    style = MaterialTheme.typography.headlineLarge,
                    color = DeFittWhite
                )
                Text(
                    text = "Tu actividad diaria, calendario y respaldos locales permanecen guardados aunque reinicies el teléfono.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeFittTextSecondary
                )
            }
        }

        // Sub-navigation pills
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Registro Diario", "Calendario", "Estadísticas").forEachIndexed { idx, title ->
                    FilterChip(
                        selected = activeTab == idx,
                        onClick = { activeTab = idx },
                        label = { Text(title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonGreen,
                            selectedLabelColor = DeFittBlack,
                            containerColor = DeFittSurfaceCard,
                            labelColor = DeFittTextSecondary
                        ),
                        modifier = Modifier.testTag("records_tab_$idx")
                    )
                }
            }
        }

        when (activeTab) {
            0 -> {
                // REGISTRO DIARIO for selectedDateKey
                item {
                    DailySummaryBanner(
                        dateKey = selectedDateKey,
                        isToday = selectedDateKey == todayKey,
                        history = selectedDateHistory,
                        onBackToToday = { onSelectDate(todayKey) }
                    )
                }

                item {
                    Text(
                        text = "COMIDAS (${selectedDateMeals.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = DeFittTextSecondary
                    )
                }
                if (selectedDateMeals.isEmpty()) {
                    item {
                        EmptyRecordCard("No se registraron comidas en esta fecha.")
                    }
                } else {
                    items(selectedDateMeals, key = { "hist_meal_${it.meal.id}" }) { m ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DeFittSurfaceCard,
                            border = BorderStroke(1.dp, DeFittBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (m.photos.isNotEmpty()) {
                                    AsyncImage(
                                        model = File(m.photos.first().localFilePath),
                                        contentDescription = m.meal.foodName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(54.dp).clip(RoundedCornerShape(10.dp))
                                    )
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = NeonGreenContainer,
                                        modifier = Modifier.size(54.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = NeonGreen)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${m.meal.mealType} · ${DateUtils.formatTime(m.meal.timestamp)}", style = MaterialTheme.typography.labelMedium, color = NeonGreen)
                                    Text(m.meal.foodName, style = MaterialTheme.typography.titleMedium, color = DeFittWhite)
                                    if (m.meal.notes.isNotBlank()) {
                                        Text(m.meal.notes, style = MaterialTheme.typography.bodyMedium, color = DeFittTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "EJERCICIOS (${selectedDateExercises.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = DeFittTextSecondary
                    )
                }
                if (selectedDateExercises.isEmpty()) {
                    item {
                        EmptyRecordCard("No se completaron ejercicios en esta fecha.")
                    }
                } else {
                    items(selectedDateExercises, key = { "hist_ex_${it.id}" }) { ex ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DeFittSurfaceCard,
                            border = BorderStroke(1.dp, DeFittBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = NeonGreen)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ex.exerciseName, style = MaterialTheme.typography.titleMedium, color = DeFittWhite)
                                    Text(
                                        text = "${ex.category.replace("_", " ")} · ${DateUtils.formatDurationMinutes(ex.durationSeconds.toLong())} · ${DateUtils.formatTime(ex.completedAt)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DeFittTextSecondary
                                    )
                                }
                                if (ex.coinsEarned > 0) {
                                    Text("+${ex.coinsEarned} D", style = MaterialTheme.typography.labelLarge, color = DeFittGold, fontFamily = JetBrainsMonoFamily)
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "RECOMPENSAS DEL DÍA (${selectedDateCoins.sumOf { it.amount }} D)",
                        style = MaterialTheme.typography.labelLarge,
                        color = DeFittTextSecondary
                    )
                }
                if (selectedDateCoins.isEmpty()) {
                    item { EmptyRecordCard("Sin monedas obtenidas en esta fecha.") }
                } else {
                    items(selectedDateCoins, key = { "hist_coin_${it.id}" }) { c ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DeFittSurfaceCard,
                            border = BorderStroke(1.dp, DeFittBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(c.reason, style = MaterialTheme.typography.bodyLarge, color = DeFittWhite)
                                    Text(DateUtils.formatTime(c.createdAt), style = MaterialTheme.typography.bodyMedium, color = DeFittTextSecondary)
                                }
                                Text("+${c.amount} D", style = MaterialTheme.typography.titleMedium, color = DeFittGold, fontFamily = JetBrainsMonoFamily)
                            }
                        }
                    }
                }
            }

            1 -> {
                // CALENDARIO
                item {
                    MonthlyCalendarCard(
                        selectedDateKey = selectedDateKey,
                        historyMap = allHistory.associateBy { it.dateKey },
                        onDateClicked = { dateKey ->
                            onSelectDate(dateKey)
                            activeTab = 0
                        }
                    )
                }

                item {
                    Text(
                        text = "HISTORIAL DIARIO COMPLETO",
                        style = MaterialTheme.typography.labelLarge,
                        color = DeFittTextSecondary
                    )
                }
                items(allHistory, key = { "day_${it.dateKey}" }) { h ->
                    val distKm = String.format(Locale.getDefault(), "%.2f km", h.distanceMeters / 1000.0)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DeFittSurfaceCard,
                        border = BorderStroke(1.dp, if (h.dateKey == selectedDateKey) NeonGreen else DeFittBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectDate(h.dateKey)
                                activeTab = 0
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(DateUtils.formatDisplayDateFromKey(h.dateKey), style = MaterialTheme.typography.titleMedium, color = DeFittWhite)
                                Text(
                                    text = "${h.mealsCount} comidas · ${h.exercisesCount} ejercicios · ${h.coinsEarned} D",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DeFittTextSecondary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${h.steps} pasos", style = MaterialTheme.typography.titleMedium, color = NeonGreen, fontFamily = JetBrainsMonoFamily)
                                Text(distKm, style = MaterialTheme.typography.bodyMedium, color = DeFittTextSecondary)
                            }
                        }
                    }
                }
            }

            2 -> {
                // ESTADÍSTICAS SEMANALES, MENSUALES & RESPALDO
                item {
                    val last7Keys = DateUtils.recentDateKeys(7, todayKey)
                    val stepsByKey = allDailySteps.associateBy { it.dateKey }
                    val weekValues = last7Keys.map { stepsByKey[it]?.steps ?: 0 }
                    val weekLabels = last7Keys.map { DateUtils.formatShortDay(it) }
                    WeeklyStepsBarChart(labels = weekLabels, values = weekValues, goal = stepGoal)
                }

                item {
                    val last7Keys = DateUtils.recentDateKeys(7, todayKey).toSet()
                    val last30Keys = DateUtils.recentDateKeys(30, todayKey).toSet()
                    val weekSteps = allDailySteps.filter { it.dateKey in last7Keys }.sumOf { it.steps }
                    val monthSteps = allDailySteps.filter { it.dateKey in last30Keys }.sumOf { it.steps }
                    val totalExercises = allHistory.sumOf { it.exercisesCount }
                    val totalMeals = allHistory.sumOf { it.mealsCount }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatBox("Últimos 7 días", "$weekSteps pasos", Modifier.weight(1f))
                            StatBox("Últimos 30 días", "$monthSteps pasos", Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatBox("Ejercicios totales", totalExercises.toString(), Modifier.weight(1f))
                            StatBox("Comidas totales", totalMeals.toString(), Modifier.weight(1f))
                        }
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = DeFittSurfaceCard,
                        border = BorderStroke(1.dp, DeFittBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "RESPALDO LOCAL SIN INTERNET",
                                style = MaterialTheme.typography.labelLarge,
                                color = NeonGreen
                            )
                            Text(
                                text = "Exporta todo tu historial, perfil, comidas y logros a un archivo local o restaura una copia previa.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DeFittTextSecondary
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(
                                    onClick = { exportLauncher.launch("defitt_respaldo_$todayKey.json") },
                                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
                                    modifier = Modifier.weight(1f).testTag("records_export_button")
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Exportar", color = DeFittWhite)
                                }
                                OutlinedButton(
                                    onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                                    border = BorderStroke(1.dp, DeFittBorder),
                                    modifier = Modifier.weight(1f).testTag("records_import_button")
                                ) {
                                    Icon(Icons.Default.Upload, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Restaurar", color = DeFittWhite)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun DailySummaryBanner(
    dateKey: String,
    isToday: Boolean,
    history: DailyHistoryEntity?,
    onBackToToday: () -> Unit
) {
    val steps = history?.steps ?: 0
    val distanceKm = String.format(Locale.getDefault(), "%.2f km", (history?.distanceMeters ?: 0.0) / 1000.0)
    val activeMin = DateUtils.formatDurationMinutes(history?.activeSeconds ?: 0L)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = DeFittSurfaceCard,
        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isToday) "REGISTRO DIARIO · HOY" else "REGISTRO DIARIO",
                        style = MaterialTheme.typography.labelLarge,
                        color = NeonGreen
                    )
                    Text(
                        text = DateUtils.formatDisplayDateFromKey(dateKey),
                        style = MaterialTheme.typography.titleLarge,
                        color = DeFittWhite
                    )
                }
                if (!isToday) {
                    Text(
                        text = "Ir a hoy",
                        style = MaterialTheme.typography.labelLarge,
                        color = NeonGreen,
                        modifier = Modifier.clickable(onClick = onBackToToday)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatSmall("Pasos", steps.toString())
                StatSmall("Distancia", distanceKm)
                StatSmall("Actividad", activeMin)
                StatSmall("Logros", (history?.achievementsUnlockedCount ?: 0).toString())
            }
        }
    }
}

@Composable
private fun StatSmall(label: String, value: String) {
    Column {
        Text(value, style = MaterialTheme.typography.titleMedium, color = DeFittWhite, fontFamily = JetBrainsMonoFamily)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = DeFittTextSecondary)
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = DeFittSurfaceCard,
        border = BorderStroke(1.dp, DeFittBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = DeFittTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, color = NeonGreen, fontFamily = JetBrainsMonoFamily)
        }
    }
}

@Composable
private fun EmptyRecordCard(message: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DeFittSurfaceCard,
        border = BorderStroke(1.dp, DeFittBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = DeFittTextSecondary,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun MonthlyCalendarCard(
    selectedDateKey: String,
    historyMap: Map<String, DailyHistoryEntity>,
    onDateClicked: (String) -> Unit
) {
    val initialCal = remember(selectedDateKey) { DateUtils.parseDateKey(selectedDateKey) }
    var currentYear by remember { mutableIntStateOf(initialCal.get(Calendar.YEAR)) }
    var currentMonth by remember { mutableIntStateOf(initialCal.get(Calendar.MONTH)) }

    val monthCal = remember(currentYear, currentMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeekOffset = (monthCal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = DeFittSurfaceCard,
        border = BorderStroke(1.dp, DeFittBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (currentMonth == 0) {
                        currentMonth = 11
                        currentYear--
                    } else currentMonth--
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mes anterior", tint = DeFittWhite)
                }
                Text(
                    text = DateUtils.monthYearTitle(currentYear, currentMonth),
                    style = MaterialTheme.typography.titleLarge,
                    color = DeFittWhite
                )
                IconButton(onClick = {
                    if (currentMonth == 11) {
                        currentMonth = 0
                        currentYear++
                    } else currentMonth++
                }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mes siguiente", tint = DeFittWhite)
                }
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { d ->
                    Text(
                        text = d,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = DeFittTextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            val totalCells = firstDayOfWeekOffset + daysInMonth
            val rows = (totalCells + 6) / 7
            for (r in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (c in 0 until 7) {
                        val cellIndex = r * 7 + c
                        val dayNum = cellIndex - firstDayOfWeekOffset + 1
                        if (dayNum in 1..daysInMonth) {
                            val cellCal = (monthCal.clone() as Calendar).apply {
                                set(Calendar.DAY_OF_MONTH, dayNum)
                            }
                            val key = DateUtils.formatDateKey(cellCal)
                            val h = historyMap[key]
                            val hasActivity = h != null && (h.steps > 0 || h.mealsCount > 0 || h.exercisesCount > 0)
                            val isSelected = key == selectedDateKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> NeonGreen
                                            hasActivity -> NeonGreenContainer
                                            else -> DeFittBlack.copy(alpha = 0.4f)
                                        }
                                    )
                                    .clickable { onDateClicked(key) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) DeFittBlack else if (hasActivity) NeonGreen else DeFittWhite
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
