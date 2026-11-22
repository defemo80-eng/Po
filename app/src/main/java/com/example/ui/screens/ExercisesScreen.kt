package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.CompletedExerciseEntity
import com.example.data.local.entity.ExerciseEntity
import com.example.data.local.entity.RoutineEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.ui.components.ExerciseIllustration
import com.example.ui.theme.DeFittBlack
import com.example.ui.theme.DeFittBorder
import com.example.ui.theme.DeFittGold
import com.example.ui.theme.DeFittSurface
import com.example.ui.theme.DeFittSurfaceCard
import com.example.ui.theme.DeFittTextSecondary
import com.example.ui.theme.DeFittWhite
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenContainer
import com.example.util.DateUtils
import kotlinx.coroutines.delay

private val EXERCISE_CATEGORIES = listOf(
    "TODOS" to "Todos",
    "PECHO" to "Pecho",
    "BRAZOS" to "Brazos",
    "ABDOMEN" to "Abdomen",
    "PIERNAS" to "Piernas",
    "ESPALDA" to "Espalda",
    "RESISTENCIA" to "Resistencia",
    "MOVILIDAD" to "Movilidad",
    "CUERPO_COMPLETO" to "Cuerpo completo"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    profile: UserProfileEntity?,
    allExercises: List<ExerciseEntity>,
    personalizedExercises: List<ExerciseEntity>,
    routines: List<RoutineEntity>,
    completedToday: List<CompletedExerciseEntity>,
    onCompleteExercise: (ExerciseEntity, Int) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("TODOS") }
    var selectedRoutine by remember { mutableStateOf<RoutineEntity?>(null) }
    var activeExercise by remember { mutableStateOf<ExerciseEntity?>(null) }

    val visibleExercises = remember(allExercises, selectedCategory, selectedRoutine) {
        when {
            selectedRoutine != null -> {
                val codes = selectedRoutine!!.exerciseCodesCsv.split(",").toSet()
                allExercises.filter { it.code in codes }
            }
            selectedCategory == "TODOS" -> allExercises
            else -> allExercises.filter { it.category == selectedCategory }
        }
    }

    val completedCodesToday = remember(completedToday) {
        completedToday.map { it.exerciseName }.toSet()
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
                    text = "EJERCICIOS",
                    style = MaterialTheme.typography.headlineLarge,
                    color = DeFittWhite
                )
                Text(
                    text = if (profile != null) {
                        "Adaptado a tu nivel ${profile.experienceLevel.lowercase()} (${profile.availableMinutes} min/día). Todo incluido sin internet."
                    } else {
                        "Entrenamiento guiado offline con calentamiento y recuperación."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeFittTextSecondary
                )
            }
        }

        // Personalized Plan Section
        if (personalizedExercises.isNotEmpty()) {
            item {
                Text(
                    text = "RECOMENDADO PARA TUS OBJETIVOS",
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(personalizedExercises, key = { "rec_${it.id}" }) { exercise ->
                        RecommendedExerciseTile(
                            exercise = exercise,
                            isDoneToday = exercise.name in completedCodesToday,
                            onClick = { activeExercise = exercise }
                        )
                    }
                }
            }
        }

        // Built-in Routines
        if (routines.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RUTINAS INTEGRADAS",
                        style = MaterialTheme.typography.labelLarge,
                        color = DeFittTextSecondary
                    )
                    if (selectedRoutine != null) {
                        Text(
                            text = "Ver todos",
                            style = MaterialTheme.typography.labelLarge,
                            color = NeonGreen,
                            modifier = Modifier.clickable { selectedRoutine = null }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(routines, key = { it.id }) { routine ->
                        val isSelected = selectedRoutine?.id == routine.id
                        Surface(
                            modifier = Modifier
                                .width(235.dp)
                                .clickable {
                                    selectedRoutine = if (isSelected) null else routine
                                },
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) NeonGreenContainer else DeFittSurfaceCard,
                            border = BorderStroke(1.dp, if (isSelected) NeonGreen else DeFittBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "${routine.difficulty} · ${routine.estimatedMinutes} min",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NeonGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = routine.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DeFittWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = routine.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DeFittTextSecondary,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(EXERCISE_CATEGORIES) { (code, label) ->
                    FilterChip(
                        selected = selectedRoutine == null && selectedCategory == code,
                        onClick = {
                            selectedRoutine = null
                            selectedCategory = code
                        },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonGreen,
                            selectedLabelColor = DeFittBlack,
                            containerColor = DeFittSurfaceCard,
                            labelColor = DeFittTextSecondary
                        ),
                        modifier = Modifier.testTag("exercise_cat_$code")
                    )
                }
            }
        }

        items(visibleExercises, key = { it.id }) { exercise ->
            ExerciseRowCard(
                exercise = exercise,
                isDoneToday = exercise.name in completedCodesToday,
                onClick = { activeExercise = exercise }
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    activeExercise?.let { exercise ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { activeExercise = null },
            sheetState = sheetState,
            containerColor = DeFittSurface
        ) {
            ExerciseWorkoutSheet(
                exercise = exercise,
                onComplete = { elapsed ->
                    onCompleteExercise(exercise, elapsed)
                    activeExercise = null
                },
                onClose = { activeExercise = null }
            )
        }
    }
}

@Composable
private fun RecommendedExerciseTile(
    exercise: ExerciseEntity,
    isDoneToday: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(190.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = DeFittSurfaceCard,
        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExerciseIllustration(
                    illustrationKey = exercise.illustrationKey,
                    modifier = Modifier.size(52.dp)
                )
                if (isDoneToday) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Completado hoy", tint = NeonGreen)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium,
                color = DeFittWhite,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${exercise.recommendedReps} · ${exercise.difficulty}",
                style = MaterialTheme.typography.bodyMedium,
                color = NeonGreen
            )
        }
    }
}

@Composable
private fun ExerciseRowCard(
    exercise: ExerciseEntity,
    isDoneToday: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("exercise_card_${exercise.code}"),
        shape = RoundedCornerShape(18.dp),
        color = DeFittSurfaceCard,
        border = BorderStroke(1.dp, if (isDoneToday) NeonGreen.copy(alpha = 0.5f) else DeFittBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExerciseIllustration(
                illustrationKey = exercise.illustrationKey,
                modifier = Modifier.size(66.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (exercise.isWarmupOrRecovery) "CALENTAMIENTO / RECUP." else exercise.category.replace("_", " "),
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonGreen
                    )
                    Text(text = "•", color = DeFittTextSecondary)
                    Text(
                        text = exercise.difficulty,
                        style = MaterialTheme.typography.labelMedium,
                        color = DeFittTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = DeFittWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${exercise.recommendedReps} · Descanso: ${exercise.restSeconds}s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeFittTextSecondary
                )
            }
            if (isDoneToday) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Completado hoy", tint = NeonGreen)
            }
        }
    }
}

@Composable
private fun ExerciseWorkoutSheet(
    exercise: ExerciseEntity,
    onComplete: (elapsedSeconds: Int) -> Unit,
    onClose: () -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    var isResting by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableIntStateOf(exercise.durationSeconds) }
    var restRemaining by remember { mutableIntStateOf(exercise.restSeconds) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRunning, isResting) {
        while (isRunning) {
            delay(1000L)
            if (isResting) {
                if (restRemaining > 0) restRemaining-- else isResting = false
            } else {
                elapsedSeconds++
                if (remainingSeconds > 0) remainingSeconds--
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ExerciseIllustration(
                illustrationKey = exercise.illustrationKey,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "${exercise.category.replace("_", " ")} · ${exercise.difficulty}",
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonGreen
                )
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = DeFittWhite
                )
                Text(
                    text = "${exercise.recommendedReps} · Descanso ${exercise.restSeconds}s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeFittTextSecondary
                )
            }
        }

        // Timer Box
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = DeFittBlack,
            border = BorderStroke(1.dp, if (isRunning) NeonGreen else DeFittBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isResting) "DESCANSO" else "TIEMPO DEL EJERCICIO",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isResting) DeFittGold else NeonGreen
                    )
                    Text(
                        text = DateUtils.formatTimerClock(if (isResting) restRemaining else remainingSeconds),
                        style = MaterialTheme.typography.displayMedium,
                        color = DeFittWhite,
                        fontFamily = JetBrainsMonoFamily
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isRunning) {
                        OutlinedButton(
                            onClick = {
                                isResting = !isResting
                                if (isResting) restRemaining = exercise.restSeconds
                            },
                            border = BorderStroke(1.dp, DeFittGold)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = DeFittGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isResting) "Reanudar" else "Descanso", color = DeFittGold)
                        }
                    }
                    Button(
                        onClick = { isRunning = !isRunning },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = DeFittBlack),
                        modifier = Modifier.testTag("exercise_start_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isRunning) "PAUSAR" else "COMENZAR")
                    }
                }
            }
        }

        Text(
            text = "Explicación",
            style = MaterialTheme.typography.titleMedium,
            color = DeFittWhite
        )
        Text(
            text = exercise.explanation,
            style = MaterialTheme.typography.bodyLarge,
            color = DeFittTextSecondary
        )

        Text(
            text = "Instrucciones paso a paso",
            style = MaterialTheme.typography.titleMedium,
            color = DeFittWhite
        )
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DeFittSurfaceCard,
            border = BorderStroke(1.dp, DeFittBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = exercise.instructions,
                style = MaterialTheme.typography.bodyLarge,
                color = DeFittWhite,
                modifier = Modifier.padding(14.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onClose,
                modifier = Modifier.weight(1f).height(52.dp),
                border = BorderStroke(1.dp, DeFittBorder),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Cerrar", color = DeFittTextSecondary)
            }
            Button(
                onClick = {
                    val recorded = elapsedSeconds.coerceAtLeast(exercise.durationSeconds / 2)
                    onComplete(recorded)
                },
                modifier = Modifier
                    .weight(1.6f)
                    .height(52.dp)
                    .testTag("exercise_complete_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = DeFittBlack),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("COMPLETAR")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
