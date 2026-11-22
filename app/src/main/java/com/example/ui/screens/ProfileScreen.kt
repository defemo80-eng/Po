package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.sensors.DeviceSensorStatus
import com.example.ui.components.DeFittOfficialLogo
import com.example.ui.theme.DeFittBlack
import com.example.ui.theme.DeFittBorder
import com.example.ui.theme.DeFittError
import com.example.ui.theme.DeFittSurface
import com.example.ui.theme.DeFittSurfaceCard
import com.example.ui.theme.DeFittTextSecondary
import com.example.ui.theme.DeFittWhite
import com.example.ui.theme.NeonGreen
import com.example.util.DateUtils

@Composable
fun ProfileScreen(
    user: UserEntity,
    profile: UserProfileEntity?,
    settings: AppSettingsEntity?,
    sensorStatus: DeviceSensorStatus,
    onBack: () -> Unit,
    onUpdateProfile: (
        fullName: String,
        age: Int,
        weightKg: Double?,
        goalsDescription: String,
        experienceLevel: String,
        activityLevel: String,
        availableMinutes: Int,
        dailyStepGoal: Int,
        newPin: String?
    ) -> Unit,
    onUpdateSettings: (
        notificationsEnabled: Boolean,
        reminderHour: Int,
        reminderMinute: Int,
        backgroundTrackingEnabled: Boolean
    ) -> Unit,
    onExportBackupUri: (Uri) -> Unit,
    onImportBackupUri: (Uri) -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current

    var showEditDialog by remember { mutableStateOf(false) }
    var showPermissionsDialog by remember { mutableStateOf(false) }
    var showAppInfoDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeFittBlack)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("profile_back_button")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = DeFittWhite)
            }
            Text(
                text = "Perfil y Configuración",
                style = MaterialTheme.typography.titleLarge,
                color = DeFittWhite
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Identity & Profile Header with Official DeFitt Logo
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = DeFittSurfaceCard,
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DeFittOfficialLogo(size = 64.dp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.fullName,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = DeFittWhite
                                )
                                Text(
                                    text = "${profile?.age ?: "-"} años" +
                                        if (profile?.weightKg != null) " · ${profile.weightKg} kg" else "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NeonGreen
                                )
                                Text(
                                    text = "Miembro desde: ${DateUtils.formatDisplayDate(user.createdAt)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DeFittTextSecondary
                                )
                            }
                            IconButton(
                                onClick = { showEditDialog = true },
                                modifier = Modifier.testTag("profile_edit_button")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar perfil", tint = NeonGreen)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Objetivos personales", style = MaterialTheme.typography.labelMedium, color = NeonGreen)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = profile?.goalsDescription ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DeFittWhite
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nivel: ${profile?.experienceLevel ?: "-"} · Actividad: ${profile?.activityLevel ?: "-"} · Meta: ${profile?.dailyStepGoal ?: 6000} pasos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DeFittTextSecondary
                        )
                    }
                }
            }

            // Preferences: Notifications & Background Step Tracking
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = DeFittSurfaceCard,
                    border = BorderStroke(1.dp, DeFittBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("PREFERENCIAS LOCALES", style = MaterialTheme.typography.labelLarge, color = NeonGreen)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = NeonGreen)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Recordatorio diario (19:00)", style = MaterialTheme.typography.titleMedium, color = DeFittWhite)
                                    Text("Notificación local sin internet", style = MaterialTheme.typography.bodyMedium, color = DeFittTextSecondary)
                                }
                            }
                            Switch(
                                checked = settings?.notificationsEnabled ?: true,
                                onCheckedChange = { checked ->
                                    onUpdateSettings(
                                        checked,
                                        settings?.dailyReminderHour ?: 19,
                                        settings?.dailyReminderMinute ?: 0,
                                        settings?.backgroundStepTrackingEnabled ?: true
                                    )
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = DeFittBlack, checkedTrackColor = NeonGreen)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Sensors, contentDescription = null, tint = NeonGreen)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Conteo automático en segundo plano", style = MaterialTheme.typography.titleMedium, color = DeFittWhite)
                                    Text("Mantiene activos los pasos al minimizar", style = MaterialTheme.typography.bodyMedium, color = DeFittTextSecondary)
                                }
                            }
                            Switch(
                                checked = settings?.backgroundStepTrackingEnabled ?: true,
                                onCheckedChange = { checked ->
                                    onUpdateSettings(
                                        settings?.notificationsEnabled ?: true,
                                        settings?.dailyReminderHour ?: 19,
                                        settings?.dailyReminderMinute ?: 0,
                                        checked
                                    )
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = DeFittBlack, checkedTrackColor = NeonGreen)
                            )
                        }
                    }
                }
            }

            // Data Export / Restore & Diagnostics
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = DeFittSurfaceCard,
                    border = BorderStroke(1.dp, DeFittBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("DATOS Y PRIVACIDAD", style = MaterialTheme.typography.labelLarge, color = NeonGreen)

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { exportLauncher.launch("defitt_respaldo_${DateUtils.todayKey()}.json") },
                                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
                                modifier = Modifier.weight(1f).testTag("profile_export_button")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Exportar", color = DeFittWhite)
                            }
                            OutlinedButton(
                                onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                                border = BorderStroke(1.dp, DeFittBorder),
                                modifier = Modifier.weight(1f).testTag("profile_import_button")
                            ) {
                                Icon(Icons.Default.Upload, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Restaurar", color = DeFittWhite)
                            }
                        }

                        OutlinedButton(
                            onClick = { showPermissionsDialog = true },
                            border = BorderStroke(1.dp, DeFittBorder),
                            modifier = Modifier.fillMaxWidth().testTag("profile_permissions_button")
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = NeonGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Consultar permisos y sensores", color = DeFittWhite)
                        }

                        OutlinedButton(
                            onClick = { showAppInfoDialog = true },
                            border = BorderStroke(1.dp, DeFittBorder),
                            modifier = Modifier.fillMaxWidth().testTag("profile_app_info_button")
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = NeonGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Información de DeFitt", color = DeFittWhite)
                        }
                    }
                }
            }

            // Session & Delete Account
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("profile_logout_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = DeFittSurfaceCard, contentColor = DeFittWhite),
                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = NeonGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cerrar sesión y proteger cuenta")
                    }

                    OutlinedButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp).testTag("profile_delete_account_button"),
                        border = BorderStroke(1.dp, DeFittError.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = DeFittError)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eliminar cuenta local", color = DeFittError)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (showEditDialog && profile != null) {
        EditProfileDialog(
            user = user,
            profile = profile,
            onDismiss = { showEditDialog = false },
            onSave = { name, age, weight, goals, exp, act, mins, goalSteps, newPin ->
                onUpdateProfile(name, age, weight, goals, exp, act, mins, goalSteps, newPin)
                showEditDialog = false
            }
        )
    }

    if (showPermissionsDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionsDialog = false },
            containerColor = DeFittSurface,
            title = { Text("Permisos y Sensores", color = DeFittWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Sensor activo: ${sensorStatus.sensorKind.label}", color = DeFittWhite)
                    Text(
                        "• Actividad física: ${if (sensorStatus.hasActivityRecognitionPermission) "Concedido" else "Pendiente"}",
                        color = if (sensorStatus.hasActivityRecognitionPermission) NeonGreen else DeFittError
                    )
                    Text(
                        "• Notificaciones: ${if (sensorStatus.hasNotificationPermission) "Concedido" else "Pendiente"}",
                        color = if (sensorStatus.hasNotificationPermission) NeonGreen else DeFittTextSecondary
                    )
                    Text(
                        "• Batería sin restricción: ${if (sensorStatus.isIgnoringBatteryOptimizations) "Sí" else "Optimizada por Android"}",
                        color = DeFittTextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        runCatching {
                            context.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            )
                        }
                    }
                ) {
                    Text("Ajustes del sistema", color = NeonGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionsDialog = false }) {
                    Text("Cerrar", color = DeFittTextSecondary)
                }
            }
        )
    }

    if (showAppInfoDialog) {
        AlertDialog(
            onDismissRequest = { showAppInfoDialog = false },
            containerColor = DeFittSurface,
            title = { Text("DeFitt v1.0", color = DeFittWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.slogan), color = NeonGreen, fontWeight = FontWeight.Bold)
                    Text(
                        "Aplicación fitness offline para Android. Todos tus datos, sensores, comidas, fotografías y logros se guardan localmente en tu dispositivo con SQLite (Room) sin enviar información a terceros.",
                        color = DeFittTextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAppInfoDialog = false }) {
                    Text("Entendido", color = NeonGreen)
                }
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            containerColor = DeFittSurface,
            title = { Text("¿Eliminar cuenta local?", color = DeFittWhite) },
            text = {
                Text(
                    "Se borrarán permanentemente tu perfil, pasos, comidas, fotografías locales, logros y monedas de este teléfono.",
                    color = DeFittTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeFittError, contentColor = DeFittWhite),
                    modifier = Modifier.testTag("confirm_delete_account_button")
                ) {
                    Text("Eliminar todo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancelar", color = DeFittTextSecondary)
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditProfileDialog(
    user: UserEntity,
    profile: UserProfileEntity,
    onDismiss: () -> Unit,
    onSave: (
        fullName: String,
        age: Int,
        weightKg: Double?,
        goalsDescription: String,
        experienceLevel: String,
        activityLevel: String,
        availableMinutes: Int,
        dailyStepGoal: Int,
        newPin: String?
    ) -> Unit
) {
    var name by remember { mutableStateOf(user.fullName) }
    var ageText by remember { mutableStateOf(profile.age.toString()) }
    var weightText by remember { mutableStateOf(profile.weightKg?.toString() ?: "") }
    var goals by remember { mutableStateOf(profile.goalsDescription) }
    var experience by remember { mutableStateOf(profile.experienceLevel) }
    var activity by remember { mutableStateOf(profile.activityLevel) }
    var availableMins by remember { mutableIntStateOf(profile.availableMinutes) }
    var stepGoalText by remember { mutableStateOf(profile.dailyStepGoal.toString()) }
    var pinText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeFittSurface,
        title = { Text("Editar perfil", color = DeFittWhite) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre completo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input")
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ageText,
                        onValueChange = { if (it.all(Char::isDigit)) ageText = it },
                        label = { Text("Edad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("Peso (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = stepGoalText,
                    onValueChange = { if (it.all(Char::isDigit)) stepGoalText = it },
                    label = { Text("Meta diaria de pasos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = goals,
                    onValueChange = { goals = it },
                    label = { Text("Objetivos personales") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Experiencia", style = MaterialTheme.typography.labelLarge, color = DeFittTextSecondary)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("PRINCIPIANTE", "INTERMEDIO", "AVANZADO").forEach { lvl ->
                        FilterChip(
                            selected = experience == lvl,
                            onClick = { experience = lvl },
                            label = { Text(lvl) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonGreen,
                                selectedLabelColor = DeFittBlack
                            )
                        )
                    }
                }
                Text("Tiempo disponible", style = MaterialTheme.typography.labelLarge, color = DeFittTextSecondary)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(15, 20, 30, 45).forEach { m ->
                        FilterChip(
                            selected = availableMins == m,
                            onClick = { availableMins = m },
                            label = { Text("$m min") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonGreen,
                                selectedLabelColor = DeFittBlack
                            )
                        )
                    }
                }
                OutlinedTextField(
                    value = pinText,
                    onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) pinText = it },
                    label = { Text("Nuevo PIN 4 dígitos (opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val age = ageText.toIntOrNull()?.coerceIn(8, 110) ?: profile.age
                    val goal = stepGoalText.toIntOrNull()?.coerceIn(2000, 15000) ?: profile.dailyStepGoal
                    onSave(
                        name.ifBlank { user.fullName },
                        age,
                        weightText.toDoubleOrNull(),
                        goals.ifBlank { profile.goalsDescription },
                        experience,
                        activity,
                        availableMins,
                        goal,
                        pinText.takeIf { it.length == 4 }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = DeFittBlack),
                modifier = Modifier.testTag("edit_profile_save_button")
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = DeFittTextSecondary)
            }
        }
    )
}
