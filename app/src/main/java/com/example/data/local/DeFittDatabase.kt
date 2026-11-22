package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
    version = 1,
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
        exerciseDao().insertExercises(CatalogSeedData.defaultExercises)
        exerciseDao().insertRoutines(CatalogSeedData.defaultRoutines)
        rewardsDao().insertAchievements(CatalogSeedData.defaultAchievements)
    }

    companion object {
        @Volatile
        private var INSTANCE: DeFittDatabase? = null

        fun getInstance(context: Context): DeFittDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    DeFittDatabase::class.java,
                    "defitt_offline.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
