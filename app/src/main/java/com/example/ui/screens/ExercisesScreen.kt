package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.DeFittSurfaceCard
import com.example.ui.theme.DeFittTextSecondary
import com.example.ui.theme.DeFittWhite
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenContainer

private val EXERCISE_CATEGORIES = listOf(
    "TODOS" to "Todos",
    "CALENTAMIENTO" to "Calentamiento",
    "RECUPERACION" to "Recuperación",
    "PECHO" to "Pecho",
    "BRAZOS" to "Brazos",
    "ABDOMEN" to "Abdomen",
    "PIERNAS" to "Piernas",
    "ESPALDA" to "Espalda",
    "RESISTENCIA" to "Resistencia",
    "MOVILIDAD" to "Movilidad",
    "CUERPO_COMPLETO" to "Cuerpo completo"
)

@Composable
fun ExercisesScreen(
    profile: UserProfileEntity?,
    allExercises: List<ExerciseEntity>,
    personalizedExercises: List<ExerciseEntity>,
    routines: List<RoutineEntity>,
    completedToday: List<CompletedExerciseEntity>,
    onOpenExercise: (ExerciseEntity) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("TODOS") }
    var selectedRoutine by remember { mutableStateOf<RoutineEntity?>(null) }

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
            .background(DeFittBlack)
            .testTag("exercises_lazy_column"),
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
                        "Adaptado a tu nivel ${profile.experienceLevel.lowercase()} (${profile.availableMinutes} min/día). Cada ejercicio incluye video de 5s sin internet."
                    } else {
                        "Entrenamiento guiado offline con video de 5s en bucle para cada ejercicio."
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
                            onClick = { onOpenExercise(exercise) }
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
                onClick = { onOpenExercise(exercise) }
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
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
            .width(196.dp)
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
                } else {
                    Icon(Icons.Default.PlayCircle, contentDescription = "Ver video 5s", tint = NeonGreen)
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
                text = "${exercise.recommendedReps} · Video 5s",
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
    val categoryLabel = when (exercise.category) {
        "CALENTAMIENTO" -> "CALENTAMIENTO"
        "RECUPERACION" -> "RECUPERACIÓN"
        "CUERPO_COMPLETO" -> "CUERPO COMPLETO"
        else -> exercise.category
    }
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
                        text = categoryLabel,
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
                    text = "${exercise.recommendedReps} · Descanso: ${exercise.restSeconds}s · Video 5s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeFittTextSecondary
                )
            }
            if (isDoneToday) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Completado hoy", tint = NeonGreen)
            } else {
                Icon(Icons.Default.PlayCircle, contentDescription = "Abrir ejercicio", tint = NeonGreen)
            }
        }
    }
}
