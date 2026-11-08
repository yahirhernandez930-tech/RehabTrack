package com.rehabtrack.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rehabtrack.app.ui.viewmodel.NextSessionViewModel
import com.rehabtrack.app.ui.viewmodel.SessionExercisesViewModel
import com.rehabtrack.app.ui.viewmodel.SessionExercisesUiState
import com.rehabtrack.app.ui.viewmodel.SessionExerciseData
import com.rehabtrack.app.ui.viewmodel.SessionProgressViewModel

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    var ejerciciosSesion by remember {
        mutableStateOf<List<SessionExerciseData>>(emptyList())
    }

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

        composable(
            route = "${Routes.Home.route}/{idUsuario}"
        ) { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {
                HomeScreen(
                    navController = navController,
                    idUsuario = idUsuario
                )
            }
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

        composable(
            route = "${Routes.NextSession.route}/{idSesion}"
        ) { backStackEntry ->

            val idSesion = backStackEntry.arguments
                ?.getString("idSesion")
                ?.toIntOrNull()

            if (idSesion != null) {

                val viewModel: NextSessionViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(idSesion) {
                    viewModel.cargarSesion(idSesion)
                }

                NextSessionScreen(
                    navController = navController,
                    uiState = uiState,
                    onStartSession = {
                        navController.navigate(
                            "${Routes.ExerciseInfo.route}/$idSesion"
                        ) {
                            popUpTo(Routes.NextSession.route) {
                                inclusive = false
                            }
                        }
                    }
                )
            }
        }

        composable(
            route = "${Routes.SessionConfirmation.route}/{idSesion}"
        ) { backStackEntry ->

            val idSesion = backStackEntry.arguments
                ?.getString("idSesion")
                ?.toIntOrNull()

            if (idSesion != null) {

                val viewModel: SessionExercisesViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(idSesion) {
                    viewModel.cargarEjercicios(idSesion)
                }

                val cargando = uiState is SessionExercisesUiState.Loading

                val cantidadEjercicios =
                    (uiState as? SessionExercisesUiState.Success)
                        ?.ejercicios
                        ?.size ?: 0

                val mensajeError =
                    (uiState as? SessionExercisesUiState.Error)
                        ?.mensaje

                SessionConfirmationScreen(
                    navController = navController,
                    idSesion = idSesion,
                    cantidadEjercicios = cantidadEjercicios,
                    cargando = cargando,
                    mensajeError = mensajeError,
                    onComenzar = {
                        val resultado =
                            uiState as? SessionExercisesUiState.Success

                        if (resultado != null) {
                            ejerciciosSesion = resultado.ejercicios

                            navController.navigate(
                                "${Routes.ExerciseInfo.route}/$idSesion/0"
                            )
                        }
                    }
                )
            }
        }

        composable(
            route = "${Routes.ExerciseInfo.route}/{idSesion}/{indiceEjercicio}"
        ) { backStackEntry ->

            val idSesion = backStackEntry.arguments
                ?.getString("idSesion")
                ?.toIntOrNull()

            val indiceEjercicio = backStackEntry.arguments
                ?.getString("indiceEjercicio")
                ?.toIntOrNull()

            if (idSesion != null && indiceEjercicio != null) {
                val ejercicio = ejerciciosSesion.getOrNull(indiceEjercicio)

                if (ejercicio != null) {
                    ExerciseInfoScreen(
                        navController = navController,
                        nombreEjercicio = ejercicio.nombre,
                        descripcion = ejercicio.descripcion,
                        instrucciones = ejercicio.instrucciones,
                        series = ejercicio.series,
                        repeticiones = ejercicio.repeticiones,
                        idSesion = idSesion,
                        indiceEjercicio = indiceEjercicio
                    )
                }
            }
        }

        composable(
            route = "${Routes.Exercise.route}/{idSesion}/{indiceEjercicio}"
        ) { backStackEntry ->

            val idSesion = backStackEntry.arguments
                ?.getString("idSesion")
                ?.toIntOrNull()

            val indiceEjercicio = backStackEntry.arguments
                ?.getString("indiceEjercicio")
                ?.toIntOrNull()

            if (idSesion != null && indiceEjercicio != null) {
                val ejercicio = ejerciciosSesion.getOrNull(indiceEjercicio)

                if (ejercicio != null) {
                    val progressViewModel: SessionProgressViewModel = viewModel()
                    val progressState by progressViewModel.uiState.collectAsState()

                    LaunchedEffect(idSesion, ejercicio.idEjercicio) {
                        progressViewModel.cargarProgreso(
                            idSesion = idSesion,
                            idEjercicio = ejercicio.idEjercicio
                        )
                    }

                    ExerciseExecutionScreen(
                        navController = navController,
                        idSesion = idSesion,
                        indiceEjercicio = indiceEjercicio,
                        idEjercicio = ejercicio.idEjercicio,
                        nombreEjercicio = ejercicio.nombre,
                        totalSeries = ejercicio.series,
                        repeticiones = ejercicio.repeticiones,
                        registros = progressState.registros,
                        cargandoProgreso = progressState.cargando,
                        guardandoSerie = progressState.guardando,
                        mensajeError = progressState.mensajeError,
                        onRegistrarSerie = { numeroSerie, repeticionesRealizadas ->
                            progressViewModel.registrarSerie(
                                idSesion = idSesion,
                                idEjercicio = ejercicio.idEjercicio,
                                numeroSerie = numeroSerie,
                                repeticionesRealizadas = repeticionesRealizadas
                            )
                        }
                    )
                }
            }
        }

        composable(
            route = "${Routes.ExerciseConfirmation.route}/{idSesion}/{indiceEjercicio}"
        ) { backStackEntry ->

            val idSesion = backStackEntry.arguments
                ?.getString("idSesion")
                ?.toIntOrNull()

            val indiceEjercicio = backStackEntry.arguments
                ?.getString("indiceEjercicio")
                ?.toIntOrNull()

            if (idSesion != null && indiceEjercicio != null) {
                ExerciseConfirmationScreen(
                    navController = navController,
                    onContinue = {
                        if (indiceEjercicio < ejerciciosSesion.lastIndex) {
                            val siguienteIndice = indiceEjercicio + 1

                            navController.navigate(
                                "${Routes.ExerciseInfo.route}/$idSesion/$siguienteIndice"
                            )
                        } else {
                            navController.navigate(
                                "${Routes.SessionCompletion.route}/$idSesion"
                            )
                        }
                    },
                    onExit = {
                        navController.popBackStack(
                            Routes.Home.route,
                            inclusive = false
                        )
                    }
                )
            }
        }

        composable(
            route = "${Routes.SessionCompletion.route}/{idSesion}"
        ) { backStackEntry ->

            val idSesion = backStackEntry.arguments
                ?.getString("idSesion")
                ?.toIntOrNull()

            if (idSesion != null) {
                SessionCompletionScreen(navController)
            }
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
            route = "${Routes.ManageProgram.route}/{idUsuario}/{idPaciente}"
        ) { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            val idPaciente = backStackEntry.arguments
                ?.getString("idPaciente")
                ?.toIntOrNull()

            if (idUsuario != null && idPaciente != null) {
                ManageProgramScreen(
                    navController = navController,
                    idUsuario = idUsuario,
                    idPaciente = idPaciente
                )
            }
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