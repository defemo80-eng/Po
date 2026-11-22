package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.DeFittApplication
import com.example.data.local.dao.AchievementWithStatus
import com.example.data.local.dao.MealWithPhotos
import com.example.data.local.dao.UserWithProfile
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.CoinTransactionEntity
import com.example.data.local.entity.CompletedExerciseEntity
import com.example.data.local.entity.DailyHistoryEntity
import com.example.data.local.entity.DailyStepsEntity
import com.example.data.local.entity.ExerciseEntity
import com.example.data.local.entity.RoutineEntity
import com.example.data.local.entity.SensorReadingEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.notifications.DeFittNotificationHelper
import com.example.sensors.ActiveStepSensorKind
import com.example.sensors.DeviceSensorStatus
import com.example.sensors.SensorCapabilityInspector
import com.example.sensors.StepSensorManager
import com.example.sensors.StepTrackingService
import com.example.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

sealed interface AuthUiState {
    data object Loading : AuthUiState
    data class LoggedOut(val localAccounts: List<UserWithProfile>) : AuthUiState
    data class Authenticated(
        val user: UserEntity,
        val profile: UserProfileEntity?,
        val settings: AppSettingsEntity?
    ) : AuthUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class DeFittViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = (application as DeFittApplication).repository

    private val _todayKey = MutableStateFlow(DateUtils.todayKey())
    val todayKey: StateFlow<String> = _todayKey.asStateFlow()

    private val _selectedRecordDateKey = MutableStateFlow(DateUtils.todayKey())
    val selectedRecordDateKey: StateFlow<String> = _selectedRecordDateKey.asStateFlow()

    private val _sensorStatus = MutableStateFlow(SensorCapabilityInspector.inspect(application))
    val sensorStatus: StateFlow<DeviceSensorStatus> = _sensorStatus.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    private var foregroundSensorManager: StepSensorManager? = null

    val authState: StateFlow<AuthUiState> = repo.observeActiveUser()
        .flatMapLatest { activeUser ->
            if (activeUser == null) {
                repo.observeAllUsersWithProfiles().flatMapLatest { accounts ->
                    flowOf<AuthUiState>(AuthUiState.LoggedOut(accounts))
                }
            } else {
                combine(
                    repo.observeProfile(activeUser.id),
                    repo.observeSettings(activeUser.id)
                ) { profile, settings ->
                    AuthUiState.Authenticated(activeUser, profile, settings)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthUiState.Loading)

    private val activeUserIdFlow = repo.observeActiveUser()

    val todaySteps: StateFlow<DailyStepsEntity?> = combine(activeUserIdFlow, _todayKey) { user, day ->
        user?.id to day
    }.flatMapLatest { (uid, day) ->
        if (uid == null) flowOf(null) else repo.observeDailySteps(uid, day)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayMeals: StateFlow<List<MealWithPhotos>> = combine(activeUserIdFlow, _todayKey) { user, day ->
        user?.id to day
    }.flatMapLatest { (uid, day) ->
        if (uid == null) flowOf(emptyList()) else repo.observeMealsForDate(uid, day)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExercises: StateFlow<List<ExerciseEntity>> = repo.observeAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRoutines: StateFlow<List<RoutineEntity>> = repo.observeAllRoutines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val personalizedExercises: StateFlow<List<ExerciseEntity>> = combine(
        allExercises,
        authState
    ) { exercises, auth ->
        val profile = (auth as? AuthUiState.Authenticated)?.profile
        repo.personalizeExercises(exercises, profile)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCompletedExercises: StateFlow<List<CompletedExerciseEntity>> = combine(
        activeUserIdFlow,
        _todayKey
    ) { user, day -> user?.id to day }
        .flatMapLatest { (uid, day) ->
            if (uid == null) flowOf(emptyList()) else repo.observeCompletedForDate(uid, day)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<AchievementWithStatus>> = activeUserIdFlow
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repo.observeAchievements(user.id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coinBalance: StateFlow<Int> = activeUserIdFlow
        .flatMapLatest { user ->
            if (user == null) flowOf(0) else repo.observeCoinBalance(user.id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayCoins: StateFlow<Int> = combine(activeUserIdFlow, _todayKey) { user, day ->
        user?.id to day
    }.flatMapLatest { (uid, day) ->
        if (uid == null) flowOf(0) else repo.observeTodayCoins(uid, day)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val coinHistory: StateFlow<List<CoinTransactionEntity>> = activeUserIdFlow
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repo.observeCoinHistory(user.id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHistory: StateFlow<List<DailyHistoryEntity>> = activeUserIdFlow
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repo.observeAllHistory(user.id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDailySteps: StateFlow<List<DailyStepsEntity>> = activeUserIdFlow
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repo.observeAllDailySteps(user.id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSensorReadings: StateFlow<List<SensorReadingEntity>> = combine(
        activeUserIdFlow,
        _todayKey
    ) { user, day -> user?.id to day }
        .flatMapLatest { (uid, day) ->
            if (uid == null) flowOf(emptyList()) else repo.observeRecentSensorReadings(uid, day)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedDateHistory: StateFlow<DailyHistoryEntity?> = combine(
        activeUserIdFlow,
        _selectedRecordDateKey
    ) { user, day -> user?.id to day }
        .flatMapLatest { (uid, day) ->
            if (uid == null) flowOf(null) else repo.observeHistoryForDate(uid, day)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedDateMeals: StateFlow<List<MealWithPhotos>> = combine(
        activeUserIdFlow,
        _selectedRecordDateKey
    ) { user, day -> user?.id to day }
        .flatMapLatest { (uid, day) ->
            if (uid == null) flowOf(emptyList()) else repo.observeMealsForDate(uid, day)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedDateExercises: StateFlow<List<CompletedExerciseEntity>> = combine(
        activeUserIdFlow,
        _selectedRecordDateKey
    ) { user, day -> user?.id to day }
        .flatMapLatest { (uid, day) ->
            if (uid == null) flowOf(emptyList()) else repo.observeCompletedForDate(uid, day)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedDateCoins: StateFlow<List<CoinTransactionEntity>> = combine(
        activeUserIdFlow,
        _selectedRecordDateKey
    ) { user, day -> user?.id to day }
        .flatMapLatest { (uid, day) ->
            if (uid == null) flowOf(emptyList()) else repo.observeCoinHistoryForDate(uid, day)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshDayAndSensors()
    }

    fun refreshDayAndSensors() {
        val currentKey = DateUtils.todayKey()
        if (_todayKey.value != currentKey) {
            _todayKey.value = currentKey
            _selectedRecordDateKey.value = currentKey
        }
        val app = getApplication<DeFittApplication>()
        _sensorStatus.value = SensorCapabilityInspector.inspect(app)
        viewModelScope.launch {
            val user = repo.getActiveUser()
            if (user != null) {
                repo.ensureCurrentDay(user.id, currentKey)
                val settings = repo.getSettings(user.id)
                if (settings?.backgroundStepTrackingEnabled != false) {
                    StepTrackingService.startIfPermitted(app)
                }
                startForegroundStepListener()
            }
        }
    }

    fun startForegroundStepListener() {
        val app = getApplication<DeFittApplication>()
        _sensorStatus.value = SensorCapabilityInspector.inspect(app)
        if (!SensorCapabilityInspector.hasActivityRecognitionPermission(app)) return
        if (foregroundSensorManager == null) {
            foregroundSensorManager = StepSensorManager(app) { kind, rawValue, isCumulative ->
                viewModelScope.launch {
                    val user = repo.getActiveUser() ?: return@launch
                    val settings = repo.getSettings(user.id)
                    // Avoid double-counting when StepTrackingService is running unless service isn't active;
                    // TYPE_STEP_COUNTER uses rawValue comparison so duplicate events produce 0 delta!
                    if (isCumulative || settings?.backgroundStepTrackingEnabled != true) {
                        repo.recordStepSensorEvent(
                            userId = user.id,
                            sensorType = kind.code,
                            rawValue = rawValue,
                            isCumulativeCounter = isCumulative,
                            dateKey = DateUtils.todayKey()
                        )
                    }
                }
            }
        }
        val startedKind = foregroundSensorManager?.startListening() ?: ActiveStepSensorKind.NONE
        if (startedKind != ActiveStepSensorKind.NONE) {
            StepTrackingService.startIfPermitted(app)
        }
    }

    fun selectRecordDate(dateKey: String) {
        _selectedRecordDateKey.value = dateKey
    }

    fun registerAccount(
        fullName: String,
        age: Int,
        weightKg: Double?,
        goalsDescription: String,
        experienceLevel: String,
        activityLevel: String,
        availableMinutes: Int,
        pin: String?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val uid = repo.registerAccount(
                fullName = fullName,
                age = age,
                weightKg = weightKg,
                goalsDescription = goalsDescription,
                experienceLevel = experienceLevel,
                activityLevel = activityLevel,
                availableMinutes = availableMinutes,
                pin = pin
            )
            val app = getApplication<DeFittApplication>()
            DeFittNotificationHelper.scheduleMidnightRollover(app)
            DeFittNotificationHelper.scheduleDailyReminder(app, true, 19, 0)
            refreshDayAndSensors()
            _snackbarMessage.tryEmit("Cuenta creada localmente en DeFitt")
            onDone()
        }
    }

    fun loginExistingAccount(userId: Long, pinInput: String?, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = repo.loginLocalAccount(userId, pinInput)
            if (ok) {
                refreshDayAndSensors()
                _snackbarMessage.tryEmit("Sesión iniciada sin conexión")
            }
            onResult(ok)
        }
    }

    fun logout() {
        viewModelScope.launch {
            foregroundSensorManager?.stopListening()
            StepTrackingService.stop(getApplication())
            repo.logoutActiveSession()
        }
    }

    fun updateProfile(
        fullName: String,
        age: Int,
        weightKg: Double?,
        goalsDescription: String,
        experienceLevel: String,
        activityLevel: String,
        availableMinutes: Int,
        dailyStepGoal: Int,
        newPin: String?
    ) {
        val current = authState.value as? AuthUiState.Authenticated ?: return
        viewModelScope.launch {
            repo.updateProfile(
                userId = current.user.id,
                fullName = fullName,
                age = age,
                weightKg = weightKg,
                goalsDescription = goalsDescription,
                experienceLevel = experienceLevel,
                activityLevel = activityLevel,
                availableMinutes = availableMinutes,
                dailyStepGoal = dailyStepGoal,
                newPin = newPin
            )
            _snackbarMessage.tryEmit("Perfil actualizado")
        }
    }

    fun updateSettings(
        notificationsEnabled: Boolean,
        reminderHour: Int,
        reminderMinute: Int,
        backgroundTrackingEnabled: Boolean
    ) {
        val current = authState.value as? AuthUiState.Authenticated ?: return
        viewModelScope.launch {
            repo.updateSettings(
                userId = current.user.id,
                notificationsEnabled = notificationsEnabled,
                reminderHour = reminderHour,
                reminderMinute = reminderMinute,
                backgroundTrackingEnabled = backgroundTrackingEnabled
            )
            val app = getApplication<DeFittApplication>()
            DeFittNotificationHelper.scheduleDailyReminder(
                app,
                notificationsEnabled,
                reminderHour,
                reminderMinute
            )
            if (backgroundTrackingEnabled) {
                StepTrackingService.startIfPermitted(app)
            } else {
                StepTrackingService.stop(app)
            }
            _snackbarMessage.tryEmit("Preferencias guardadas")
        }
    }

    fun deleteLocalAccount() {
        val current = authState.value as? AuthUiState.Authenticated ?: return
        viewModelScope.launch {
            foregroundSensorManager?.stopListening()
            StepTrackingService.stop(getApplication())
            repo.deleteLocalAccount(current.user.id)
            _snackbarMessage.tryEmit("Cuenta y datos locales eliminados")
        }
    }

    fun saveMeal(
        existingMealId: Long?,
        mealType: String,
        foodName: String,
        notes: String,
        photoPaths: List<String>
    ) {
        val current = authState.value as? AuthUiState.Authenticated ?: return
        viewModelScope.launch {
            repo.saveMeal(
                userId = current.user.id,
                existingMealId = existingMealId,
                mealType = mealType,
                foodName = foodName,
                notes = notes,
                photoPaths = photoPaths,
                dateKey = DateUtils.todayKey()
            )
            _snackbarMessage.tryEmit(
                if (existingMealId == null) "Comida guardada localmente" else "Comida actualizada"
            )
        }
    }

    fun deleteMeal(mealId: Long, dateKey: String) {
        val current = authState.value as? AuthUiState.Authenticated ?: return
        viewModelScope.launch {
            repo.deleteMeal(current.user.id, mealId, dateKey)
            _snackbarMessage.tryEmit("Comida eliminada")
        }
    }

    suspend fun copyPickedPhotoToInternal(uri: Uri): String? =
        repo.copyUriToPrivateMealStorage(uri)

    fun createCameraOutputFile(): File = repo.createCameraOutputFile()

    fun completeExercise(exercise: ExerciseEntity, elapsedSeconds: Int) {
        val current = authState.value as? AuthUiState.Authenticated ?: return
        viewModelScope.launch {
            val coins = repo.completeExercise(
                userId = current.user.id,
                exercise = exercise,
                elapsedSeconds = elapsedSeconds,
                dateKey = DateUtils.todayKey()
            )
            val msg = if (coins > 0) {
                "¡${exercise.name} completado! +$coins DeFitt Coins"
            } else {
                "¡${exercise.name} completado y guardado en tu historial!"
            }
            _snackbarMessage.tryEmit(msg)
        }
    }

    fun exportBackupToUri(uri: Uri) {
        val current = authState.value as? AuthUiState.Authenticated ?: return
        viewModelScope.launch {
            runCatching {
                val json = repo.exportUserDataToJson(current.user.id)
                getApplication<DeFittApplication>().contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(json.toByteArray(Charsets.UTF_8))
                } ?: error("No se pudo abrir el archivo de destino")
            }.onSuccess {
                _snackbarMessage.tryEmit("Respaldo local exportado correctamente")
            }.onFailure {
                _snackbarMessage.tryEmit("Error al exportar: ${it.message}")
            }
        }
    }

    fun importBackupFromUri(uri: Uri) {
        viewModelScope.launch {
            runCatching {
                val json = getApplication<DeFittApplication>().contentResolver
                    .openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("No se pudo leer el archivo seleccionado")
                repo.importUserDataFromJson(json)
            }.onSuccess {
                refreshDayAndSensors()
                _snackbarMessage.tryEmit("Datos restaurados correctamente")
            }.onFailure {
                _snackbarMessage.tryEmit("No se pudo restaurar el archivo: ${it.message}")
            }
        }
    }

    override fun onCleared() {
        foregroundSensorManager?.stopListening()
        super.onCleared()
    }
}
