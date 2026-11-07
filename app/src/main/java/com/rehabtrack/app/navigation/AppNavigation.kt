package com.rehabtrack.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rehabtrack.app.ui.screens.auth.LoginScreen
import com.rehabtrack.app.ui.screens.auth.RegisterScreen
import com.rehabtrack.app.ui.screens.patient.HomeScreen
import com.rehabtrack.app.ui.screens.patient.ProgressScreen
import com.rehabtrack.app.ui.screens.patient.HistoryScreen
import com.rehabtrack.app.ui.screens.patient.SessionDetailScreen
import com.rehabtrack.app.ui.screens.profile.ProfileScreen
import com.rehabtrack.app.ui.screens.profile.ChangeEmailScreen
import com.rehabtrack.app.ui.screens.profile.ChangePasswordScreen
import com.rehabtrack.app.ui.screens.patient.ExerciseConfirmationScreen
import com.rehabtrack.app.ui.screens.patient.ExerciseExecutionScreen
import com.rehabtrack.app.ui.screens.patient.NextSessionScreen
import com.rehabtrack.app.ui.screens.patient.SessionConfirmationScreen
import com.rehabtrack.app.ui.screens.patient.ExerciseInfoScreen
import com.rehabtrack.app.ui.screens.patient.SessionCompletionScreen
import com.rehabtrack.app.ui.screens.physiotherapist.PhysiotherapistHomeScreen
import com.rehabtrack.app.ui.screens.physiotherapist.PatientDetailScreen
import com.rehabtrack.app.ui.screens.physiotherapist.ManageProgramScreen
import com.rehabtrack.app.ui.screens.physiotherapist.SelectExercisesScreen
import com.rehabtrack.app.ui.screens.physiotherapist.PatientsScreen
import com.rehabtrack.app.ui.screens.physiotherapist.RegisterPatientScreen
import com.rehabtrack.app.ui.screens.physiotherapist.SelectPatientScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    var ejercicioActual by remember {
        mutableIntStateOf(0)
    }

    val ejercicios = listOf(
        "Sentadilla asistida",
        "Extensión de rodilla",
        "Elevación de pierna"
    )

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

        composable(
            route = "${Routes.History.route}/{patientName}"
        ) { backStackEntry ->

            val patientName = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("patientName") ?: "Paciente",
                "UTF-8"
            )

            HistoryScreen(
                navController = navController,
                patientName = patientName
            )
        }

        composable(Routes.SessionDetail.route) {
            SessionDetailScreen(navController)
        }

        composable(
            route = "${Routes.SessionDetail.route}/{patientName}"
        ) { backStackEntry ->

            val patientName = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("patientName") ?: "Paciente",
                "UTF-8"
            )

            SessionDetailScreen(
                navController = navController,
                patientName = patientName
            )
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
            NextSessionScreen(
                navController = navController,
                onStartSession = {
                    ejercicioActual = 0
                    navController.navigate(Routes.SessionConfirmation.route) {
                        popUpTo(Routes.NextSession.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }

        composable(Routes.SessionConfirmation.route) {
            SessionConfirmationScreen(navController)
        }

        composable(Routes.ExerciseInfo.route) {
            ExerciseInfoScreen(
                navController = navController,
                nombreEjercicio = ejercicios[ejercicioActual]
            )
        }

        composable(Routes.Exercise.route) {
            ExerciseExecutionScreen(navController)
        }

        composable(Routes.ExerciseConfirmation.route) {
            ExerciseConfirmationScreen(
                navController = navController,
                onContinue = {
                    if (ejercicioActual < ejercicios.lastIndex) {
                        ejercicioActual++

                        navController.navigate(
                            Routes.ExerciseInfo.route
                        ) {
                            popUpTo(Routes.ExerciseInfo.route) {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.navigate(
                            Routes.SessionCompletion.route
                        ) {
                            popUpTo(Routes.NextSession.route) {
                                inclusive = false
                            }
                        }
                    }
                },
                onExit = {
                    navController.popBackStack(
                        Routes.NextSession.route,
                        inclusive = false
                    )
                }
            )
        }

        composable(Routes.SessionCompletion.route) {
            SessionCompletionScreen(navController)
        }

        composable(
            route = "${Routes.PhysiotherapistHome.route}/{idUsuario}"
        ) { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {
                PhysiotherapistHomeScreen(
                    idUsuario = idUsuario,

                    onHomeClick = {
                        navController.navigate(
                            "${Routes.PhysiotherapistHome.route}/$idUsuario"
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onPatientsClick = {
                        navController.navigate(
                            "${Routes.Patients.route}/$idUsuario"
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onProfileClick = {
                        // Pendiente de implementar.
                    }
                )
            }
        }

        composable(
            route = "${Routes.Patients.route}/{idUsuario}"
        ) { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {
                PatientsScreen(
                    idUsuario = idUsuario,
                    onHomeClick = {
                        navController.navigate(
                            "${Routes.PhysiotherapistHome.route}/$idUsuario"
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onPatientsClick = {
                        navController.navigate(
                            "${Routes.Patients.route}/$idUsuario"
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onProfileClick = {
                        // Pendiente de implementar.
                    },
                    onPatientClick = { idPaciente ->
                        navController.navigate(
                            "${Routes.PatientDetail.route}/$idUsuario/$idPaciente"
                        )
                    },
                    onSelectExistingPatientClick = {
                        navController.navigate(
                            "${Routes.SelectPatient.route}/$idUsuario"
                        )
                    },
                    onRegisterNewPatientClick = {
                        navController.navigate(
                            "${Routes.RegisterPatient.route}/$idUsuario"
                        )
                    }
                )
            }
        }

        composable(
            route = "${Routes.RegisterPatient.route}/{idUsuario}"
        ) { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {
                RegisterPatientScreen(
                    idUsuario = idUsuario,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            route = "${Routes.PatientDetail.route}/{idUsuario}/{idPaciente}"
        ) { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            val idPaciente = backStackEntry.arguments
                ?.getString("idPaciente")
                ?.toIntOrNull()

            if (idUsuario != null && idPaciente != null) {
                PatientDetailScreen(
                    navController = navController,
                    idUsuario = idUsuario,
                    idPaciente = idPaciente
                )
            }
        }

        composable(
            route = "${Routes.ManageProgram.route}/{patientName}"
        ) { backStackEntry ->

            val patientName = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("patientName") ?: "Paciente",
                "UTF-8"
            )

            ManageProgramScreen(
                navController = navController,
                patientName = patientName
            )
        }

        composable(Routes.SelectExercises.route) {
            SelectExercisesScreen(navController)
        }

        composable(
            route = "${Routes.SelectPatient.route}/{idUsuario}"
        ) { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {
                SelectPatientScreen(
                    idUsuario = idUsuario,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
