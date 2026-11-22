package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.data.local.dao.MealWithPhotos
import com.example.ui.theme.DeFittBlack
import com.example.ui.theme.DeFittBorder
import com.example.ui.theme.DeFittError
import com.example.ui.theme.DeFittSurface
import com.example.ui.theme.DeFittSurfaceCard
import com.example.ui.theme.DeFittSurfaceElevated
import com.example.ui.theme.DeFittTextSecondary
import com.example.ui.theme.DeFittWhite
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenContainer
import com.example.util.DateUtils
import kotlinx.coroutines.launch
import java.io.File

private val MEAL_TYPES = listOf("DESAYUNO", "ALMUERZO", "CENA", "MERIENDA")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealsScreen(
    mealsToday: List<MealWithPhotos>,
    selectedDateKey: String,
    onCreateCameraFile: () -> File,
    onCopyPickedUri: suspend (Uri) -> String?,
    onSaveMeal: (
        existingMealId: Long?,
        mealType: String,
        foodName: String,
        notes: String,
        photoPaths: List<String>
    ) -> Unit,
    onDeleteMeal: (mealId: Long, dateKey: String) -> Unit
) {
    var editingMeal by remember { mutableStateOf<MealWithPhotos?>(null) }
    var showFormDialog by remember { mutableStateOf(false) }
    var initialMealType by remember { mutableStateOf("DESAYUNO") }
    var activeFilter by remember { mutableStateOf<String?>(null) }
    var fullScreenPhotoPath by remember { mutableStateOf<String?>(null) }

    val filteredMeals = remember(mealsToday, activeFilter) {
        if (activeFilter == null) mealsToday else mealsToday.filter { it.meal.mealType == activeFilter }
    }

    Scaffold(
        containerColor = DeFittBlack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingMeal = null
                    initialMealType = activeFilter ?: "DESAYUNO"
                    showFormDialog = true
                },
                containerColor = NeonGreen,
                contentColor = DeFittBlack,
                modifier = Modifier.testTag("add_meal_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar comida")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "COMIDA",
                        style = MaterialTheme.typography.headlineLarge,
                        color = DeFittWhite
                    )
                    Text(
                        text = "Registra tus alimentos y guarda fotos localmente sin internet (${DateUtils.formatDisplayDateFromKey(selectedDateKey)}).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeFittTextSecondary
                    )
                }
            }

            // Quick register buttons by meal type
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = activeFilter == null,
                        onClick = { activeFilter = null },
                        label = { Text("Todas (${mealsToday.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonGreen,
                            selectedLabelColor = DeFittBlack,
                            containerColor = DeFittSurfaceCard,
                            labelColor = DeFittTextSecondary
                        )
                    )
                    MEAL_TYPES.forEach { type ->
                        val count = mealsToday.count { it.meal.mealType == type }
                        FilterChip(
                            selected = activeFilter == type,
                            onClick = { activeFilter = if (activeFilter == type) null else type },
                            label = { Text("$type ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonGreen,
                                selectedLabelColor = DeFittBlack,
                                containerColor = DeFittSurfaceCard,
                                labelColor = DeFittTextSecondary
                            ),
                            modifier = Modifier.testTag("meal_filter_$type")
                        )
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(MEAL_TYPES) { type ->
                        OutlinedButton(
                            onClick = {
                                editingMeal = null
                                initialMealType = type
                                showFormDialog = true
                            },
                            border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("quick_add_$type")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(type, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            if (filteredMeals.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DeFittSurfaceCard,
                        border = BorderStroke(1.dp, DeFittBorder),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Sin comidas registradas aún",
                                style = MaterialTheme.typography.titleMedium,
                                color = DeFittWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Toca + o elige Desayuno, Almuerzo, Cena o Merienda para guardar tu alimentación y fotos en tu teléfono.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DeFittTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(filteredMeals, key = { it.meal.id }) { mealWithPhotos ->
                    MealCard(
                        item = mealWithPhotos,
                        onPhotoClick = { path -> fullScreenPhotoPath = path },
                        onEdit = {
                            editingMeal = mealWithPhotos
                            initialMealType = mealWithPhotos.meal.mealType
                            showFormDialog = true
                        },
                        onDelete = {
                            onDeleteMeal(mealWithPhotos.meal.id, mealWithPhotos.meal.dateKey)
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }
    }

    if (showFormDialog) {
        MealFormDialog(
            initialMeal = editingMeal,
            defaultMealType = initialMealType,
            onCreateCameraFile = onCreateCameraFile,
            onCopyPickedUri = onCopyPickedUri,
            onDismiss = { showFormDialog = false },
            onSave = { type, food, notes, photos ->
                onSaveMeal(editingMeal?.meal?.id, type, food, notes, photos)
                showFormDialog = false
            }
        )
    }

    fullScreenPhotoPath?.let { path ->
        Dialog(
            onDismissRequest = { fullScreenPhotoPath = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DeFittBlack)
                    .clickable { fullScreenPhotoPath = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = File(path),
                    contentDescription = "Fotografía guardada de comida",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                )
                IconButton(
                    onClick = { fullScreenPhotoPath = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp)
                        .background(DeFittSurfaceCard, CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar foto", tint = DeFittWhite)
                }
            }
        }
    }
}

@Composable
private fun MealCard(
    item: MealWithPhotos,
    onPhotoClick: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = DeFittSurfaceCard,
        border = BorderStroke(1.dp, DeFittBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("meal_card_${item.meal.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NeonGreenContainer,
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = item.meal.mealType,
                        style = MaterialTheme.typography.labelMedium,
                        color = NeonGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = DateUtils.formatTime(item.meal.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = DeFittTextSecondary
                    )
                    IconButton(onClick = onEdit, modifier = Modifier.testTag("edit_meal_${item.meal.id}")) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar comida", tint = DeFittWhite, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_meal_${item.meal.id}")) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar comida", tint = DeFittError, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.meal.foodName,
                style = MaterialTheme.typography.titleLarge,
                color = DeFittWhite,
                fontWeight = FontWeight.Bold
            )
            if (item.meal.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.meal.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DeFittTextSecondary
                )
            }

            if (item.photos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(item.photos, key = { it.id }) { photo ->
                        AsyncImage(
                            model = File(photo.localFilePath),
                            contentDescription = "Foto de ${item.meal.foodName}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(92.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onPhotoClick(photo.localFilePath) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MealFormDialog(
    initialMeal: MealWithPhotos?,
    defaultMealType: String,
    onCreateCameraFile: () -> File,
    onCopyPickedUri: suspend (Uri) -> String?,
    onDismiss: () -> Unit,
    onSave: (mealType: String, foodName: String, notes: String, photoPaths: List<String>) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var mealType by remember { mutableStateOf(initialMeal?.meal?.mealType ?: defaultMealType) }
    var foodName by remember { mutableStateOf(initialMeal?.meal?.foodName ?: "") }
    var notes by remember { mutableStateOf(initialMeal?.meal?.notes ?: "") }
    val photoPaths = remember {
        mutableStateListOf<String>().apply {
            initialMeal?.photos?.forEach { add(it.localFilePath) }
        }
    }
    var pendingCameraPath by remember { mutableStateOf<String?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val saved = onCopyPickedUri(uri)
                if (saved != null) photoPaths.add(saved)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val path = pendingCameraPath
        if (success && path != null && File(path).exists()) {
            photoPaths.add(path)
        }
        pendingCameraPath = null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeFittSurface,
        title = {
            Text(
                text = if (initialMeal == null) "Registrar comida" else "Editar comida",
                color = DeFittWhite
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MEAL_TYPES.forEach { type ->
                        FilterChip(
                            selected = mealType == type,
                            onClick = { mealType = type },
                            label = { Text(type) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonGreen,
                                selectedLabelColor = DeFittBlack,
                                containerColor = DeFittSurfaceElevated,
                                labelColor = DeFittTextSecondary
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it; errorMsg = null },
                    label = { Text("Nombre del alimento *") },
                    placeholder = { Text("Ej: Avena con fruta y huevo") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = DeFittBorder,
                        focusedTextColor = DeFittWhite,
                        unfocusedTextColor = DeFittWhite
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("meal_food_name_input")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observaciones (opcional)") },
                    placeholder = { Text("Porción, hidratación o cómo te sentiste") },
                    minLines = 2,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = DeFittBorder,
                        focusedTextColor = DeFittWhite,
                        unfocusedTextColor = DeFittWhite
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("meal_notes_input")
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val file = onCreateCameraFile()
                            pendingCameraPath = file.absolutePath
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                            cameraLauncher.launch(uri)
                        },
                        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
                        modifier = Modifier.weight(1f).testTag("meal_camera_button")
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cámara", color = DeFittWhite)
                    }

                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        border = BorderStroke(1.dp, DeFittBorder),
                        modifier = Modifier.weight(1f).testTag("meal_gallery_button")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Galería", color = DeFittWhite)
                    }
                }

                if (photoPaths.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(photoPaths) { path ->
                            Box {
                                AsyncImage(
                                    model = File(path),
                                    contentDescription = "Vista previa de comida",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(68.dp).clip(RoundedCornerShape(10.dp))
                                )
                                IconButton(
                                    onClick = { photoPaths.remove(path) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(22.dp)
                                        .background(DeFittBlack.copy(alpha = 0.75f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Quitar foto", tint = DeFittWhite, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                if (errorMsg != null) {
                    Text(errorMsg!!, color = DeFittError, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (foodName.isBlank()) {
                        errorMsg = "Escribe el nombre del alimento."
                    } else {
                        onSave(mealType, foodName, notes, photoPaths.toList())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = DeFittBlack),
                modifier = Modifier.testTag("meal_save_button")
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
