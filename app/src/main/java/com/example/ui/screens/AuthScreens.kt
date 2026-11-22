package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.WifiOff
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.dao.UserWithProfile
import com.example.ui.components.DeFittOfficialLogo
import com.example.ui.theme.DeFittBlack
import com.example.ui.theme.DeFittBorder
import com.example.ui.theme.DeFittError
import com.example.ui.theme.DeFittSurface
import com.example.ui.theme.DeFittSurfaceCard
import com.example.ui.theme.DeFittTextSecondary
import com.example.ui.theme.DeFittWhite
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenContainer
import com.example.util.DateUtils

@Composable
fun DeFittSplashScreen() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeFittBlack, NeonGreenContainer.copy(alpha = 0.35f), DeFittBlack)
                )
            )
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = visible, enter = fadeIn()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                DeFittOfficialLogo(size = 132.dp)
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "DeFitt",
                    style = MaterialTheme.typography.displayMedium,
                    color = DeFittWhite
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.slogan),
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonGreen,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun WelcomeScreen(
    hasExistingAccounts: Boolean,
    onCreateAccountClick: () -> Unit,
    onExistingAccountClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DeFittBlack, NeonGreenContainer.copy(alpha = 0.28f), DeFittBlack)
                )
            )
            .systemBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DeFittOfficialLogo(size = 148.dp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.welcome_title),
                style = MaterialTheme.typography.headlineLarge,
                color = DeFittWhite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.slogan),
                style = MaterialTheme.typography.labelLarge,
                color = NeonGreen,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DeFittSurfaceCard,
                border = BorderStroke(1.dp, DeFittBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = "Funcionamiento sin internet",
                        tint = NeonGreen
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "100% sin conexión. Tus pasos, comidas, fotografías y progreso viven de forma privada en tu teléfono.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeFittTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onCreateAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("welcome_create_account_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen,
                    contentColor = DeFittBlack
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.create_account),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            OutlinedButton(
                onClick = onExistingAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("welcome_existing_account_button"),
                border = BorderStroke(1.5.dp, NeonGreen.copy(alpha = 0.7f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DeFittWhite),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (hasExistingAccounts) {
                        stringResource(R.string.already_have_account)
                    } else {
                        "${stringResource(R.string.already_have_account)} / RESTAURAR"
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onSubmit: (
        fullName: String,
        age: Int,
        weightKg: Double?,
        goalsDescription: String,
        experienceLevel: String,
        activityLevel: String,
        availableMinutes: Int,
        pin: String?
    ) -> Unit
) {
    BackHandler(onBack = onBack)

    var fullName by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var goalsDescription by remember { mutableStateOf("") }
    var experienceLevel by remember { mutableStateOf("PRINCIPIANTE") }
    var activityLevel by remember { mutableStateOf("MODERADO") }
    var availableMinutes by remember { mutableIntStateOf(20) }
    var pinText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val parsedAge = ageText.toIntOrNull()
    val isMinor = parsedAge != null && parsedAge in 1..17

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonGreen,
        unfocusedBorderColor = DeFittBorder,
        focusedLabelColor = NeonGreen,
        unfocusedLabelColor = DeFittTextSecondary,
        focusedTextColor = DeFittWhite,
        unfocusedTextColor = DeFittWhite,
        cursorColor = NeonGreen
    )

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
            IconButton(onClick = onBack, modifier = Modifier.testTag("register_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = DeFittWhite
                )
            }
            Text(
                text = "Crear cuenta en DeFitt",
                style = MaterialTheme.typography.titleLarge,
                color = DeFittWhite
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .widthIn(max = 600.dp)
                .align(Alignment.CenterHorizontally)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; errorMsg = null },
                label = { Text("Nombre completo *") },
                singleLine = true,
                colors = fieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_name_input")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = ageText,
                    onValueChange = {
                        if (it.length <= 3 && it.all(Char::isDigit)) {
                            ageText = it
                            errorMsg = null
                        }
                    },
                    label = { Text("Edad *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("register_age_input")
                )
                OutlinedTextField(
                    value = weightText,
                    onValueChange = {
                        if (it.length <= 6 && it.all { ch -> ch.isDigit() || ch == '.' }) {
                            weightText = it
                        }
                    },
                    label = { Text("Peso actual (kg, opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("register_weight_input")
                )
            }

            if (isMinor) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NeonGreenContainer,
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Enfoque saludable DeFitt: tus objetivos se centrarán en hábitos diarios, movilidad, fuerza y resistencia.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeFittWhite,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            OutlinedTextField(
                value = goalsDescription,
                onValueChange = { goalsDescription = it; errorMsg = null },
                label = { Text("Descripción de objetivos personales *") },
                placeholder = {
                    Text(
                        "Ej: Quiero mejorar mi resistencia, fortalecer mi cuerpo, trabajar mi abdomen y mejorar mi condición física",
                        color = DeFittTextSecondary.copy(alpha = 0.65f)
                    )
                },
                minLines = 3,
                maxLines = 4,
                colors = fieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_goals_input")
            )

            Text(
                text = "Nivel de experiencia",
                style = MaterialTheme.typography.titleMedium,
                color = DeFittWhite
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("PRINCIPIANTE", "INTERMEDIO", "AVANZADO").forEach { lvl ->
                    FilterChip(
                        selected = experienceLevel == lvl,
                        onClick = { experienceLevel = lvl },
                        label = { Text(lvl.lowercase().replaceFirstChar(Char::uppercase)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonGreen,
                            selectedLabelColor = DeFittBlack,
                            containerColor = DeFittSurfaceCard,
                            labelColor = DeFittTextSecondary
                        )
                    )
                }
            }

            Text(
                text = "Nivel de actividad habitual",
                style = MaterialTheme.typography.titleMedium,
                color = DeFittWhite
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("BAJO", "MODERADO", "ALTO").forEach { act ->
                    FilterChip(
                        selected = activityLevel == act,
                        onClick = { activityLevel = act },
                        label = { Text(act.lowercase().replaceFirstChar(Char::uppercase)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonGreen,
                            selectedLabelColor = DeFittBlack,
                            containerColor = DeFittSurfaceCard,
                            labelColor = DeFittTextSecondary
                        )
                    )
                }
            }

            Text(
                text = "Tiempo disponible diario",
                style = MaterialTheme.typography.titleMedium,
                color = DeFittWhite
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15, 20, 30, 45).forEach { mins ->
                    FilterChip(
                        selected = availableMinutes == mins,
                        onClick = { availableMinutes = mins },
                        label = { Text("$mins min") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonGreen,
                            selectedLabelColor = DeFittBlack,
                            containerColor = DeFittSurfaceCard,
                            labelColor = DeFittTextSecondary
                        )
                    )
                }
            }

            OutlinedTextField(
                value = pinText,
                onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) pinText = it },
                label = { Text("PIN de protección local (4 dígitos, opcional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                colors = fieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("register_pin_input")
            )

            if (errorMsg != null) {
                Text(
                    text = errorMsg!!,
                    color = DeFittError,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    val age = ageText.toIntOrNull()
                    val weight = weightText.toDoubleOrNull()
                    when {
                        fullName.isBlank() -> errorMsg = "Ingresa tu nombre completo."
                        age == null || age !in 8..110 -> errorMsg = "Ingresa una edad válida (8 a 110 años)."
                        goalsDescription.isBlank() -> errorMsg = "Describe tus objetivos personales de bienestar."
                        pinText.isNotEmpty() && pinText.length < 4 -> errorMsg = "El PIN debe tener 4 dígitos o dejarse vacío."
                        else -> {
                            onSubmit(
                                fullName,
                                age,
                                weight,
                                goalsDescription,
                                experienceLevel,
                                activityLevel,
                                availableMinutes,
                                pinText.ifBlank { null }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("register_submit_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen,
                    contentColor = DeFittBlack
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.create_my_account),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ExistingAccountScreen(
    accounts: List<UserWithProfile>,
    onBack: () -> Unit,
    onLogin: (userId: Long, pin: String?, onResult: (Boolean) -> Unit) -> Unit,
    onRestoreBackupUri: (android.net.Uri) -> Unit
) {
    BackHandler(onBack = onBack)

    var selectedForPin by remember { mutableStateOf<UserWithProfile?>(null) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onRestoreBackupUri(uri)
        }
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
            IconButton(onClick = onBack, modifier = Modifier.testTag("existing_account_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = DeFittWhite
                )
            }
            Text(
                text = "Entrar con cuenta local",
                style = MaterialTheme.typography.titleLarge,
                color = DeFittWhite
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .widthIn(max = 600.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (accounts.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DeFittSurfaceCard,
                        border = BorderStroke(1.dp, DeFittBorder),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Aún no hay cuentas guardadas en este dispositivo.",
                                style = MaterialTheme.typography.titleMedium,
                                color = DeFittWhite,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Crea una cuenta nueva o restaura un archivo de respaldo local de DeFitt.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DeFittTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(accounts, key = { it.user.id }) { item ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = DeFittSurfaceCard,
                        border = BorderStroke(1.dp, DeFittBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (item.user.pinHash.isNullOrBlank()) {
                                    onLogin(item.user.id, null) {}
                                } else {
                                    pinInput = ""
                                    pinError = false
                                    selectedForPin = item
                                }
                            }
                            .testTag("local_account_card_${item.user.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NeonGreenContainer,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (item.user.pinHash != null) Icons.Default.Lock else Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = NeonGreen
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.user.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = DeFittWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${item.profile?.age ?: "-"} años · Registro: ${DateUtils.formatDisplayDate(item.user.createdAt)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DeFittTextSecondary
                                )
                            }
                            Text(
                                text = "ENTRAR",
                                style = MaterialTheme.typography.labelLarge,
                                color = NeonGreen
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { restoreLauncher.launch(arrayOf("application/json", "*/*")) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("restore_backup_login_button"),
                    border = BorderStroke(1.dp, DeFittBorder),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Restaurar respaldo local (.json)", color = DeFittWhite)
                }
            }
        }
    }

    selectedForPin?.let { target ->
        AlertDialog(
            onDismissRequest = { selectedForPin = null },
            containerColor = DeFittSurface,
            title = {
                Text("PIN de ${target.user.fullName}", color = DeFittWhite)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Ingresa tu PIN de 4 dígitos para proteger tu cuenta.",
                        color = DeFittTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all(Char::isDigit)) {
                                pinInput = it
                                pinError = false
                            }
                        },
                        label = { Text("PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("login_pin_input")
                    )
                    if (pinError) {
                        Text("PIN incorrecto", color = DeFittError, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onLogin(target.user.id, pinInput) { success ->
                            if (success) {
                                selectedForPin = null
                            } else {
                                pinError = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = DeFittBlack),
                    modifier = Modifier.testTag("login_pin_confirm_button")
                ) {
                    Text("Entrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedForPin = null }) {
                    Text("Cancelar", color = DeFittTextSecondary)
                }
            }
        )
    }
}
