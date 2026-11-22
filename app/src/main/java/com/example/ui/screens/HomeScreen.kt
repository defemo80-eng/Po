package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.DailyStepsEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.sensors.ActiveStepSensorKind
import com.example.sensors.DeviceSensorStatus
import com.example.ui.components.DeFittCoinsPill
import com.example.ui.components.DeFittOfficialLogo
import com.example.ui.components.StepProgressRing
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
import java.util.Locale

@Composable
fun HomeScreen(
    user: UserEntity,
    profile: UserProfileEntity?,
    todaySteps: DailyStepsEntity?,
    mealsTodayCount: Int,
    exercisesTodayCount: Int,
    unlockedAchievementsCount: Int,
    totalAchievementsCount: Int,
    coinBalance: Int,
    sensorStatus: DeviceSensorStatus,
    onRefreshSensors: () -> Unit,
    onOpenMeals: () -> Unit,
    onOpenExercises: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenDailyLog: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val context = LocalContext.current

    val activityPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        onRefreshSensors()
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        onRefreshSensors()
    }

    val steps = todaySteps?.steps ?: 0
    val goal = profile?.dailyStepGoal ?: todaySteps?.stepGoal ?: 6000
    val distanceMeters = todaySteps?.distanceMeters ?: 0.0
    val activeSeconds = todaySteps?.activeSeconds ?: 0L
    val distanceFormatted = String.format(Locale.getDefault(), "%.2f km", distanceMeters / 1000.0)
    val activeTimeFormatted = DateUtils.formatDurationMinutes(activeSeconds)
    val progressPct = if (goal > 0) ((steps * 100f) / goal).toInt().coerceAtMost(999) else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeFittBlack),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    DeFittOfficialLogo(size = 44.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Hola, ${user.fullName.substringBefore(" ")}",
                            style = MaterialTheme.typography.titleLarge,
                            color = DeFittWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = DateUtils.formatDisplayDate(System.currentTimeMillis()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = DeFittTextSecondary
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DeFittCoinsPill(
                        balance = coinBalance,
                        modifier = Modifier
                            .clickable(onClick = onOpenAchievements)
                            .testTag("home_coins_pill")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onOpenProfile,
                        modifier = Modifier.testTag("home_profile_button")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = DeFittSurfaceCard,
                            border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Perfil y configuración",
                                    tint = NeonGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hero Step Counter Card
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = DeFittSurfaceCard,
                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    NeonGreenContainer.copy(alpha = 0.55f),
                                    DeFittSurfaceCard
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = DeFittBlack.copy(alpha = 0.7f),
                            border = BorderStroke(1.dp, DeFittBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = if (sensorStatus.hasActivityRecognitionPermission &&
                                        sensorStatus.sensorKind != ActiveStepSensorKind.NONE
                                    ) NeonGreen else DeFittWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = sensorStatus.sensorKind.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DeFittTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        StepProgressRing(
                            steps = steps,
                            goal = goal,
                            distanceKmFormatted = distanceFormatted,
                            activeTimeFormatted = activeTimeFormatted
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MiniStat("Progreso", "$progressPct%", NeonGreen)
                            MiniStat("Distancia", distanceFormatted, DeFittWhite)
                            MiniStat("Actividad", activeTimeFormatted, DeFittWhite)
                        }
                    }
                }
            }
        }

        // Real Sensor Permission / Limitation Notice
        if (!sensorStatus.hasActivityRecognitionPermission ||
            sensorStatus.limitationMessage != null ||
            !sensorStatus.hasNotificationPermission
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF1D1A0B),
                    border = BorderStroke(1.dp, DeFittWarning.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BatteryAlert,
                                contentDescription = null,
                                tint = DeFittWarning
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Estado de sensores y segundo plano",
                                style = MaterialTheme.typography.titleMedium,
                                color = DeFittWhite
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = sensorStatus.limitationMessage
                                ?: "Activa notificaciones para mantener el conteo automático en segundo plano.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DeFittTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!sensorStatus.hasActivityRecognitionPermission &&
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                            ) {
                                Button(
                                    onClick = {
                                        activityPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonGreen,
                                        contentColor = DeFittBlack
                                    ),
                                    modifier = Modifier.testTag("grant_activity_permission_button")
                                ) {
                                    Text("Permitir pasos")
                                }
                            }
                            if (!sensorStatus.hasNotificationPermission &&
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                            ) {
                                TextButton(
                                    onClick = {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                ) {
                                    Text("Permitir notificaciones", color = NeonGreen)
                                }
                            }
                            if (!sensorStatus.isIgnoringBatteryOptimizations) {
                                TextButton(
                                    onClick = {
                                        runCatching {
                                            context.startActivity(
                                                Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                }
                                            )
                                        }.onFailure {
                                            runCatching {
                                                context.startActivity(
                                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                        data = Uri.fromParts("package", context.packageName, null)
                                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                ) {
                                    Text("Revisar batería", color = DeFittWarning)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Overview 2-column Grid Cards
        item {
            Text(
                text = "RESUMEN DE HOY",
                style = MaterialTheme.typography.labelLarge,
                color = DeFittTextSecondary
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardMetricCard(
                        title = "Pasos",
                        value = steps.toString(),
                        subtitle = "Sensor real",
                        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                        accent = NeonGreen,
                        onClick = onOpenDailyLog,
                        modifier = Modifier.weight(1f).testTag("home_card_steps")
                    )
                    DashboardMetricCard(
                        title = "Distancia",
                        value = distanceFormatted,
                        subtitle = "Estimada",
                        icon = Icons.Default.Timeline,
                        accent = NeonGreen,
                        onClick = onOpenDailyLog,
                        modifier = Modifier.weight(1f).testTag("home_card_distance")
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardMetricCard(
                        title = "Actividad",
                        value = activeTimeFormatted,
                        subtitle = "Tiempo activo",
                        icon = Icons.Default.Timer,
                        accent = NeonGreen,
                        onClick = onOpenDailyLog,
                        modifier = Modifier.weight(1f).testTag("home_card_activity")
                    )
                    DashboardMetricCard(
                        title = "Comidas",
                        value = mealsTodayCount.toString(),
                        subtitle = "Registradas hoy",
                        icon = Icons.Default.Restaurant,
                        accent = NeonGreen,
                        onClick = onOpenMeals,
                        modifier = Modifier.weight(1f).testTag("home_card_meals")
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardMetricCard(
                        title = "Ejercicios",
                        value = exercisesTodayCount.toString(),
                        subtitle = "Completados hoy",
                        icon = Icons.Default.FitnessCenter,
                        accent = NeonGreen,
                        onClick = onOpenExercises,
                        modifier = Modifier.weight(1f).testTag("home_card_exercises")
                    )
                    DashboardMetricCard(
                        title = "Logros",
                        value = "$unlockedAchievementsCount/$totalAchievementsCount",
                        subtitle = "Desbloqueados",
                        icon = Icons.Default.EmojiEvents,
                        accent = DeFittGold,
                        onClick = onOpenAchievements,
                        modifier = Modifier.weight(1f).testTag("home_card_achievements")
                    )
                }
            }
        }

        // Quick Shortcuts Row
        item {
            Text(
                text = "ACCESOS RÁPIDOS",
                style = MaterialTheme.typography.labelLarge,
                color = DeFittTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    QuickShortcutChip("Subir comida", Icons.Default.Restaurant, onOpenMeals, "shortcut_meals")
                }
                item {
                    QuickShortcutChip("Entrenar ahora", Icons.Default.FitnessCenter, onOpenExercises, "shortcut_exercises")
                }
                item {
                    QuickShortcutChip("Registro diario", Icons.Default.History, onOpenDailyLog, "shortcut_daily_log")
                }
                item {
                    QuickShortcutChip("Logros y Coins", Icons.Default.EmojiEvents, onOpenAchievements, "shortcut_achievements")
                }
                item {
                    QuickShortcutChip("Mi perfil", Icons.Default.Person, onOpenProfile, "shortcut_profile")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
private fun MiniStat(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = valueColor, fontFamily = JetBrainsMonoFamily)
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = DeFittTextSecondary)
    }
}

@Composable
private fun DashboardMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = DeFittSurfaceCard,
        border = BorderStroke(1.dp, DeFittBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.labelLarge, color = DeFittTextSecondary)
                Icon(imageVector = icon, contentDescription = title, tint = accent, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = DeFittWhite,
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = DeFittTextSecondary
            )
        }
    }
}

@Composable
private fun QuickShortcutChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick).testTag(tag),
        shape = RoundedCornerShape(14.dp),
        color = NeonGreenContainer,
        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelLarge, color = DeFittWhite)
        }
    }
}
