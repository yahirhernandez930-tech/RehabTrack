package com.rehabtrack.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.rehabtrack.app.ui.viewmodel.SessionProgressViewModel
import com.rehabtrack.app.ui.viewmodel.SessionCompletionViewModel
import com.rehabtrack.app.ui.viewmodel.SessionDetailViewModel
import com.rehabtrack.app.ui.viewmodel.ProfileViewModel
import com.rehabtrack.app.ui.viewmodel.ChangeEmailViewModel
import com.rehabtrack.app.ui.viewmodel.ChangePasswordViewModel
import com.rehabtrack.app.ui.screens.physiotherapist.PhysiotherapistProfileScreen
import com.rehabtrack.app.ui.viewmodel.PhysiotherapistProfileViewModel
import com.rehabtrack.app.ui.viewmodel.ProgressViewModel
import com.rehabtrack.app.ui.viewmodel.EvidenciaViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Text
import com.rehabtrack.app.data.preferences.SessionManager
import kotlinx.coroutines.flow.first
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.rehabtrack.app.ui.screens.physiotherapist.ManageExerciseVideosScreen
import com.rehabtrack.app.ui.viewmodel.NavigationViewModel
import com.rehabtrack.app.ui.screens.admin.AdminHomeScreen
import com.rehabtrack.app.ui.screens.admin.RegisterPhysiotherapistScreen
import com.rehabtrack.app.ui.screens.admin.AdminSetupScreen
import com.rehabtrack.app.ui.viewmodel.AuthViewModel
import com.rehabtrack.app.ui.screens.admin.BackupScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current

    val sessionManager = remember {
        SessionManager(context)
    }

    val coroutineScope = rememberCoroutineScope()

    val navigationViewModel: NavigationViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "session_check"
    ) {

        composable("session_check") {

            val authViewModel: AuthViewModel = viewModel()

            LaunchedEffect(Unit) {

                val session = sessionManager.sessionFlow.first()

                val destino = if (session != null) {

                    when (session.rol) {
                        "PACIENTE" ->
                            "${Routes.Home.route}/${session.idUsuario}"

                        "FISIOTERAPEUTA" ->
                            "${Routes.PhysiotherapistHome.route}/${session.idUsuario}"

                        "ADMINISTRADOR" ->
                            "${Routes.AdminHome.route}/${session.idUsuario}"

                        else -> Routes.Login.route
                    }

                } else {

                    val existeAdministrador =
                        authViewModel.existeAdministrador()

                    if (existeAdministrador) {
                        Routes.Login.route
                    } else {
                        Routes.AdminSetup.route
                    }
                }

                navController.navigate(destino) {
                    popUpTo("session_check") {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            Text(text = "Verificando configuración...")
        }

        composable(Routes.AdminSetup.route) {

            AdminSetupScreen(
                onSetupSuccess = {

                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.AdminSetup.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

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

                val viewModel: ProgressViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(idUsuario) {
                    viewModel.cargarProgreso(idUsuario)
                }

                ProgressScreen(
                    navController = navController,
                    idUsuario = idUsuario,
                    uiState = uiState
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
                        coroutineScope.launch {
                            sessionManager.cerrarSesion()

                            navController.navigate(Routes.Login.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
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
                            navigationViewModel.guardarEjercicios(resultado.ejercicios)

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
                val ejercicio = navigationViewModel.obtenerEjercicio(indiceEjercicio)

                if (ejercicio != null) {
                    ExerciseInfoScreen(
                        navController = navController,
                        nombreEjercicio = ejercicio.nombre,
                        descripcion = ejercicio.descripcion,
                        instrucciones = ejercicio.instrucciones,
                        series = ejercicio.series,
                        repeticiones = ejercicio.repeticiones,
                        idSesion = idSesion,
                        indiceEjercicio = indiceEjercicio,
                        videoUrl = ejercicio.videoUrl
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
                val ejercicio = navigationViewModel.obtenerEjercicio(indiceEjercicio)

                if (ejercicio != null) {
                    val progressViewModel: SessionProgressViewModel = viewModel()
                    val progressState by progressViewModel.uiState.collectAsState()
                    val evidenciaViewModel: EvidenciaViewModel = viewModel()
                    val evidenciaState by evidenciaViewModel.uiState.collectAsState()

                    LaunchedEffect(idSesion, ejercicio.idEjercicio) {
                        progressViewModel.cargarProgreso(
                            idSesion = idSesion,
                            idEjercicio = ejercicio.idEjercicio
                        )
                    }

                    LaunchedEffect(idSesion, ejercicio.idEjercicio) {
                        evidenciaViewModel.cargarEvidencias(
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
                        evidencias = evidenciaState.evidencias,
                        guardandoEvidencia = evidenciaState.guardando,
                        mensajeErrorEvidencia = evidenciaState.mensajeError,
                        onRegistrarSerie = { numeroSerie, repeticionesRealizadas ->
                            progressViewModel.registrarSerie(
                                idSesion = idSesion,
                                idEjercicio = ejercicio.idEjercicio,
                                numeroSerie = numeroSerie,
                                repeticionesRealizadas = repeticionesRealizadas
                            )
                        },
                        onFotografiaCapturada = { rutaArchivo ->
                            evidenciaViewModel.registrarEvidencia(
                                idSesion = idSesion,
                                idEjercicio = ejercicio.idEjercicio,
                                rutaArchivo = rutaArchivo
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
                        if (indiceEjercicio < navigationViewModel.ejerciciosSesion.lastIndex) {
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

        composable("backup") {
            BackupScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("${Routes.AdminHome.route}/{idUsuario}") { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {
                AdminHomeScreen(
                    idUsuario = idUsuario,

                    onRegisterPhysiotherapistClick = {
                        navController.navigate(
                            "${Routes.RegisterPhysiotherapist.route}/$idUsuario"
                        )
                    },

                    onBackupClick = {
                        navController.navigate("backup")
                    },

                    onLogout = {
                        coroutineScope.launch {
                            sessionManager.cerrarSesion()

                            navController.navigate(Routes.Login.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }

        composable(
            route = "${Routes.RegisterPhysiotherapist.route}/{idUsuario}"
        ) { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {
                RegisterPhysiotherapistScreen(
                    onBack = {
                        navController.popBackStack()
                    },

                    onRegistrationSuccess = {
                        navController.popBackStack()
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
                        coroutineScope.launch {
                            sessionManager.cerrarSesion()

                            navController.navigate(Routes.Login.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }

        composable(
            route = "${Routes.ManageExerciseVideos.route}/{idUsuario}"
        ) { backStackEntry ->

            val idUsuario = backStackEntry.arguments
                ?.getString("idUsuario")
                ?.toIntOrNull()

            if (idUsuario != null) {
                ManageExerciseVideosScreen(
                    idUsuario = idUsuario,
                    onProfileClick = {
                        navController.navigate(
                            "${Routes.PhysiotherapistProfile.route}/$idUsuario"
                        ) {
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