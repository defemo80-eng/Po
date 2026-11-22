package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.ExerciseEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.ui.components.ExerciseVideoMapper
import com.example.ui.components.ExerciseVideoPlayer
import com.example.ui.theme.DeFittBlack
import com.example.ui.theme.DeFittBorder
import com.example.ui.theme.DeFittGold
import com.example.ui.theme.DeFittSurfaceCard
import com.example.ui.theme.DeFittTextSecondary
import com.example.ui.theme.DeFittWarning
import com.example.ui.theme.DeFittWhite
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenContainer
import com.example.util.DateUtils
import kotlinx.coroutines.delay

@Composable
fun ExerciseDetailScreen(
    exercise: ExerciseEntity,
    profile: UserProfileEntity?,
    isDoneToday: Boolean,
    onBack: () -> Unit,
    onComplete: (elapsedSeconds: Int) -> Unit
) {
    BackHandler(onBack = onBack)

    var isRunning by remember(exercise.id) { mutableStateOf(false) }
    var isResting by remember(exercise.id) { mutableStateOf(false) }
    var remainingSeconds by remember(exercise.id) { mutableIntStateOf(exercise.durationSeconds) }
    var restRemaining by remember(exercise.id) { mutableIntStateOf(exercise.restSeconds) }
    var elapsedSeconds by remember(exercise.id) { mutableIntStateOf(0) }

    LaunchedEffect(isRunning, isResting, exercise.id) {
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

    val rawVideoRes = remember(exercise.code) { ExerciseVideoMapper.getRawVideoRes(exercise) }
    val categoryDisplay = remember(exercise.category) {
        when (exercise.category) {
            "CALENTAMIENTO" -> "Calentamiento"
            "RECUPERACION" -> "Recuperación"
            "CUERPO_COMPLETO" -> "Cuerpo completo"
            else -> exercise.category.lowercase().replaceFirstChar { it.uppercase() }
        }
    }
    val adaptationText = remember(profile, exercise.difficulty) {
        buildAdaptationTip(profile, exercise)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeFittBlack)
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("exercise_detail_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver a ejercicios",
                    tint = DeFittWhite
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = categoryDisplay.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = NeonGreen
                )
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = DeFittWhite,
                    fontWeight = FontWeight.Bold
                )
            }
            if (isDoneToday) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = NeonGreenContainer,
                    border = BorderStroke(1.dp, NeonGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completado hoy",
                            tint = NeonGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Hoy", style = MaterialTheme.typography.labelSmall, color = NeonGreen)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("exercise_detail_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. 5-second auto-looping offline demonstration video
            item {
                ExerciseVideoPlayer(
                    rawResId = rawVideoRes,
                    illustrationKey = exercise.illustrationKey,
                    exerciseName = exercise.name
                )
            }

            // 2. Category, Difficulty, Reps/Time, Rest pills
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetaPill(
                        label = "Categoría",
                        value = categoryDisplay,
                        accent = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetaPill(
                        label = "Dificultad",
                        value = exercise.difficulty,
                        accent = DeFittWhite,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetaPill(
                        label = "Tiempo / Reps",
                        value = exercise.recommendedReps,
                        accent = NeonGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetaPill(
                        label = "Descanso",
                        value = "${exercise.restSeconds} seg",
                        accent = DeFittGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. Interactive Timer + COMENZAR button
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DeFittSurfaceCard,
                    border = BorderStroke(1.5.dp, if (isRunning) NeonGreen else DeFittBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isResting) "TIEMPO DE DESCANSO" else "CRONÓMETRO ACTIVO",
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
                            if (isRunning) {
                                OutlinedButton(
                                    onClick = {
                                        isResting = !isResting
                                        if (isResting) restRemaining = exercise.restSeconds
                                    },
                                    border = BorderStroke(1.dp, DeFittGold)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = DeFittGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isResting) "Volver" else "Descansar",
                                        color = DeFittGold
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { isRunning = !isRunning },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRunning) DeFittSurfaceCard else NeonGreen,
                                    contentColor = if (isRunning) NeonGreen else DeFittBlack
                                ),
                                border = if (isRunning) BorderStroke(1.dp, NeonGreen) else null,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("exercise_start_button")
                            ) {
                                Icon(
                                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isRunning) "PAUSAR" else "COMENZAR")
                            }

                            Button(
                                onClick = {
                                    val recorded = elapsedSeconds.coerceAtLeast(exercise.durationSeconds / 2)
                                    onComplete(recorded)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonGreen,
                                    contentColor = DeFittBlack
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("exercise_complete_button")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("COMPLETAR")
                            }
                        }
                    }
                }
            }

            // 4. Explanation
            item {
                DetailSectionCard(
                    title = "Explicación del movimiento",
                    icon = Icons.Default.Info,
                    accent = NeonGreen,
                    body = exercise.explanation
                )
            }

            // 5. Step-by-step instructions
            item {
                DetailSectionCard(
                    title = "Instrucciones paso a paso",
                    icon = Icons.Default.FitnessCenter,
                    accent = NeonGreen,
                    body = exercise.instructions
                )
            }

            // 6. Safety Warning
            if (exercise.safetyWarning.isNotBlank()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF1D180A),
                        border = BorderStroke(1.dp, DeFittWarning.copy(alpha = 0.55f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = DeFittWarning,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Advertencia de seguridad",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DeFittWarning
                                )
                            }
                            Text(
                                text = exercise.safetyWarning,
                                style = MaterialTheme.typography.bodyLarge,
                                color = DeFittWhite
                            )
                        }
                    }
                }
            }

            // 7. Age & Experience Adaptation
            item {
                DetailSectionCard(
                    title = "Adaptación según tu edad y experiencia",
                    icon = Icons.Default.Tune,
                    accent = DeFittGold,
                    body = adaptationText
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun MetaPill(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = DeFittSurfaceCard,
        border = BorderStroke(1.dp, DeFittBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = DeFittTextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    body: String
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = DeFittSurfaceCard,
        border = BorderStroke(1.dp, DeFittBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = DeFittWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = DeFittTextSecondary
            )
        }
    }
}

private fun buildAdaptationTip(profile: UserProfileEntity?, exercise: ExerciseEntity): String {
    if (profile == null) {
        return "Mantén una técnica controlada priorizando la postura. Descansa ${exercise.restSeconds}s entre series si lo necesitas."
    }
    val ageTip = when {
        profile.age < 16 -> "Edad (${profile.age} años): prioriza técnica limpia y peso corporal moderado; evita forzar articulaciones."
        profile.age >= 50 -> "Edad (${profile.age} años): realiza transiciones lentas, cuida el equilibrio y usa apoyo estable si lo requieres."
        else -> "Edad (${profile.age} años): ajusta la amplitud de movimiento de forma progresiva."
    }
    val expTip = when (profile.experienceLevel) {
        "PRINCIPIANTE" -> "Nivel principiante: puedes reducir a 2 series o aumentar 15s de descanso mientras dominas la forma."
        "INTERMEDIO" -> "Nivel intermedio: busca ritmo constante y control excéntrico de 2 segundos por repetición."
        else -> "Nivel avanzado: mantén pausas estrictas (${exercise.restSeconds}s) y máxima estabilidad en cada serie."
    }
    return "$ageTip\n$expTip"
}
