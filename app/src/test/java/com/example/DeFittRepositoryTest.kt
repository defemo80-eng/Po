package com.example

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CatalogSeedData
import com.example.data.local.DeFittDatabase
import com.example.data.repository.DeFittRepository
import com.example.ui.components.ExerciseVideoMapper
import com.example.util.DateUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DeFittRepositoryTest {

    private lateinit var context: Context
    private lateinit var db: DeFittDatabase
    private lateinit var repo: DeFittRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, DeFittDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = DeFittRepository(context, db)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun registrationLoginWithPinLogoutAndProfileEditPersistLocally() = runTest {
        val userId = repo.registerAccount(
            fullName = "Lucía Torres",
            age = 16,
            weightKg = null,
            goalsDescription = "Mejorar mi resistencia y movilidad",
            experienceLevel = "PRINCIPIANTE",
            activityLevel = "MODERADO",
            availableMinutes = 20,
            pin = "1234",
            todayKey = "2026-11-20"
        )

        val activeUser = repo.getActiveUser()
        assertNotNull(activeUser)
        assertEquals("Lucía Torres", activeUser?.fullName)

        // Logout protects account
        repo.logoutActiveSession()
        assertEquals(null, repo.getActiveUser())

        // Wrong PIN rejected
        assertFalse(repo.loginLocalAccount(userId, "9999"))
        assertEquals(null, repo.getActiveUser())

        // Correct PIN succeeds
        assertTrue(repo.loginLocalAccount(userId, "1234"))
        assertEquals(userId, repo.getActiveUser()?.id)

        // Edit profile
        repo.updateProfile(
            userId = userId,
            fullName = "Lucía M. Torres",
            age = 17,
            weightKg = null,
            goalsDescription = "Fuerza y resistencia diaria",
            experienceLevel = "INTERMEDIO",
            activityLevel = "ALTO",
            availableMinutes = 30,
            dailyStepGoal = 7500,
            newPin = null
        )
        val updatedProfile = repo.getProfile(userId)
        assertEquals("Lucía M. Torres", repo.getActiveUser()?.fullName)
        assertEquals(7500, updatedProfile?.dailyStepGoal)
        assertEquals("INTERMEDIO", updatedProfile?.experienceLevel)
    }

    @Test
    fun stepCounterHandlesBaselineDeltasAndDeviceRebootRecovery() = runTest {
        val day = "2026-11-20"
        val userId = repo.registerAccount(
            fullName = "Mateo Silva",
            age = 28,
            weightKg = 74.0,
            goalsDescription = "Caminar más cada día",
            experienceLevel = "PRINCIPIANTE",
            activityLevel = "MODERADO",
            availableMinutes = 20,
            pin = null,
            todayKey = day
        )

        // First cumulative hardware reading establishes baseline (0 delta)
        repo.recordStepSensorEvent(userId, "TYPE_STEP_COUNTER", 5000f, true, day)
        var daily = repo.observeDailySteps(userId, day).first()
        assertEquals(0, daily?.steps)

        // User walks 1,600 real steps -> cumulative counter = 6600
        repo.recordStepSensorEvent(userId, "TYPE_STEP_COUNTER", 6600f, true, day)
        daily = repo.observeDailySteps(userId, day).first()
        assertEquals(1600, daily?.steps)

        // Phone reboots: TYPE_STEP_COUNTER resets to 250 steps since boot
        repo.recordStepSensorEvent(userId, "TYPE_STEP_COUNTER", 250f, true, day)
        daily = repo.observeDailySteps(userId, day).first()
        assertEquals(1850, daily?.steps)

        // Verify achievements auto-unlocked (first_steps + first_walk) without duplicate coins
        val achs = repo.observeAchievements(userId).first()
        assertTrue(achs.first { it.achievement.code == "first_steps" }.isUnlocked)
        assertTrue(achs.first { it.achievement.code == "first_walk" }.isUnlocked)

        // Walk coins: 5 (1000 steps) + achievements 10 + 20 = 35 coins
        val balance = repo.observeCoinBalance(userId).first()
        assertEquals(35, balance)
    }

    @Test
    fun twoRealStepsAndSensorHubZeroWakeupAnomalyNeverProduce32800StepsOr5Hours() = runTest {
        val day = "2026-11-22"
        val t0 = 1_700_000_000_000L
        val elapsed0 = 500_000_000L
        val userId = repo.registerAccount(
            fullName = "Andrés Vega",
            age = 30,
            weightKg = 76.0,
            goalsDescription = "Caminar a diario",
            experienceLevel = "PRINCIPIANTE",
            activityLevel = "MODERADO",
            availableMinutes = 20,
            pin = null,
            todayKey = day
        )

        // 1. Hardware HAL emits initial 0f flush on registration -> 0 steps
        repo.recordStepSensorEvent(
            userId = userId,
            sensorType = "TYPE_STEP_COUNTER",
            rawValue = 0f,
            isCumulativeCounter = true,
            dateKey = day,
            nowMs = t0,
            elapsedRealtimeMs = elapsed0
        )
        assertEquals(0, repo.observeDailySteps(userId, day).first()?.steps)

        // 2. 150ms later HAL flushes real cumulative boot total 32,800 -> MUST re-anchor, 0 steps!
        repo.recordStepSensorEvent(
            userId = userId,
            sensorType = "TYPE_STEP_COUNTER",
            rawValue = 32800f,
            isCumulativeCounter = true,
            dateKey = day,
            nowMs = t0 + 150L,
            elapsedRealtimeMs = elapsed0 + 150L
        )
        var daily = repo.observeDailySteps(userId, day).first()
        assertEquals(0, daily?.steps)
        assertEquals(0L, daily?.activeSeconds)

        // 3. User actually walks 2 steps in 2 seconds -> cumulative counter 32,802
        repo.recordStepSensorEvent(
            userId = userId,
            sensorType = "TYPE_STEP_COUNTER",
            rawValue = 32802f,
            isCumulativeCounter = true,
            dateKey = day,
            nowMs = t0 + 2_150L,
            elapsedRealtimeMs = elapsed0 + 2_150L
        )
        daily = repo.observeDailySteps(userId, day).first()
        assertEquals(2, daily?.steps)
        assertEquals("1 m", DateUtils.formatDistance(daily?.distanceMeters ?: 0.0))
        assertTrue((daily?.activeSeconds ?: 999L) <= 4L)

        // 4. Duplicate reading 32,802 never double-counts
        repo.recordStepSensorEvent(
            userId = userId,
            sensorType = "TYPE_STEP_COUNTER",
            rawValue = 32802f,
            isCumulativeCounter = true,
            dateKey = day,
            nowMs = t0 + 3_000L,
            elapsedRealtimeMs = elapsed0 + 3_000L
        )
        assertEquals(2, repo.observeDailySteps(userId, day).first()?.steps)

        // 5. User walks 100 more steps -> 102 total
        repo.recordStepSensorEvent(
            userId = userId,
            sensorType = "TYPE_STEP_COUNTER",
            rawValue = 32902f,
            isCumulativeCounter = true,
            dateKey = day,
            nowMs = t0 + 63_000L,
            elapsedRealtimeMs = elapsed0 + 63_000L
        )
        daily = repo.observeDailySteps(userId, day).first()
        assertEquals(102, daily?.steps)
        assertEquals("73 m", DateUtils.formatDistance(daily?.distanceMeters ?: 0.0))
    }

    @Test
    fun everyExerciseHasUniqueOfflineVideoAndRequiredCategories() = runTest {
        repo.initializeCatalog()
        val seeded = repo.observeAllExercises().first()
        assertEquals(CatalogSeedData.defaultExercises.size, seeded.size)
        assertEquals(33, seeded.size)

        val expectedCategories = setOf(
            "CALENTAMIENTO", "RECUPERACION", "PECHO", "BRAZOS",
            "ABDOMEN", "PIERNAS", "ESPALDA", "RESISTENCIA",
            "MOVILIDAD", "CUERPO_COMPLETO"
        )
        assertTrue(seeded.map { it.category }.toSet().containsAll(expectedCategories))

        val mappedIds = mutableSetOf<Int>()
        for (ex in seeded) {
            assertTrue("Missing safetyWarning for ${ex.code}", ex.safetyWarning.isNotBlank())
            assertTrue("Missing videoResName for ${ex.code}", ex.videoResName.isNotBlank())
            val resId = ExerciseVideoMapper.getRawVideoRes(ex)
            assertNotNull("Missing R.raw video mapping for ${ex.code}", resId)
            assertTrue("Duplicate video resource ID for ${ex.code}", mappedIds.add(resId!!))
            val stream = context.resources.openRawResource(resId)
            assertTrue("Empty MP4 video for ${ex.code}", stream.available() > 1000)
            stream.close()
        }
        assertEquals(33, mappedIds.size)
    }

    @Test
    fun migration1To2SanitizesCorrupted32800StepsWhilePreservingUserData() {
        val dbName = "migration_test.db"
        context.deleteDatabase(dbName)
        val dbFile = context.getDatabasePath(dbName)
        dbFile.parentFile?.mkdirs()

        // Create a v1 schema manually and insert a corrupted 32,800-step row + valid user & meal
        val v1Db = SQLiteDatabase.openOrCreateDatabase(dbFile, null)
        v1Db.version = 1
        v1Db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, fullName TEXT NOT NULL, pinHash TEXT, isActiveSession INTEGER NOT NULL, createdAt INTEGER NOT NULL)")
        v1Db.execSQL("CREATE TABLE profiles (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId INTEGER NOT NULL, age INTEGER NOT NULL, weightKg REAL, goalsDescription TEXT NOT NULL, experienceLevel TEXT NOT NULL, activityLevel TEXT NOT NULL, availableMinutes INTEGER NOT NULL, dailyStepGoal INTEGER NOT NULL, updatedAt INTEGER NOT NULL, FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE)")
        v1Db.execSQL("CREATE UNIQUE INDEX index_profiles_userId ON profiles (userId)")
        v1Db.execSQL("CREATE TABLE daily_steps (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId INTEGER NOT NULL, dateKey TEXT NOT NULL, steps INTEGER NOT NULL, distanceMeters REAL NOT NULL, activeSeconds INTEGER NOT NULL, sensorBaselineValue REAL, lastSensorRawValue REAL, stepGoal INTEGER NOT NULL, updatedAt INTEGER NOT NULL, FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE)")
        v1Db.execSQL("CREATE UNIQUE INDEX index_daily_steps_userId_dateKey ON daily_steps (userId, dateKey)")
        v1Db.execSQL("CREATE TABLE daily_history (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId INTEGER NOT NULL, dateKey TEXT NOT NULL, steps INTEGER NOT NULL, distanceMeters REAL NOT NULL, activeSeconds INTEGER NOT NULL, mealsCount INTEGER NOT NULL, exercisesCount INTEGER NOT NULL, achievementsUnlockedCount INTEGER NOT NULL, coinsEarned INTEGER NOT NULL, summaryNote TEXT NOT NULL, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL, FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE)")
        v1Db.execSQL("CREATE UNIQUE INDEX index_daily_history_userId_dateKey ON daily_history (userId, dateKey)")
        v1Db.execSQL("CREATE TABLE exercises (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, code TEXT NOT NULL, name TEXT NOT NULL, category TEXT NOT NULL, explanation TEXT NOT NULL, instructions TEXT NOT NULL, difficulty TEXT NOT NULL, durationSeconds INTEGER NOT NULL, recommendedReps TEXT NOT NULL, restSeconds INTEGER NOT NULL, illustrationKey TEXT NOT NULL, isWarmupOrRecovery INTEGER NOT NULL)")
        v1Db.execSQL("CREATE UNIQUE INDEX index_exercises_code ON exercises (code)")

        v1Db.execSQL("INSERT INTO users (id, fullName, pinHash, isActiveSession, createdAt) VALUES (1, 'Carlos Test', NULL, 1, 1000)")
        v1Db.execSQL("INSERT INTO daily_steps (id, userId, dateKey, steps, distanceMeters, activeSeconds, sensorBaselineValue, lastSensorRawValue, stepGoal, updatedAt) VALUES (1, 1, '2026-11-22', 32800, 23616.0, 18040, 0.0, 32800.0, 6000, 2000)")
        v1Db.execSQL("INSERT INTO daily_history (id, userId, dateKey, steps, distanceMeters, activeSeconds, mealsCount, exercisesCount, achievementsUnlockedCount, coinsEarned, summaryNote, createdAt, updatedAt) VALUES (1, 1, '2026-11-22', 32800, 23616.0, 18040, 2, 1, 0, 15, '', 1000, 2000)")
        v1Db.close()

        val sqliteHelper = FrameworkSQLiteOpenHelperFactory().create(
            androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbName)
                .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(2) {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) = Unit
                    override fun onUpgrade(
                        db: androidx.sqlite.db.SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int
                    ) {
                        DeFittDatabase.MIGRATION_1_2.migrate(db)
                    }
                })
                .build()
        )
        val migratedDb = sqliteHelper.writableDatabase
        migratedDb.query("SELECT steps, distanceMeters, activeSeconds, sensorBaselineValue FROM daily_steps WHERE userId = 1").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals(0, c.getInt(0))
            assertEquals(0.0, c.getDouble(1), 0.01)
            assertEquals(0L, c.getLong(2))
            assertEquals(32800f, c.getFloat(3), 0.1f)
        }
        migratedDb.query("SELECT steps, mealsCount, exercisesCount, coinsEarned FROM daily_history WHERE userId = 1").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals(0, c.getInt(0))
            assertEquals(2, c.getInt(1))
            assertEquals(1, c.getInt(2))
            assertEquals(15, c.getInt(3))
        }
        migratedDb.close()
        context.deleteDatabase(dbName)
    }

    @Test
    fun mealsPhotosExercisesAndDailyRolloverPreserveAllHistory() = runTest {
        val day1 = "2026-11-18"
        val day3 = "2026-11-20" // User skipped opening app on day 2
        val userId = repo.registerAccount(
            fullName = "Sofía Rojas",
            age = 26,
            weightKg = 62.0,
            goalsDescription = "Fortalecer abdomen y piernas",
            experienceLevel = "PRINCIPIANTE",
            activityLevel = "MODERADO",
            availableMinutes = 20,
            pin = null,
            todayKey = day1
        )

        // Create a real local photo file in internal storage
        val photoFile = repo.createCameraOutputFile().apply { writeText("local-jpeg-bytes") }
        repo.saveMeal(
            userId = userId,
            existingMealId = null,
            mealType = "DESAYUNO",
            foodName = "Avena con plátano",
            notes = "Con agua y semillas",
            photoPaths = listOf(photoFile.absolutePath),
            dateKey = day1
        )

        // Complete an exercise on day 1
        repo.initializeCatalog()
        val exercise = repo.observeAllExercises().first().first()
        val earned = repo.completeExercise(userId, exercise, 120, day1)
        assertEquals(10, earned)

        // Simulate 1,200 steps on day 1
        repo.recordStepSensorEvent(userId, "TYPE_STEP_COUNTER", 1000f, true, day1)
        repo.recordStepSensorEvent(userId, "TYPE_STEP_COUNTER", 2200f, true, day1)

        // Rollover to day 3 (skipping day 2 while app was closed)
        repo.ensureCurrentDay(userId, day3)

        val day1History = repo.observeHistoryForDate(userId, day1).first()
        val day2History = repo.observeHistoryForDate(userId, "2026-11-19").first()
        val day3Steps = repo.observeDailySteps(userId, day3).first()

        // Day 1 preserved completely
        assertNotNull(day1History)
        assertEquals(1200, day1History?.steps)
        assertEquals(1, day1History?.mealsCount)
        assertEquals(1, day1History?.exercisesCount)

        // Day 2 created with 0 steps (no fabricated activity)
        assertNotNull(day2History)
        assertEquals(0, day2History?.steps)

        // Day 3 starts fresh at 0 steps, while photo & coins remain intact
        assertEquals(0, day3Steps?.steps)
        assertTrue(File(photoFile.absolutePath).exists())
        assertTrue(repo.observeCoinBalance(userId).first() > 0)

        // Export and restore backup JSON
        val json = repo.exportUserDataToJson(userId)
        val restoredUserId = repo.importUserDataFromJson(json)
        val restoredDay1 = repo.observeHistoryForDate(restoredUserId, day1).first()
        assertEquals(1200, restoredDay1?.steps)
        assertEquals(1, restoredDay1?.mealsCount)
    }
}
