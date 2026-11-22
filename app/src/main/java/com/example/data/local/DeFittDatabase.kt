package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ExerciseDao
import com.example.data.local.dao.HistoryDao
import com.example.data.local.dao.MealDao
import com.example.data.local.dao.RewardsDao
import com.example.data.local.dao.StepsDao
import com.example.data.local.dao.UserDao
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

@Database(
    entities = [
        UserEntity::class,
        UserProfileEntity::class,
        DailyStepsEntity::class,
        SensorReadingEntity::class,
        MealEntity::class,
        PhotoEntity::class,
        ExerciseEntity::class,
        RoutineEntity::class,
        CompletedExerciseEntity::class,
        AchievementEntity::class,
        UserAchievementEntity::class,
        CoinTransactionEntity::class,
        DailyHistoryEntity::class,
        AppSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class DeFittDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun stepsDao(): StepsDao
    abstract fun mealDao(): MealDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun rewardsDao(): RewardsDao
    abstract fun historyDao(): HistoryDao

    suspend fun ensureSeeded() {
        val exDao = exerciseDao()
        exDao.insertExercises(CatalogSeedData.defaultExercises)
        CatalogSeedData.defaultExercises.forEach { ex ->
            exDao.updateExerciseByCode(
                code = ex.code,
                name = ex.name,
                category = ex.category,
                explanation = ex.explanation,
                instructions = ex.instructions,
                difficulty = ex.difficulty,
                durationSeconds = ex.durationSeconds,
                recommendedReps = ex.recommendedReps,
                restSeconds = ex.restSeconds,
                illustrationKey = ex.illustrationKey,
                isWarmupOrRecovery = ex.isWarmupOrRecovery,
                safetyWarning = ex.safetyWarning,
                videoResName = ex.videoResName
            )
        }
        exDao.insertRoutines(CatalogSeedData.defaultRoutines)
        rewardsDao().insertAchievements(CatalogSeedData.defaultAchievements)
    }

    companion object {
        @Volatile
        private var INSTANCE: DeFittDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE daily_steps ADD COLUMN lastSensorTimestampMs INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE daily_steps ADD COLUMN lastBootTimestampMs INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE exercises ADD COLUMN safetyWarning TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE exercises ADD COLUMN videoResName TEXT NOT NULL DEFAULT ''")

                // Sanitize any corrupted rows caused by v1 sensor-hub 0 -> cumulative boot dump
                // (e.g., baseline=0, steps > 1000 matching cumulative boot total, bogus hours).
                db.execSQL(
                    """
                    UPDATE daily_steps
                    SET steps = 0,
                        distanceMeters = 0.0,
                        activeSeconds = 0,
                        sensorBaselineValue = lastSensorRawValue
                    WHERE sensorBaselineValue IS NOT NULL
                      AND sensorBaselineValue = 0.0
                      AND lastSensorRawValue IS NOT NULL
                      AND steps > 1000
                      AND ABS(steps - CAST(lastSensorRawValue AS INTEGER)) <= 5
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    UPDATE daily_history
                    SET steps = 0,
                        distanceMeters = 0.0,
                        activeSeconds = 0
                    WHERE EXISTS (
                        SELECT 1 FROM daily_steps ds
                        WHERE ds.userId = daily_history.userId
                          AND ds.dateKey = daily_history.dateKey
                          AND ds.steps = 0
                          AND daily_history.steps > 1000
                          AND ds.sensorBaselineValue IS NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): DeFittDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DeFittDatabase::class.java,
                    "defitt_offline.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
