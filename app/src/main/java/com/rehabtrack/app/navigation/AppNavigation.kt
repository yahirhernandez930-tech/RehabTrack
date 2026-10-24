package com.rehabtrack.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rehabtrack.app.ui.screens.LoginScreen
import com.rehabtrack.app.ui.screens.RegisterScreen
import com.rehabtrack.app.ui.screens.HomeScreen
import com.rehabtrack.app.ui.screens.ProgressScreen
import com.rehabtrack.app.ui.screens.HistoryScreen
import com.rehabtrack.app.ui.screens.SessionDetailScreen
import com.rehabtrack.app.ui.screens.ProfileScreen
import com.rehabtrack.app.ui.screens.ChangeEmailScreen
import com.rehabtrack.app.ui.screens.ChangePasswordScreen
import com.rehabtrack.app.ui.screens.ExerciseConfirmationScreen
import com.rehabtrack.app.ui.screens.ExerciseExecutionScreen
import com.rehabtrack.app.ui.screens.NextSessionScreen
import com.rehabtrack.app.ui.screens.SessionConfirmationScreen
import com.rehabtrack.app.ui.screens.ExerciseInfoScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {

        composable(Routes.Login.route) {
            LoginScreen(navController)
        }

        composable(Routes.Register.route) {
            RegisterScreen(navController)
        }

        composable(Routes.Home.route) {
            HomeScreen(navController)
        }

        composable(Routes.Progress.route) {
            ProgressScreen(navController)
        }

        composable(Routes.History.route) {
            HistoryScreen(navController)
        }

        composable(Routes.SessionDetail.route) {
            SessionDetailScreen(navController)
        }

        composable(Routes.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Routes.ChangeEmail.route) {
            ChangeEmailScreen(navController)
        }

        composable(Routes.ChangePassword.route) {
            ChangePasswordScreen(navController)
        }

        composable(Routes.NextSession.route) {
            NextSessionScreen(navController)
        }

        composable(Routes.SessionConfirmation.route) {
            SessionConfirmationScreen(navController)
        }

        composable(Routes.ExerciseInfo.route) {
            ExerciseInfoScreen(navController)
        }

        composable(Routes.Exercise.route) {
            ExerciseExecutionScreen(navController)
        }

        composable(Routes.ExerciseConfirmation.route) {
            ExerciseConfirmationScreen(
                navController = navController,
                onContinue = {
                    navController.navigate(Routes.ExerciseInfo.route)
                },
                onExit = {
                    navController.popBackStack(
                        Routes.NextSession.route,
                        inclusive = false
                    )
                }
            )
        }
    }
}