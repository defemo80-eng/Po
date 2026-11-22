package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val pinHash: String? = null,
    val isActiveSession: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "profiles",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"], unique = true)]
)
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val age: Int,
    val weightKg: Double? = null,
    val goalsDescription: String,
    val experienceLevel: String = "PRINCIPIANTE",
    val activityLevel: String = "MODERADO",
    val availableMinutes: Int = 20,
    val dailyStepGoal: Int = 6000,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "daily_steps",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId", "dateKey"], unique = true)]
)
data class DailyStepsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val dateKey: String,
    val steps: Int = 0,
    val distanceMeters: Double = 0.0,
    val activeSeconds: Long = 0L,
    val sensorBaselineValue: Float? = null,
    val lastSensorRawValue: Float? = null,
    val lastSensorTimestampMs: Long = 0L,
    val lastBootTimestampMs: Long = 0L,
    val stepGoal: Int = 6000,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "sensor_readings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId", "dateKey"])]
)
data class SensorReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val dateKey: String,
    val sensorType: String,
    val rawValue: Float,
    val deltaSteps: Int,
    val recordedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "meals",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId", "dateKey"])]
)
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val dateKey: String,
    val mealType: String,
    val foodName: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"]), Index(value = ["mealId"])]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val mealId: Long? = null,
    val localFilePath: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "exercises",
    indices = [Index(value = ["code"], unique = true)]
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val category: String,
    val explanation: String,
    val instructions: String,
    val difficulty: String,
    val durationSeconds: Int,
    val recommendedReps: String,
    val restSeconds: Int,
    val illustrationKey: String,
    val isWarmupOrRecovery: Boolean = false,
    val safetyWarning: String = "",
    val videoResName: String = ""
)

@Entity(
    tableName = "routines",
    indices = [Index(value = ["code"], unique = true)]
)
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val description: String,
    val difficulty: String,
    val estimatedMinutes: Int,
    val focusGoal: String,
    val exerciseCodesCsv: String
)

@Entity(
    tableName = "completed_exercises",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId", "dateKey"]), Index(value = ["exerciseId"])]
)
data class CompletedExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val category: String,
    val dateKey: String,
    val durationSeconds: Int,
    val coinsEarned: Int,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "achievements",
    indices = [Index(value = ["code"], unique = true)]
)
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val description: String,
    val badgeTier: String,
    val coinsReward: Int,
    val requirementType: String,
    val requirementTarget: Int
)

@Entity(
    tableName = "user_achievements",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AchievementEntity::class,
            parentColumns = ["code"],
            childColumns = ["achievementCode"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId", "achievementCode"], unique = true),
        Index(value = ["achievementCode"])
    ]
)
data class UserAchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val achievementCode: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val dateKey: String? = null
)

@Entity(
    tableName = "coins",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId", "dateKey"]),
        Index(value = ["userId", "dedupKey"], unique = true)
    ]
)
data class CoinTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val dateKey: String,
    val amount: Int,
    val reason: String,
    val sourceType: String,
    val dedupKey: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "daily_history",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId", "dateKey"], unique = true)]
)
data class DailyHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val dateKey: String,
    val steps: Int = 0,
    val distanceMeters: Double = 0.0,
    val activeSeconds: Long = 0L,
    val mealsCount: Int = 0,
    val exercisesCount: Int = 0,
    val achievementsUnlockedCount: Int = 0,
    val coinsEarned: Int = 0,
    val summaryNote: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "app_settings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"], unique = true)]
)
data class AppSettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val notificationsEnabled: Boolean = true,
    val dailyReminderHour: Int = 19,
    val dailyReminderMinute: Int = 0,
    val backgroundStepTrackingEnabled: Boolean = true,
    val lastActiveDateKey: String,
    val strideLengthMeters: Double = 0.72
)
