package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DeFittDatabase
import com.example.data.repository.DeFittRepository
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
