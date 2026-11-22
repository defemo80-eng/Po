package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appNameMatchesDeFitt() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("DeFitt", context.getString(R.string.app_name))
    }

    @Test
    fun registrationToDashboardExerciseDetailAndTabNavigationFlowWorks() {
        composeTestRule.waitForIdle()

        // Welcome screen
        composeTestRule.onNodeWithText("BIENVENIDO A DEFITT.").assertIsDisplayed()
        composeTestRule.onNodeWithTag("welcome_create_account_button").performClick()
        composeTestRule.waitForIdle()

        // Registration form
        composeTestRule.onNodeWithTag("register_name_input").performTextInput("Carlos Mendoza")
        composeTestRule.onNodeWithTag("register_age_input").performTextInput("24")
        composeTestRule.onNodeWithTag("register_goals_input")
            .performTextInput("Quiero mejorar mi resistencia y fortalecer mi cuerpo")
        composeTestRule.onNodeWithTag("register_submit_button")
            .performScrollTo()
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            runCatching {
                composeTestRule.onNodeWithTag("nav_meals").assertIsDisplayed()
                true
            }.getOrDefault(false)
        }

        // Navigate across all 5 bottom bar tabs
        composeTestRule.onNodeWithTag("nav_meals").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("add_meal_fab").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_exercises").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("RUTINAS INTEGRADAS").assertIsDisplayed()

        // Scroll LazyColumn to exercise card and open dedicated Exercise Detail Screen
        composeTestRule.onNodeWithTag("exercises_lazy_column")
            .performScrollToNode(hasTestTag("exercise_card_warmup_joint_mobility"))
        composeTestRule.onNodeWithTag("exercise_card_warmup_joint_mobility").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("exercise_video_player").assertIsDisplayed()
        composeTestRule.onNodeWithTag("exercise_detail_list")
            .performScrollToNode(hasTestTag("exercise_start_button"))
        composeTestRule.onNodeWithTag("exercise_start_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("exercise_complete_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("exercise_complete_button").performClick()
        composeTestRule.waitForIdle()

        // Returned cleanly to Exercises list
        composeTestRule.onNodeWithText("RUTINAS INTEGRADAS").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_achievements").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("defitt_coins_wallet_card").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_records").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("records_tab_0").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_home").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Hola, Carlos").assertIsDisplayed()
    }
}
