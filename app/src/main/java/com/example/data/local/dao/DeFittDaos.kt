package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.AchievementEntity
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
import kotlinx.coroutines.flow.Flow

data class UserWithProfile(
    @Embedded val user: UserEntity,
    @Relation(parentColumn = "id", entityColumn = "userId")
    val profile: UserProfileEntity?
)

data class MealWithPhotos(
    @Embedded val meal: MealEntity,
    @Relation(parentColumn = "id", entityColumn = "mealId")
    val photos: List<PhotoEntity>
)

data class AchievementWithStatus(
    @Embedded val achievement: AchievementEntity,
    val isUnlocked: Boolean,
    val unlockedAt: Long?,
    val dateKey: String?
)

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE isActiveSession = 1 LIMIT 1")
    fun observeActiveUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isActiveSession = 1 LIMIT 1")
    suspend fun getActiveUser(): UserEntity?

    @Transaction
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun observeAllUsersWithProfiles(): Flow<List<UserWithProfile>>

    @Transaction
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    suspend fun getAllUsersWithProfiles(): List<UserWithProfile>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isActiveSession = 0")
    suspend fun clearAllActiveSessions()

    @Query("UPDATE users SET isActiveSession = 1 WHERE id = :userId")
    suspend fun activateUserSession(userId: Long)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: Long)

    @Query("SELECT * FROM profiles WHERE userId = :userId LIMIT 1")
    fun observeProfile(userId: Long): Flow<UserProfileEntity?>

    @Query("SELECT * FROM profiles WHERE userId = :userId LIMIT 1")
    suspend fun getProfile(userId: Long): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity): Long

    @Query("SELECT * FROM app_settings WHERE userId = :userId LIMIT 1")
    fun observeSettings(userId: Long): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE userId = :userId LIMIT 1")
    suspend fun getSettings(userId: Long): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: AppSettingsEntity): Long
}

@Dao
interface StepsDao {
    @Query("SELECT * FROM daily_steps WHERE userId = :userId AND dateKey = :dateKey LIMIT 1")
    fun observeDailySteps(userId: Long, dateKey: String): Flow<DailyStepsEntity?>

    @Query("SELECT * FROM daily_steps WHERE userId = :userId AND dateKey = :dateKey LIMIT 1")
    suspend fun getDailySteps(userId: Long, dateKey: String): DailyStepsEntity?

    @Query("SELECT * FROM daily_steps WHERE userId = :userId ORDER BY dateKey DESC")
    fun observeAllDailySteps(userId: Long): Flow<List<DailyStepsEntity>>

    @Query("SELECT * FROM daily_steps WHERE userId = :userId ORDER BY dateKey DESC")
    suspend fun getAllDailySteps(userId: Long): List<DailyStepsEntity>

    @Query("SELECT COALESCE(MAX(steps), 0) FROM daily_steps WHERE userId = :userId AND dateKey < :todayKey")
    suspend fun getBestPreviousDaySteps(userId: Long, todayKey: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailySteps(entity: DailyStepsEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSensorReading(reading: SensorReadingEntity): Long

    @Query("SELECT * FROM sensor_readings WHERE userId = :userId AND dateKey = :dateKey ORDER BY recordedAt DESC LIMIT 50")
    fun observeRecentSensorReadings(userId: Long, dateKey: String): Flow<List<SensorReadingEntity>>

    @Query("SELECT * FROM sensor_readings WHERE userId = :userId ORDER BY recordedAt DESC")
    suspend fun getAllSensorReadings(userId: Long): List<SensorReadingEntity>
}

@Dao
interface MealDao {
    @Transaction
    @Query("SELECT * FROM meals WHERE userId = :userId AND dateKey = :dateKey ORDER BY timestamp DESC")
    fun observeMealsForDate(userId: Long, dateKey: String): Flow<List<MealWithPhotos>>

    @Transaction
    @Query("SELECT * FROM meals WHERE userId = :userId ORDER BY timestamp DESC")
    fun observeAllMeals(userId: Long): Flow<List<MealWithPhotos>>

    @Transaction
    @Query("SELECT * FROM meals WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getAllMealsWithPhotos(userId: Long): List<MealWithPhotos>

    @Query("SELECT COUNT(*) FROM meals WHERE userId = :userId")
    suspend fun getTotalMealsCount(userId: Long): Int

    @Query("SELECT COUNT(*) FROM meals WHERE userId = :userId AND dateKey = :dateKey")
    suspend fun getMealsCountForDate(userId: Long, dateKey: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteMealById(mealId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity): Long

    @Query("SELECT * FROM photos WHERE mealId = :mealId")
    suspend fun getPhotosForMeal(mealId: Long): List<PhotoEntity>

    @Query("SELECT * FROM photos WHERE userId = :userId")
    suspend fun getAllPhotosForUser(userId: Long): List<PhotoEntity>

    @Query("DELETE FROM photos WHERE mealId = :mealId")
    suspend fun deletePhotosForMeal(mealId: Long)
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY isWarmupOrRecovery DESC, category ASC, difficulty ASC")
    fun observeAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises")
    suspend fun getAllExercises(): List<ExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Query(
        """
        UPDATE exercises
        SET name = :name,
            category = :category,
            explanation = :explanation,
            instructions = :instructions,
            difficulty = :difficulty,
            durationSeconds = :durationSeconds,
            recommendedReps = :recommendedReps,
            restSeconds = :restSeconds,
            illustrationKey = :illustrationKey,
            isWarmupOrRecovery = :isWarmupOrRecovery,
            safetyWarning = :safetyWarning,
            videoResName = :videoResName
        WHERE code = :code
        """
    )
    suspend fun updateExerciseByCode(
        code: String,
        name: String,
        category: String,
        explanation: String,
        instructions: String,
        difficulty: String,
        durationSeconds: Int,
        recommendedReps: String,
        restSeconds: Int,
        illustrationKey: String,
        isWarmupOrRecovery: Boolean,
        safetyWarning: String,
        videoResName: String
    )

    @Query("SELECT * FROM routines ORDER BY estimatedMinutes ASC")
    fun observeAllRoutines(): Flow<List<RoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRoutines(routines: List<RoutineEntity>)

    @Query("SELECT * FROM completed_exercises WHERE userId = :userId AND dateKey = :dateKey ORDER BY completedAt DESC")
    fun observeCompletedForDate(userId: Long, dateKey: String): Flow<List<CompletedExerciseEntity>>

    @Query("SELECT * FROM completed_exercises WHERE userId = :userId ORDER BY completedAt DESC")
    fun observeAllCompleted(userId: Long): Flow<List<CompletedExerciseEntity>>

    @Query("SELECT * FROM completed_exercises WHERE userId = :userId ORDER BY completedAt DESC")
    suspend fun getAllCompleted(userId: Long): List<CompletedExerciseEntity>

    @Query("SELECT COUNT(*) FROM completed_exercises WHERE userId = :userId")
    suspend fun getTotalCompletedCount(userId: Long): Int

    @Query("SELECT COUNT(*) FROM completed_exercises WHERE userId = :userId AND dateKey = :dateKey")
    suspend fun getCompletedCountForDate(userId: Long, dateKey: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedExercise(item: CompletedExerciseEntity): Long
}

@Dao
interface RewardsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(items: List<AchievementEntity>)

    @Query("SELECT * FROM achievements ORDER BY id ASC")
    suspend fun getAllAchievements(): List<AchievementEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUserAchievements(items: List<UserAchievementEntity>)

    @Query(
        """
        SELECT a.*,
               COALESCE(ua.isUnlocked, 0) AS isUnlocked,
               ua.unlockedAt AS unlockedAt,
               ua.dateKey AS dateKey
        FROM achievements a
        LEFT JOIN user_achievements ua
          ON a.code = ua.achievementCode AND ua.userId = :userId
        ORDER BY isUnlocked DESC, a.id ASC
        """
    )
    fun observeAchievementsForUser(userId: Long): Flow<List<AchievementWithStatus>>

    @Query("SELECT * FROM user_achievements WHERE userId = :userId")
    suspend fun getUserAchievements(userId: Long): List<UserAchievementEntity>

    @Query("UPDATE user_achievements SET isUnlocked = 1, unlockedAt = :unlockedAt, dateKey = :dateKey WHERE userId = :userId AND achievementCode = :code AND isUnlocked = 0")
    suspend fun unlockUserAchievement(userId: Long, code: String, unlockedAt: Long, dateKey: String): Int

    @Query("SELECT COUNT(*) FROM user_achievements WHERE userId = :userId AND dateKey = :dateKey AND isUnlocked = 1")
    suspend fun getUnlockedCountForDate(userId: Long, dateKey: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCoinTransaction(tx: CoinTransactionEntity): Long

    @Query("SELECT COALESCE(SUM(amount), 0) FROM coins WHERE userId = :userId")
    fun observeCoinBalance(userId: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM coins WHERE userId = :userId AND dateKey = :dateKey")
    fun observeTodayCoins(userId: Long, dateKey: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM coins WHERE userId = :userId AND dateKey = :dateKey")
    suspend fun getCoinsForDate(userId: Long, dateKey: String): Int

    @Query("SELECT COALESCE(SUM(amount), 0) FROM coins WHERE userId = :userId AND dateKey = :dateKey AND sourceType = :sourceType")
    suspend fun getCoinsForDateBySource(userId: Long, dateKey: String, sourceType: String): Int

    @Query("SELECT * FROM coins WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeCoinHistory(userId: Long): Flow<List<CoinTransactionEntity>>

    @Query("SELECT * FROM coins WHERE userId = :userId AND dateKey = :dateKey ORDER BY createdAt DESC")
    fun observeCoinHistoryForDate(userId: Long, dateKey: String): Flow<List<CoinTransactionEntity>>

    @Query("SELECT * FROM coins WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getAllCoinTransactions(userId: Long): List<CoinTransactionEntity>
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM daily_history WHERE userId = :userId ORDER BY dateKey DESC")
    fun observeAllHistory(userId: Long): Flow<List<DailyHistoryEntity>>

    @Query("SELECT * FROM daily_history WHERE userId = :userId AND dateKey = :dateKey LIMIT 1")
    fun observeHistoryForDate(userId: Long, dateKey: String): Flow<DailyHistoryEntity?>

    @Query("SELECT * FROM daily_history WHERE userId = :userId AND dateKey = :dateKey LIMIT 1")
    suspend fun getHistoryForDate(userId: Long, dateKey: String): DailyHistoryEntity?

    @Query("SELECT * FROM daily_history WHERE userId = :userId ORDER BY dateKey DESC")
    suspend fun getAllHistory(userId: Long): List<DailyHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyHistory(history: DailyHistoryEntity): Long
}
