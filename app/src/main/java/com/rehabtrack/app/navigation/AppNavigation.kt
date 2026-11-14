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
import com.rehabtrack.app.ui.viewmodel.SessionCompletionViewModel
import com.rehabtrack.app.ui.viewmodel.SessionDetailViewModel
import com.rehabtrack.app.ui.viewmodel.ProfileViewModel
import com.rehabtrack.app.ui.viewmodel.ChangeEmailViewModel
import com.rehabtrack.app.ui.viewmodel.ChangePasswordViewModel
import com.rehabtrack.app.ui.screens.physiotherapist.PhysiotherapistProfileScreen
import com.rehabtrack.app.ui.viewmodel.PhysiotherapistProfileViewModel

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

        composable("${Routes.Progress.route}/{idUsuario}") { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {
                ProgressScreen(
                    navController = navController,
                    idUsuario = idUsuario
                )
            }
        }

        composable("${Routes.History.route}/{idUsuario}") { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {
                HistoryScreen(
                    navController = navController,
                    idUsuario = idUsuario
                )
            }
        }

        composable(
            route = "${Routes.History.route}/patient/{idPaciente}"
        ) { backStackEntry ->

            val idPaciente = backStackEntry.arguments
                ?.getString("idPaciente")
                ?.toIntOrNull()

            if (idPaciente != null) {
                HistoryScreen(
                    navController = navController,
                    idPaciente = idPaciente
                )
            }
        }

        composable(
            route = "${Routes.SessionDetail.route}/{idSesion}"
        ) { backStackEntry ->

            val idSesion = backStackEntry.arguments
                ?.getString("idSesion")
                ?.toIntOrNull()

            if (idSesion != null) {

                val viewModel: SessionDetailViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(idSesion) {
                    viewModel.cargarDetalle(idSesion)
                }

                SessionDetailScreen(
                    navController = navController,
                    uiState = uiState
                )
            }
        }

        composable("${Routes.Profile.route}/{idUsuario}") { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull()

            if (idUsuario != null) {
                val viewModel: ProfileViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(idUsuario) {
                    viewModel.cargarPerfil(idUsuario)
                }

                ProfileScreen(
                    navController = navController,
                    idUsuario = idUsuario,
                    uiState = uiState,
                    onLogout = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable(
            route = "${Routes.ChangeEmail.route}/{idUsuario}"
        ) { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {

                val viewModel: ChangeEmailViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(idUsuario) {
                    viewModel.cargarCorreo(idUsuario)
                }

                ChangeEmailScreen(
                    navController = navController,
                    idUsuario = idUsuario,
                    uiState = uiState,
                    onChangeEmail = { nuevoCorreo ->
                        viewModel.cambiarCorreo(
                            idUsuario = idUsuario,
                            nuevoCorreo = nuevoCorreo
                        )
                    }
                )
            }
        }

        composable(
            route = "${Routes.ChangePassword.route}/{idUsuario}"
        ) { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {

                val viewModel: ChangePasswordViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                ChangePasswordScreen(
                    navController = navController,
                    idUsuario = idUsuario,
                    uiState = uiState,
                    onChangePassword = { passwordActual, nuevaPassword, confirmarPassword ->

                        viewModel.cambiarPassword(
                            idUsuario = idUsuario,
                            passwordActual = passwordActual,
                            nuevaPassword = nuevaPassword,
                            confirmarPassword = confirmarPassword
                        )
                    }
                )
            }
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
                            route = "${Routes.Home.route}/{idUsuario}",
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

                val viewModel: SessionCompletionViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(idSesion) {
                    viewModel.cargarSesion(idSesion)
                }

                SessionCompletionScreen(
                    navController = navController,
                    idSesion = idSesion,
                    uiState = uiState,
                    onFinalizarSesion = { nivelDolor, observaciones ->
                        viewModel.finalizarSesion(
                            idSesion = idSesion,
                            nivelDolor = nivelDolor,
                            observaciones = observaciones
                        )
                    }
                )
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
                        navController.navigate("${Routes.PhysiotherapistProfile.route}/$idUsuario") {
                            launchSingleTop = true
                        }
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
                        navController.navigate("${Routes.PhysiotherapistProfile.route}/$idUsuario") {
                            launchSingleTop = true
                        }
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

        composable("${Routes.PhysiotherapistProfile.route}/{idUsuario}") { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {
                val viewModel: PhysiotherapistProfileViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(idUsuario) {
                    viewModel.cargarPerfil(idUsuario)
                }

                PhysiotherapistProfileScreen(
                    navController = navController,
                    idUsuario = idUsuario,
                    uiState = uiState,
                    onHomeClick = {
                        navController.navigate("${Routes.PhysiotherapistHome.route}/$idUsuario") {
                            launchSingleTop = true
                        }
                    },
                    onPatientsClick = {
                        navController.navigate("${Routes.Patients.route}/$idUsuario") {
                            launchSingleTop = true
                        }
                    },
                    onProfileClick = {
                        navController.navigate("${Routes.PhysiotherapistProfile.route}/$idUsuario") {
                            launchSingleTop = true
                        }
                    },
                    onLogout = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
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