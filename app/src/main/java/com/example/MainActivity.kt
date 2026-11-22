package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.DeFittSplashScreen
import com.example.ui.screens.ExercisesScreen
import com.example.ui.screens.ExistingAccountScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MealsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RecordsAndDailyLogScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.DeFittBlack
import com.example.ui.theme.DeFittSurface
import com.example.ui.theme.DeFittTextSecondary
import com.example.ui.theme.DeFittTheme
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenContainer
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.DeFittViewModel

enum class MainTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "INICIO", Icons.Filled.Home, Icons.Outlined.Home),
    MEALS("meals", "COMIDA", Icons.Filled.Restaurant, Icons.Outlined.Restaurant),
    EXERCISES("exercises", "EJERCICIOS", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
    ACHIEVEMENTS("achievements", "LOGROS", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents),
    RECORDS("records", "REGISTROS", Icons.Filled.History, Icons.Outlined.History)
}

private enum class AuthScreenRoute {
    WELCOME, REGISTER, EXISTING_ACCOUNT
}

class MainActivity : ComponentActivity() {

    private val viewModel: DeFittViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeFittTheme {
                DeFittAppRoot(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshDayAndSensors()
    }
}

@Composable
fun DeFittAppRoot(viewModel: DeFittViewModel) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    var authRoute by rememberSaveable { mutableStateOf(AuthScreenRoute.WELCOME) }
    var currentTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    var showProfile by rememberSaveable { mutableStateOf(false) }

    when (val state = authState) {
        AuthUiState.Loading -> {
            DeFittSplashScreen()
        }

        is AuthUiState.LoggedOut -> {
            showProfile = false
            Scaffold(
                containerColor = DeFittBlack,
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    when (authRoute) {
                        AuthScreenRoute.WELCOME -> WelcomeScreen(
                            hasExistingAccounts = state.localAccounts.isNotEmpty(),
                            onCreateAccountClick = { authRoute = AuthScreenRoute.REGISTER },
                            onExistingAccountClick = { authRoute = AuthScreenRoute.EXISTING_ACCOUNT }
                        )
                        AuthScreenRoute.REGISTER -> RegisterScreen(
                            onBack = { authRoute = AuthScreenRoute.WELCOME },
                            onSubmit = { name, age, weight, goals, exp, act, mins, pin ->
                                viewModel.registerAccount(
                                    fullName = name,
                                    age = age,
                                    weightKg = weight,
                                    goalsDescription = goals,
                                    experienceLevel = exp,
                                    activityLevel = act,
                                    availableMinutes = mins,
                                    pin = pin
                                ) {
                                    currentTab = MainTab.HOME
                                    authRoute = AuthScreenRoute.WELCOME
                                }
                            }
                        )
                        AuthScreenRoute.EXISTING_ACCOUNT -> ExistingAccountScreen(
                            accounts = state.localAccounts,
                            onBack = { authRoute = AuthScreenRoute.WELCOME },
                            onLogin = { userId, pin, cb ->
                                viewModel.loginExistingAccount(userId, pin) { ok ->
                                    if (ok) {
                                        currentTab = MainTab.HOME
                                        authRoute = AuthScreenRoute.WELCOME
                                    }
                                    cb(ok)
                                }
                            },
                            onRestoreBackupUri = { uri ->
                                viewModel.importBackupFromUri(uri)
                            }
                        )
                    }
                }
            }
        }

        is AuthUiState.Authenticated -> {
            val todayKey by viewModel.todayKey.collectAsStateWithLifecycle()
            val todaySteps by viewModel.todaySteps.collectAsStateWithLifecycle()
            val todayMeals by viewModel.todayMeals.collectAsStateWithLifecycle()
            val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()
            val personalizedExercises by viewModel.personalizedExercises.collectAsStateWithLifecycle()
            val allRoutines by viewModel.allRoutines.collectAsStateWithLifecycle()
            val todayCompleted by viewModel.todayCompletedExercises.collectAsStateWithLifecycle()
            val achievements by viewModel.achievements.collectAsStateWithLifecycle()
            val coinBalance by viewModel.coinBalance.collectAsStateWithLifecycle()
            val todayCoins by viewModel.todayCoins.collectAsStateWithLifecycle()
            val coinHistory by viewModel.coinHistory.collectAsStateWithLifecycle()
            val allHistory by viewModel.allHistory.collectAsStateWithLifecycle()
            val allDailySteps by viewModel.allDailySteps.collectAsStateWithLifecycle()
            val selectedRecordDateKey by viewModel.selectedRecordDateKey.collectAsStateWithLifecycle()
            val selectedDateHistory by viewModel.selectedDateHistory.collectAsStateWithLifecycle()
            val selectedDateMeals by viewModel.selectedDateMeals.collectAsStateWithLifecycle()
            val selectedDateExercises by viewModel.selectedDateExercises.collectAsStateWithLifecycle()
            val selectedDateCoins by viewModel.selectedDateCoins.collectAsStateWithLifecycle()
            val sensorStatus by viewModel.sensorStatus.collectAsStateWithLifecycle()

            if (showProfile) {
                Scaffold(
                    containerColor = DeFittBlack,
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        ProfileScreen(
                            user = state.user,
                            profile = state.profile,
                            settings = state.settings,
                            sensorStatus = sensorStatus,
                            onBack = { showProfile = false },
                            onUpdateProfile = viewModel::updateProfile,
                            onUpdateSettings = viewModel::updateSettings,
                            onExportBackupUri = viewModel::exportBackupToUri,
                            onImportBackupUri = viewModel::importBackupFromUri,
                            onLogout = {
                                showProfile = false
                                viewModel.logout()
                            },
                            onDeleteAccount = {
                                showProfile = false
                                viewModel.deleteLocalAccount()
                            }
                        )
                    }
                }
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxSize().background(DeFittBlack)) {
                    val useNavRail = maxWidth >= 680.dp

                    Scaffold(
                        containerColor = DeFittBlack,
                        contentWindowInsets = WindowInsets.safeDrawing,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        bottomBar = {
                            if (!useNavRail) {
                                NavigationBar(
                                    containerColor = DeFittSurface,
                                    tonalElevation = 0.dp
                                ) {
                                    MainTab.entries.forEach { tab ->
                                        val selected = currentTab == tab
                                        NavigationBarItem(
                                            selected = selected,
                                            onClick = { currentTab = tab },
                                            icon = {
                                                Icon(
                                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = tab.label
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = tab.label,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    maxLines = 1
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = NeonGreen,
                                                selectedTextColor = NeonGreen,
                                                indicatorColor = NeonGreenContainer,
                                                unselectedIconColor = DeFittTextSecondary,
                                                unselectedTextColor = DeFittTextSecondary
                                            ),
                                            modifier = Modifier.testTag("nav_${tab.route}")
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (useNavRail) {
                                NavigationRail(
                                    containerColor = DeFittSurface,
                                    modifier = Modifier.fillMaxHeight()
                                ) {
                                    MainTab.entries.forEach { tab ->
                                        val selected = currentTab == tab
                                        NavigationRailItem(
                                            selected = selected,
                                            onClick = { currentTab = tab },
                                            icon = {
                                                Icon(
                                                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = tab.label
                                                )
                                            },
                                            label = { Text(tab.label, style = MaterialTheme.typography.labelMedium) },
                                            colors = NavigationRailItemDefaults.colors(
                                                selectedIconColor = NeonGreen,
                                                selectedTextColor = NeonGreen,
                                                indicatorColor = NeonGreenContainer,
                                                unselectedIconColor = DeFittTextSecondary,
                                                unselectedTextColor = DeFittTextSecondary
                                            ),
                                            modifier = Modifier.testTag("rail_${tab.route}")
                                        )
                                    }
                                }
                            }

                            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                when (currentTab) {
                                    MainTab.HOME -> HomeScreen(
                                        user = state.user,
                                        profile = state.profile,
                                        todaySteps = todaySteps,
                                        mealsTodayCount = todayMeals.size,
                                        exercisesTodayCount = todayCompleted.size,
                                        unlockedAchievementsCount = achievements.count { it.isUnlocked },
                                        totalAchievementsCount = achievements.size,
                                        coinBalance = coinBalance,
                                        sensorStatus = sensorStatus,
                                        onRefreshSensors = { viewModel.startForegroundStepListener() },
                                        onOpenMeals = { currentTab = MainTab.MEALS },
                                        onOpenExercises = { currentTab = MainTab.EXERCISES },
                                        onOpenAchievements = { currentTab = MainTab.ACHIEVEMENTS },
                                        onOpenDailyLog = {
                                            viewModel.selectRecordDate(todayKey)
                                            currentTab = MainTab.RECORDS
                                        },
                                        onOpenProfile = { showProfile = true }
                                    )

                                    MainTab.MEALS -> MealsScreen(
                                        mealsToday = todayMeals,
                                        selectedDateKey = todayKey,
                                        onCreateCameraFile = viewModel::createCameraOutputFile,
                                        onCopyPickedUri = viewModel::copyPickedPhotoToInternal,
                                        onSaveMeal = viewModel::saveMeal,
                                        onDeleteMeal = viewModel::deleteMeal
                                    )

                                    MainTab.EXERCISES -> ExercisesScreen(
                                        profile = state.profile,
                                        allExercises = allExercises,
                                        personalizedExercises = personalizedExercises,
                                        routines = allRoutines,
                                        completedToday = todayCompleted,
                                        onCompleteExercise = viewModel::completeExercise
                                    )

                                    MainTab.ACHIEVEMENTS -> AchievementsScreen(
                                        achievements = achievements,
                                        coinBalance = coinBalance,
                                        todayCoins = todayCoins,
                                        coinHistory = coinHistory
                                    )

                                    MainTab.RECORDS -> RecordsAndDailyLogScreen(
                                        todayKey = todayKey,
                                        selectedDateKey = selectedRecordDateKey,
                                        selectedDateHistory = selectedDateHistory,
                                        selectedDateMeals = selectedDateMeals,
                                        selectedDateExercises = selectedDateExercises,
                                        selectedDateCoins = selectedDateCoins,
                                        allHistory = allHistory,
                                        allDailySteps = allDailySteps,
                                        stepGoal = state.profile?.dailyStepGoal ?: 6000,
                                        onSelectDate = viewModel::selectRecordDate,
                                        onExportBackupUri = viewModel::exportBackupToUri,
                                        onImportBackupUri = viewModel::importBackupFromUri
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
