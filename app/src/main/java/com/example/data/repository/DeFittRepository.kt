package com.example.data.repository

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.example.data.local.DeFittDatabase
import com.example.data.local.dao.AchievementWithStatus
import com.example.data.local.dao.MealWithPhotos
import com.example.data.local.dao.UserWithProfile
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.CoinTransactionEntity
import com.example.data.local.entity.CompletedExerciseEntity
import com.example.data.local.entity.DailyHistoryEntity
import com.example.data.local.entity.DailyStepsEntity
import com.example.data.local.entity.ExerciseEntity
import com.example.data.local.entity.MealEntity
import com.example.data.local.entity.PhotoEntity
import com.example.data.local.entity.RoutineEntity
import com.example.data.local.entity.SensorReadingEntity
import com.example.data.local.entity.UserAchievementEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.util.DateUtils
import com.example.util.PinHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class DeFittRepository(
    private val context: Context,
    private val db: DeFittDatabase
) {
    private val userDao = db.userDao()
    private val stepsDao = db.stepsDao()
    private val mealDao = db.mealDao()
    private val exerciseDao = db.exerciseDao()
    private val rewardsDao = db.rewardsDao()
    private val historyDao = db.historyDao()

    private var lastSensorReadingSavedAt = 0L

    suspend fun initializeCatalog() {
        db.ensureSeeded()
    }

    fun observeActiveUser(): Flow<UserEntity?> = userDao.observeActiveUser()
    fun observeAllUsersWithProfiles(): Flow<List<UserWithProfile>> = userDao.observeAllUsersWithProfiles()
    fun observeProfile(userId: Long): Flow<UserProfileEntity?> = userDao.observeProfile(userId)
    fun observeSettings(userId: Long): Flow<AppSettingsEntity?> = userDao.observeSettings(userId)

    fun observeDailySteps(userId: Long, dateKey: String): Flow<DailyStepsEntity?> =
        stepsDao.observeDailySteps(userId, dateKey)

    fun observeAllDailySteps(userId: Long): Flow<List<DailyStepsEntity>> =
        stepsDao.observeAllDailySteps(userId)

    fun observeRecentSensorReadings(userId: Long, dateKey: String): Flow<List<SensorReadingEntity>> =
        stepsDao.observeRecentSensorReadings(userId, dateKey)

    fun observeMealsForDate(userId: Long, dateKey: String): Flow<List<MealWithPhotos>> =
        mealDao.observeMealsForDate(userId, dateKey)

    fun observeAllExercises(): Flow<List<ExerciseEntity>> = exerciseDao.observeAllExercises()
    fun observeAllRoutines(): Flow<List<RoutineEntity>> = exerciseDao.observeAllRoutines()
    fun observeCompletedForDate(userId: Long, dateKey: String): Flow<List<CompletedExerciseEntity>> =
        exerciseDao.observeCompletedForDate(userId, dateKey)

    fun observeAchievements(userId: Long): Flow<List<AchievementWithStatus>> =
        rewardsDao.observeAchievementsForUser(userId)

    fun observeCoinBalance(userId: Long): Flow<Int> = rewardsDao.observeCoinBalance(userId)
    fun observeTodayCoins(userId: Long, dateKey: String): Flow<Int> =
        rewardsDao.observeTodayCoins(userId, dateKey)

    fun observeCoinHistory(userId: Long): Flow<List<CoinTransactionEntity>> =
        rewardsDao.observeCoinHistory(userId)

    fun observeCoinHistoryForDate(userId: Long, dateKey: String): Flow<List<CoinTransactionEntity>> =
        rewardsDao.observeCoinHistoryForDate(userId, dateKey)

    fun observeAllHistory(userId: Long): Flow<List<DailyHistoryEntity>> =
        historyDao.observeAllHistory(userId)

    fun observeHistoryForDate(userId: Long, dateKey: String): Flow<DailyHistoryEntity?> =
        historyDao.observeHistoryForDate(userId, dateKey)

    suspend fun getActiveUser(): UserEntity? = userDao.getActiveUser()
    suspend fun getSettings(userId: Long): AppSettingsEntity? = userDao.getSettings(userId)
    suspend fun getProfile(userId: Long): UserProfileEntity? = userDao.getProfile(userId)

    suspend fun registerAccount(
        fullName: String,
        age: Int,
        weightKg: Double?,
        goalsDescription: String,
        experienceLevel: String,
        activityLevel: String,
        availableMinutes: Int,
        pin: String?,
        todayKey: String = DateUtils.todayKey()
    ): Long = withContext(Dispatchers.IO) {
        db.ensureSeeded()
        val cleanName = fullName.trim()
        val safeGoals = goalsDescription.trim().ifEmpty {
            "Mejorar mi resistencia, movilidad y hábitos saludables diarios."
        }
        val recommendedGoal = when {
            age < 18 -> 6000
            activityLevel == "ALTO" -> 8500
            activityLevel == "MODERADO" -> 6500
            else -> 5000
        }
        val pinHash = pin?.trim()?.takeIf { it.isNotEmpty() }?.let { PinHasher.hashPin(it) }

        db.withTransaction {
            userDao.clearAllActiveSessions()
            val userId = userDao.insertUser(
                UserEntity(
                    fullName = cleanName,
                    pinHash = pinHash,
                    isActiveSession = true
                )
            )
            userDao.insertOrUpdateProfile(
                UserProfileEntity(
                    userId = userId,
                    age = age,
                    weightKg = weightKg,
                    goalsDescription = safeGoals,
                    experienceLevel = experienceLevel,
                    activityLevel = activityLevel,
                    availableMinutes = availableMinutes,
                    dailyStepGoal = recommendedGoal
                )
            )
            userDao.insertOrUpdateSettings(
                AppSettingsEntity(
                    userId = userId,
                    lastActiveDateKey = todayKey
                )
            )
            stepsDao.upsertDailySteps(
                DailyStepsEntity(
                    userId = userId,
                    dateKey = todayKey,
                    stepGoal = recommendedGoal
                )
            )
            historyDao.upsertDailyHistory(
                DailyHistoryEntity(
                    userId = userId,
                    dateKey = todayKey,
                    summaryNote = "Inicio de actividad en DeFitt"
                )
            )
            seedUserAchievements(userId)
            userId
        }
    }

    private suspend fun seedUserAchievements(userId: Long) {
        val achievements = rewardsDao.getAllAchievements()
        val items = achievements.map {
            UserAchievementEntity(userId = userId, achievementCode = it.code)
        }
        rewardsDao.insertUserAchievements(items)
    }

    suspend fun loginLocalAccount(userId: Long, pinInput: String?): Boolean =
        withContext(Dispatchers.IO) {
            val user = userDao.getUserById(userId) ?: return@withContext false
            if (!PinHasher.verifyPin(pinInput.orEmpty(), user.pinHash)) {
                return@withContext false
            }
            db.withTransaction {
                userDao.clearAllActiveSessions()
                userDao.activateUserSession(userId)
            }
            ensureCurrentDay(userId)
            true
        }

    suspend fun logoutActiveSession() = withContext(Dispatchers.IO) {
        userDao.clearAllActiveSessions()
    }

    suspend fun updateProfile(
        userId: Long,
        fullName: String,
        age: Int,
        weightKg: Double?,
        goalsDescription: String,
        experienceLevel: String,
        activityLevel: String,
        availableMinutes: Int,
        dailyStepGoal: Int,
        newPin: String?
    ) = withContext(Dispatchers.IO) {
        val today = DateUtils.todayKey()
        db.withTransaction {
            val existingUser = userDao.getUserById(userId) ?: return@withTransaction
            val existingProfile = userDao.getProfile(userId)
            val updatedPinHash = when {
                newPin == null -> existingUser.pinHash
                newPin.isBlank() -> null
                else -> PinHasher.hashPin(newPin.trim())
            }
            userDao.updateUser(
                existingUser.copy(
                    fullName = fullName.trim(),
                    pinHash = updatedPinHash
                )
            )
            val clampedGoal = dailyStepGoal.coerceIn(2000, 15000)
            userDao.insertOrUpdateProfile(
                UserProfileEntity(
                    id = existingProfile?.id ?: 0,
                    userId = userId,
                    age = age,
                    weightKg = weightKg,
                    goalsDescription = goalsDescription.trim(),
                    experienceLevel = experienceLevel,
                    activityLevel = activityLevel,
                    availableMinutes = availableMinutes,
                    dailyStepGoal = clampedGoal,
                    updatedAt = System.currentTimeMillis()
                )
            )
            val todaySteps = stepsDao.getDailySteps(userId, today)
            if (todaySteps != null) {
                stepsDao.upsertDailySteps(todaySteps.copy(stepGoal = clampedGoal))
            }
        }
    }

    suspend fun updateSettings(
        userId: Long,
        notificationsEnabled: Boolean,
        reminderHour: Int,
        reminderMinute: Int,
        backgroundTrackingEnabled: Boolean
    ) = withContext(Dispatchers.IO) {
        val current = userDao.getSettings(userId) ?: return@withContext
        userDao.insertOrUpdateSettings(
            current.copy(
                notificationsEnabled = notificationsEnabled,
                dailyReminderHour = reminderHour,
                dailyReminderMinute = reminderMinute,
                backgroundStepTrackingEnabled = backgroundTrackingEnabled
            )
        )
    }

    suspend fun deleteLocalAccount(userId: Long) = withContext(Dispatchers.IO) {
        val photos = mealDao.getAllPhotosForUser(userId)
        photos.forEach { photo ->
            runCatching { File(photo.localFilePath).delete() }
        }
        userDao.deleteUserById(userId)
    }

    /**
     * Automatic Day Rollover:
     * Detects date change even if app was closed across midnight or for multiple days.
     * Saves previous days' snapshots into daily_history and initializes missing dates with 0 steps
     * (never inventing fake activity).
     */
    suspend fun ensureCurrentDay(
        userId: Long,
        targetDateKey: String = DateUtils.todayKey()
    ) = withContext(Dispatchers.IO) {
        db.ensureSeeded()
        db.withTransaction {
            val settings = userDao.getSettings(userId) ?: AppSettingsEntity(
                userId = userId,
                lastActiveDateKey = targetDateKey
            )
            val profile = userDao.getProfile(userId)
            val stepGoal = profile?.dailyStepGoal ?: 6000
            val lastActive = settings.lastActiveDateKey

            if (lastActive < targetDateKey) {
                // Finalize snapshot of lastActive day
                syncDailyHistorySnapshotInternal(userId, lastActive)

                // Fill any intermediate days + targetDateKey cleanly with 0 steps
                val daysToCreate = DateUtils.daysBetweenInclusiveExclusive(lastActive, targetDateKey)
                for (dayKey in daysToCreate) {
                    if (stepsDao.getDailySteps(userId, dayKey) == null) {
                        stepsDao.upsertDailySteps(
                            DailyStepsEntity(
                                userId = userId,
                                dateKey = dayKey,
                                steps = 0,
                                distanceMeters = 0.0,
                                activeSeconds = 0L,
                                stepGoal = stepGoal
                            )
                        )
                    }
                    if (historyDao.getHistoryForDate(userId, dayKey) == null) {
                        historyDao.upsertDailyHistory(
                            DailyHistoryEntity(
                                userId = userId,
                                dateKey = dayKey,
                                summaryNote = if (dayKey == targetDateKey) "Día actual" else "Sin actividad registrada"
                            )
                        )
                    }
                }
                userDao.insertOrUpdateSettings(settings.copy(lastActiveDateKey = targetDateKey))
            } else {
                // Ensure today's rows exist
                if (stepsDao.getDailySteps(userId, targetDateKey) == null) {
                    stepsDao.upsertDailySteps(
                        DailyStepsEntity(
                            userId = userId,
                            dateKey = targetDateKey,
                            stepGoal = stepGoal
                        )
                    )
                }
                if (historyDao.getHistoryForDate(userId, targetDateKey) == null) {
                    historyDao.upsertDailyHistory(
                        DailyHistoryEntity(
                            userId = userId,
                            dateKey = targetDateKey
                        )
                    )
                }
                if (userDao.getSettings(userId) == null) {
                    userDao.insertOrUpdateSettings(settings)
                }
            }
            seedUserAchievements(userId)
        }
    }

    /**
     * Processes a real hardware sensor event from TYPE_STEP_COUNTER, TYPE_STEP_DETECTOR,
     * or ACCELEROMETER_FALLBACK. Never fabricates steps.
     */
    suspend fun recordStepSensorEvent(
        userId: Long,
        sensorType: String,
        rawValue: Float,
        isCumulativeCounter: Boolean,
        dateKey: String = DateUtils.todayKey()
    ) = withContext(Dispatchers.IO) {
        ensureCurrentDay(userId, dateKey)
        val settings = userDao.getSettings(userId)
        val stride = settings?.strideLengthMeters ?: 0.72
        val profile = userDao.getProfile(userId)
        val goal = profile?.dailyStepGoal ?: 6000

        db.withTransaction {
            val current = stepsDao.getDailySteps(userId, dateKey) ?: DailyStepsEntity(
                userId = userId,
                dateKey = dateKey,
                stepGoal = goal
            )

            val deltaSteps: Int
            val updatedBaseline: Float?
            val updatedLastRaw: Float?

            if (isCumulativeCounter) {
                val prevRaw = current.lastSensorRawValue
                when {
                    prevRaw == null -> {
                        // First reading of this day: establish baseline without claiming prior boot steps
                        deltaSteps = 0
                        updatedBaseline = rawValue
                        updatedLastRaw = rawValue
                    }
                    rawValue >= prevRaw -> {
                        deltaSteps = (rawValue - prevRaw).toInt().coerceAtLeast(0)
                        updatedBaseline = current.sensorBaselineValue ?: rawValue
                        updatedLastRaw = rawValue
                    }
                    else -> {
                        // Device rebooted since last reading (cumulative counter reset to 0+)
                        deltaSteps = rawValue.toInt().coerceAtLeast(0)
                        updatedBaseline = 0f
                        updatedLastRaw = rawValue
                    }
                }
            } else {
                // Step detector / accelerometer fallback emits 1 step per trigger
                deltaSteps = rawValue.toInt().coerceAtLeast(0)
                updatedBaseline = current.sensorBaselineValue
                updatedLastRaw = current.lastSensorRawValue
            }

            val newSteps = current.steps + deltaSteps
            val newDistance = newSteps * stride
            val addedActiveSeconds = if (deltaSteps > 0) (deltaSteps * 0.55).toLong().coerceAtLeast(1L) else 0L
            val newActiveSeconds = current.activeSeconds + addedActiveSeconds

            stepsDao.upsertDailySteps(
                current.copy(
                    steps = newSteps,
                    distanceMeters = newDistance,
                    activeSeconds = newActiveSeconds,
                    sensorBaselineValue = updatedBaseline,
                    lastSensorRawValue = updatedLastRaw,
                    stepGoal = goal,
                    updatedAt = System.currentTimeMillis()
                )
            )

            val now = System.currentTimeMillis()
            if (deltaSteps > 0 && (now - lastSensorReadingSavedAt > 4000L || !isCumulativeCounter)) {
                lastSensorReadingSavedAt = now
                stepsDao.insertSensorReading(
                    SensorReadingEntity(
                        userId = userId,
                        dateKey = dateKey,
                        sensorType = sensorType,
                        rawValue = rawValue,
                        deltaSteps = deltaSteps,
                        recordedAt = now
                    )
                )
            }

            // Award walk milestone coins (5 coins per 1,000 steps, capped at 30 coins/day to avoid excessive activity incentive)
            if (newSteps >= 1000) {
                val milestone = (newSteps / 1000).coerceAtMost(6)
                for (m in 1..milestone) {
                    val dedup = "walk_${dateKey}_$m"
                    rewardsDao.insertCoinTransaction(
                        CoinTransactionEntity(
                            userId = userId,
                            dateKey = dateKey,
                            amount = 5,
                            reason = "Caminata saludable (${m * 1000} pasos)",
                            sourceType = "WALK",
                            dedupKey = dedup
                        )
                    )
                }
            }

            evaluateAchievementsInternal(userId, dateKey)
            syncDailyHistorySnapshotInternal(userId, dateKey)
        }
    }

    suspend fun saveMeal(
        userId: Long,
        existingMealId: Long?,
        mealType: String,
        foodName: String,
        notes: String,
        photoPaths: List<String>,
        dateKey: String = DateUtils.todayKey()
    ) = withContext(Dispatchers.IO) {
        ensureCurrentDay(userId, dateKey)
        val now = System.currentTimeMillis()
        db.withTransaction {
            val mealId = if (existingMealId != null) {
                mealDao.insertMeal(
                    MealEntity(
                        id = existingMealId,
                        userId = userId,
                        dateKey = dateKey,
                        mealType = mealType,
                        foodName = foodName.trim(),
                        notes = notes.trim(),
                        timestamp = now
                    )
                )
                existingMealId
            } else {
                mealDao.insertMeal(
                    MealEntity(
                        userId = userId,
                        dateKey = dateKey,
                        mealType = mealType,
                        foodName = foodName.trim(),
                        notes = notes.trim(),
                        timestamp = now
                    )
                )
            }

            // Replace photo links if edited
            val previousPhotos = mealDao.getPhotosForMeal(mealId)
            val keptPaths = photoPaths.toSet()
            previousPhotos.filter { it.localFilePath !in keptPaths }.forEach { removed ->
                runCatching { File(removed.localFilePath).delete() }
            }
            mealDao.deletePhotosForMeal(mealId)
            photoPaths.distinct().forEach { path ->
                mealDao.insertPhoto(
                    PhotoEntity(
                        userId = userId,
                        mealId = mealId,
                        localFilePath = path
                    )
                )
            }

            // Reward up to 10 coins/day for logging balanced meals
            val mealCoinsToday = rewardsDao.getCoinsForDateBySource(userId, dateKey, "MEAL_LOG")
            if (mealCoinsToday < 10) {
                rewardsDao.insertCoinTransaction(
                    CoinTransactionEntity(
                        userId = userId,
                        dateKey = dateKey,
                        amount = 5,
                        reason = "Registro de comida ($mealType)",
                        sourceType = "MEAL_LOG",
                        dedupKey = "meal_${dateKey}_${mealId}"
                    )
                )
            }

            evaluateAchievementsInternal(userId, dateKey)
            syncDailyHistorySnapshotInternal(userId, dateKey)
        }
    }

    suspend fun deleteMeal(userId: Long, mealId: Long, dateKey: String) =
        withContext(Dispatchers.IO) {
            val photos = mealDao.getPhotosForMeal(mealId)
            photos.forEach { photo ->
                runCatching { File(photo.localFilePath).delete() }
            }
            db.withTransaction {
                mealDao.deleteMealById(mealId)
                syncDailyHistorySnapshotInternal(userId, dateKey)
            }
        }

    suspend fun copyUriToPrivateMealStorage(uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val dir = File(context.filesDir, "meal_photos").apply { mkdirs() }
            val outFile = File(dir, "meal_${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return@runCatching null
            outFile.absolutePath
        }.getOrNull()
    }

    fun createCameraOutputFile(): File {
        val dir = File(context.filesDir, "meal_photos").apply { mkdirs() }
        return File(dir, "cam_${System.currentTimeMillis()}.jpg")
    }

    suspend fun completeExercise(
        userId: Long,
        exercise: ExerciseEntity,
        elapsedSeconds: Int,
        dateKey: String = DateUtils.todayKey()
    ): Int = withContext(Dispatchers.IO) {
        ensureCurrentDay(userId, dateKey)
        db.withTransaction {
            val existingExerciseCoinsToday =
                rewardsDao.getCoinsForDateBySource(userId, dateKey, "EXERCISE")
            // Healthy cap: up to 30 coins/day from exercises
            val coinsEarned = if (existingExerciseCoinsToday < 30) {
                minOf(10, 30 - existingExerciseCoinsToday)
            } else {
                0
            }

            val completedId = exerciseDao.insertCompletedExercise(
                CompletedExerciseEntity(
                    userId = userId,
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    category = exercise.category,
                    dateKey = dateKey,
                    durationSeconds = elapsedSeconds.coerceAtLeast(15),
                    coinsEarned = coinsEarned
                )
            )

            if (coinsEarned > 0) {
                rewardsDao.insertCoinTransaction(
                    CoinTransactionEntity(
                        userId = userId,
                        dateKey = dateKey,
                        amount = coinsEarned,
                        reason = "Ejercicio completado: ${exercise.name}",
                        sourceType = "EXERCISE",
                        dedupKey = "exercise_${dateKey}_$completedId"
                    )
                )
            }

            // Also credit active workout seconds to daily steps/activity summary
            val currentSteps = stepsDao.getDailySteps(userId, dateKey)
            if (currentSteps != null) {
                stepsDao.upsertDailySteps(
                    currentSteps.copy(
                        activeSeconds = currentSteps.activeSeconds + elapsedSeconds.coerceAtLeast(15),
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }

            evaluateAchievementsInternal(userId, dateKey)
            syncDailyHistorySnapshotInternal(userId, dateKey)
            coinsEarned
        }
    }

    private suspend fun evaluateAchievementsInternal(userId: Long, dateKey: String) {
        val now = System.currentTimeMillis()
        val todaySteps = stepsDao.getDailySteps(userId, dateKey)
        val stepsCount = todaySteps?.steps ?: 0
        val stepGoal = todaySteps?.stepGoal ?: 6000
        val totalMeals = mealDao.getTotalMealsCount(userId)
        val totalExercises = exerciseDao.getTotalCompletedCount(userId)
        val previousBestSteps = stepsDao.getBestPreviousDaySteps(userId, dateKey)
        val allHistory = historyDao.getAllHistory(userId)
        val activeDays = allHistory.count {
            it.steps > 0 || it.exercisesCount > 0 || it.mealsCount > 0
        }.coerceAtLeast(if (stepsCount > 0 || totalExercises > 0 || totalMeals > 0) 1 else 0)

        val achievements = rewardsDao.getAllAchievements()
        for (ach in achievements) {
            val met = when (ach.requirementType) {
                "STEPS_DAY" -> stepsCount >= ach.requirementTarget
                "STEP_GOAL" -> stepsCount >= stepGoal
                "MEALS_TOTAL" -> totalMeals >= ach.requirementTarget
                "EXERCISES_TOTAL" -> totalExercises >= ach.requirementTarget
                "BEAT_RECORD" -> previousBestSteps > 0 && stepsCount > previousBestSteps
                "ACTIVE_DAYS" -> activeDays >= ach.requirementTarget
                else -> false
            }
            if (met) {
                val changed = rewardsDao.unlockUserAchievement(userId, ach.code, now, dateKey)
                if (changed > 0 && ach.coinsReward > 0) {
                    rewardsDao.insertCoinTransaction(
                        CoinTransactionEntity(
                            userId = userId,
                            dateKey = dateKey,
                            amount = ach.coinsReward,
                            reason = "Logro desbloqueado: ${ach.name}",
                            sourceType = "ACHIEVEMENT",
                            dedupKey = "ach_${ach.code}"
                        )
                    )
                }
            }
        }
    }

    private suspend fun syncDailyHistorySnapshotInternal(userId: Long, dateKey: String) {
        val steps = stepsDao.getDailySteps(userId, dateKey)
        val mealsCount = mealDao.getMealsCountForDate(userId, dateKey)
        val exercisesCount = exerciseDao.getCompletedCountForDate(userId, dateKey)
        val achievementsCount = rewardsDao.getUnlockedCountForDate(userId, dateKey)
        val coinsEarned = rewardsDao.getCoinsForDate(userId, dateKey)
        val existing = historyDao.getHistoryForDate(userId, dateKey)

        historyDao.upsertDailyHistory(
            DailyHistoryEntity(
                id = existing?.id ?: 0,
                userId = userId,
                dateKey = dateKey,
                steps = steps?.steps ?: 0,
                distanceMeters = steps?.distanceMeters ?: 0.0,
                activeSeconds = steps?.activeSeconds ?: 0L,
                mealsCount = mealsCount,
                exercisesCount = exercisesCount,
                achievementsUnlockedCount = achievementsCount,
                coinsEarned = coinsEarned,
                summaryNote = existing?.summaryNote ?: "",
                createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    fun personalizeExercises(
        exercises: List<ExerciseEntity>,
        profile: UserProfileEntity?
    ): List<ExerciseEntity> {
        if (profile == null) return exercises.filter { it.difficulty == "PRINCIPIANTE" }.take(6)

        val goalsLower = profile.goalsDescription.lowercase()
        val allowedDifficulties = when {
            profile.age < 15 -> setOf("PRINCIPIANTE")
            profile.experienceLevel == "PRINCIPIANTE" -> setOf("PRINCIPIANTE")
            profile.experienceLevel == "INTERMEDIO" -> setOf("PRINCIPIANTE", "INTERMEDIO")
            else -> setOf("PRINCIPIANTE", "INTERMEDIO", "AVANZADO")
        }

        val preferredCategories = mutableSetOf<String>()
        if ("abdom" in goalsLower || "core" in goalsLower) preferredCategories += "ABDOMEN"
        if ("resist" in goalsLower || "cardio" in goalsLower || "caminar" in goalsLower) {
            preferredCategories += "RESISTENCIA"
        }
        if ("fuerz" in goalsLower || "pecho" in goalsLower) preferredCategories += "PECHO"
        if ("braz" in goalsLower) preferredCategories += "BRAZOS"
        if ("piern" in goalsLower) preferredCategories += "PIERNAS"
        if ("espald" in goalsLower || "postur" in goalsLower) preferredCategories += "ESPALDA"
        if ("movil" in goalsLower || "flex" in goalsLower) preferredCategories += "MOVILIDAD"
        if (preferredCategories.isEmpty()) {
            preferredCategories += listOf("CUERPO_COMPLETO", "RESISTENCIA", "MOVILIDAD")
        }

        val maxItems = when {
            profile.availableMinutes <= 15 -> 4
            profile.availableMinutes <= 30 -> 6
            else -> 8
        }

        return exercises
            .filter { it.difficulty in allowedDifficulties }
            .sortedByDescending { ex ->
                var score = 0
                if (ex.isWarmupOrRecovery) score += 2
                if (ex.category in preferredCategories) score += 5
                if (ex.difficulty == profile.experienceLevel) score += 2
                score
            }
            .take(maxItems)
    }

    suspend fun exportUserDataToJson(userId: Long): String = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId) ?: error("Usuario no encontrado")
        val profile = userDao.getProfile(userId)
        val settings = userDao.getSettings(userId)
        val stepsList = stepsDao.getAllDailySteps(userId)
        val mealsList = mealDao.getAllMealsWithPhotos(userId)
        val completedExercises = exerciseDao.getAllCompleted(userId)
        val userAchievements = rewardsDao.getUserAchievements(userId)
        val coins = rewardsDao.getAllCoinTransactions(userId)
        val history = historyDao.getAllHistory(userId)

        JSONObject().apply {
            put("app", "DeFitt")
            put("version", 1)
            put("exportedAt", System.currentTimeMillis())
            put("user", JSONObject().apply {
                put("fullName", user.fullName)
                put("createdAt", user.createdAt)
            })
            if (profile != null) {
                put("profile", JSONObject().apply {
                    put("age", profile.age)
                    put("weightKg", profile.weightKg ?: JSONObject.NULL)
                    put("goalsDescription", profile.goalsDescription)
                    put("experienceLevel", profile.experienceLevel)
                    put("activityLevel", profile.activityLevel)
                    put("availableMinutes", profile.availableMinutes)
                    put("dailyStepGoal", profile.dailyStepGoal)
                })
            }
            if (settings != null) {
                put("settings", JSONObject().apply {
                    put("notificationsEnabled", settings.notificationsEnabled)
                    put("dailyReminderHour", settings.dailyReminderHour)
                    put("dailyReminderMinute", settings.dailyReminderMinute)
                    put("backgroundStepTrackingEnabled", settings.backgroundStepTrackingEnabled)
                    put("lastActiveDateKey", settings.lastActiveDateKey)
                })
            }
            put("dailySteps", JSONArray().apply {
                stepsList.forEach {
                    put(JSONObject().apply {
                        put("dateKey", it.dateKey)
                        put("steps", it.steps)
                        put("distanceMeters", it.distanceMeters)
                        put("activeSeconds", it.activeSeconds)
                        put("stepGoal", it.stepGoal)
                    })
                }
            })
            put("meals", JSONArray().apply {
                mealsList.forEach { m ->
                    put(JSONObject().apply {
                        put("dateKey", m.meal.dateKey)
                        put("mealType", m.meal.mealType)
                        put("foodName", m.meal.foodName)
                        put("notes", m.meal.notes)
                        put("timestamp", m.meal.timestamp)
                        put("photoPaths", JSONArray(m.photos.map { it.localFilePath }))
                    })
                }
            })
            put("completedExercises", JSONArray().apply {
                completedExercises.forEach { ce ->
                    put(JSONObject().apply {
                        put("exerciseName", ce.exerciseName)
                        put("category", ce.category)
                        put("dateKey", ce.dateKey)
                        put("durationSeconds", ce.durationSeconds)
                        put("coinsEarned", ce.coinsEarned)
                        put("completedAt", ce.completedAt)
                    })
                }
            })
            put("userAchievements", JSONArray().apply {
                userAchievements.filter { it.isUnlocked }.forEach { ua ->
                    put(JSONObject().apply {
                        put("achievementCode", ua.achievementCode)
                        put("unlockedAt", ua.unlockedAt ?: 0L)
                        put("dateKey", ua.dateKey ?: "")
                    })
                }
            })
            put("coins", JSONArray().apply {
                coins.forEach { c ->
                    put(JSONObject().apply {
                        put("dateKey", c.dateKey)
                        put("amount", c.amount)
                        put("reason", c.reason)
                        put("sourceType", c.sourceType)
                        put("dedupKey", c.dedupKey ?: JSONObject.NULL)
                        put("createdAt", c.createdAt)
                    })
                }
            })
            put("dailyHistory", JSONArray().apply {
                history.forEach { h ->
                    put(JSONObject().apply {
                        put("dateKey", h.dateKey)
                        put("steps", h.steps)
                        put("distanceMeters", h.distanceMeters)
                        put("activeSeconds", h.activeSeconds)
                        put("mealsCount", h.mealsCount)
                        put("exercisesCount", h.exercisesCount)
                        put("achievementsUnlockedCount", h.achievementsUnlockedCount)
                        put("coinsEarned", h.coinsEarned)
                    })
                }
            })
        }.toString(2)
    }

    suspend fun importUserDataFromJson(jsonText: String): Long = withContext(Dispatchers.IO) {
        db.ensureSeeded()
        val root = JSONObject(jsonText)
        require(root.optString("app") == "DeFitt") { "Archivo no compatible con DeFitt" }
        val userObj = root.getJSONObject("user")
        val profileObj = root.optJSONObject("profile")
        val settingsObj = root.optJSONObject("settings")
        val today = DateUtils.todayKey()

        val allExercises = exerciseDao.getAllExercises().associateBy { it.name }
        val fallbackExId = allExercises.values.firstOrNull()?.id ?: 1L

        db.withTransaction {
            userDao.clearAllActiveSessions()
            val userId = userDao.insertUser(
                UserEntity(
                    fullName = userObj.getString("fullName"),
                    isActiveSession = true,
                    createdAt = userObj.optLong("createdAt", System.currentTimeMillis())
                )
            )

            val stepGoal = profileObj?.optInt("dailyStepGoal", 6000) ?: 6000
            userDao.insertOrUpdateProfile(
                UserProfileEntity(
                    userId = userId,
                    age = profileObj?.optInt("age", 25) ?: 25,
                    weightKg = if (profileObj != null && !profileObj.isNull("weightKg")) {
                        profileObj.optDouble("weightKg")
                    } else null,
                    goalsDescription = profileObj?.optString("goalsDescription", "") ?: "",
                    experienceLevel = profileObj?.optString("experienceLevel", "PRINCIPIANTE") ?: "PRINCIPIANTE",
                    activityLevel = profileObj?.optString("activityLevel", "MODERADO") ?: "MODERADO",
                    availableMinutes = profileObj?.optInt("availableMinutes", 20) ?: 20,
                    dailyStepGoal = stepGoal
                )
            )
            userDao.insertOrUpdateSettings(
                AppSettingsEntity(
                    userId = userId,
                    notificationsEnabled = settingsObj?.optBoolean("notificationsEnabled", true) ?: true,
                    dailyReminderHour = settingsObj?.optInt("dailyReminderHour", 19) ?: 19,
                    dailyReminderMinute = settingsObj?.optInt("dailyReminderMinute", 0) ?: 0,
                    backgroundStepTrackingEnabled = settingsObj?.optBoolean("backgroundStepTrackingEnabled", true) ?: true,
                    lastActiveDateKey = settingsObj?.optString("lastActiveDateKey", today) ?: today
                )
            )
            seedUserAchievements(userId)

            val stepsArr = root.optJSONArray("dailySteps")
            if (stepsArr != null) {
                for (i in 0 until stepsArr.length()) {
                    val o = stepsArr.getJSONObject(i)
                    stepsDao.upsertDailySteps(
                        DailyStepsEntity(
                            userId = userId,
                            dateKey = o.getString("dateKey"),
                            steps = o.optInt("steps", 0),
                            distanceMeters = o.optDouble("distanceMeters", 0.0),
                            activeSeconds = o.optLong("activeSeconds", 0L),
                            stepGoal = o.optInt("stepGoal", stepGoal)
                        )
                    )
                }
            }

            val mealsArr = root.optJSONArray("meals")
            if (mealsArr != null) {
                for (i in 0 until mealsArr.length()) {
                    val o = mealsArr.getJSONObject(i)
                    val mealId = mealDao.insertMeal(
                        MealEntity(
                            userId = userId,
                            dateKey = o.getString("dateKey"),
                            mealType = o.getString("mealType"),
                            foodName = o.getString("foodName"),
                            notes = o.optString("notes", ""),
                            timestamp = o.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                    val paths = o.optJSONArray("photoPaths")
                    if (paths != null) {
                        for (p in 0 until paths.length()) {
                            val path = paths.getString(p)
                            if (File(path).exists()) {
                                mealDao.insertPhoto(PhotoEntity(userId = userId, mealId = mealId, localFilePath = path))
                            }
                        }
                    }
                }
            }

            val exArr = root.optJSONArray("completedExercises")
            if (exArr != null) {
                for (i in 0 until exArr.length()) {
                    val o = exArr.getJSONObject(i)
                    val name = o.getString("exerciseName")
                    exerciseDao.insertCompletedExercise(
                        CompletedExerciseEntity(
                            userId = userId,
                            exerciseId = allExercises[name]?.id ?: fallbackExId,
                            exerciseName = name,
                            category = o.optString("category", "CUERPO_COMPLETO"),
                            dateKey = o.getString("dateKey"),
                            durationSeconds = o.optInt("durationSeconds", 60),
                            coinsEarned = o.optInt("coinsEarned", 0),
                            completedAt = o.optLong("completedAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            val achArr = root.optJSONArray("userAchievements")
            if (achArr != null) {
                for (i in 0 until achArr.length()) {
                    val o = achArr.getJSONObject(i)
                    rewardsDao.unlockUserAchievement(
                        userId = userId,
                        code = o.getString("achievementCode"),
                        unlockedAt = o.optLong("unlockedAt", System.currentTimeMillis()),
                        dateKey = o.optString("dateKey", today)
                    )
                }
            }

            val coinsArr = root.optJSONArray("coins")
            if (coinsArr != null) {
                for (i in 0 until coinsArr.length()) {
                    val o = coinsArr.getJSONObject(i)
                    rewardsDao.insertCoinTransaction(
                        CoinTransactionEntity(
                            userId = userId,
                            dateKey = o.getString("dateKey"),
                            amount = o.getInt("amount"),
                            reason = o.getString("reason"),
                            sourceType = o.getString("sourceType"),
                            dedupKey = if (o.isNull("dedupKey")) null else o.optString("dedupKey"),
                            createdAt = o.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            val histArr = root.optJSONArray("dailyHistory")
            if (histArr != null) {
                for (i in 0 until histArr.length()) {
                    val o = histArr.getJSONObject(i)
                    historyDao.upsertDailyHistory(
                        DailyHistoryEntity(
                            userId = userId,
                            dateKey = o.getString("dateKey"),
                            steps = o.optInt("steps", 0),
                            distanceMeters = o.optDouble("distanceMeters", 0.0),
                            activeSeconds = o.optLong("activeSeconds", 0L),
                            mealsCount = o.optInt("mealsCount", 0),
                            exercisesCount = o.optInt("exercisesCount", 0),
                            achievementsUnlockedCount = o.optInt("achievementsUnlockedCount", 0),
                            coinsEarned = o.optInt("coinsEarned", 0)
                        )
                    )
                }
            }
            userId
        }
    }
}
