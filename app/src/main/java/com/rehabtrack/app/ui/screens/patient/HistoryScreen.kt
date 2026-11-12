package com.rehabtrack.app.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rehabtrack.app.navigation.Routes
import com.rehabtrack.app.ui.components.PatientBottomBar
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.CircularProgressIndicator
import com.rehabtrack.app.ui.viewmodel.HistoryViewModel
import com.rehabtrack.app.ui.viewmodel.HistorySessionItem

@Composable
fun HistoryScreen(
    navController: NavController,
    idUsuario: Int? = null,
    idPaciente: Int? = null
) {
    val colors = MaterialTheme.colorScheme

    val historyViewModel: HistoryViewModel = viewModel()

    val uiState by historyViewModel.uiState.collectAsState()

    LaunchedEffect(idUsuario, idPaciente) {
        historyViewModel.cargarHistorial(
            idUsuario = idUsuario,
            idPaciente = idPaciente
        )
    }

    val isPhysioterapist = idPaciente != null
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if(!isPhysioterapist && idUsuario != null) {
                PatientBottomBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    idUsuario = idUsuario
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(
                    if (isPhysioterapist) {
                        Modifier.navigationBarsPadding()
                    } else {
                        Modifier
                    }
                )
                .verticalScroll(rememberScrollState())
                .background(colors.background)
        ) {
            // Encabezado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primary)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPhysioterapist) {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = colors.onPrimary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }

                Text(
                    text = "RehabTrack",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isPhysioterapist) {
                        "Historial de sesiones"
                    } else {
                        "Historial"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Sesiones recientes",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onBackground,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(40.dp))

                when {
                    uiState.cargando -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    uiState.mensajeError != null -> {
                        Text(
                            text = uiState.mensajeError ?: "Error al cargar el historial",
                            color = colors.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    uiState.sesiones.isEmpty() -> {
                        Text(
                            text = "Todavía no hay sesiones registradas.",
                            color = colors.onBackground,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    else -> {
                        uiState.sesiones.forEachIndexed { index, session ->
                            HistorySessionItem(
                                session = session,
                                onViewMore = {
                                    navController.navigate(
                                        "${Routes.SessionDetail.route}/${session.sesion.idSesion}"
                                    )
                                }
                            )

                            if (index < uiState.sesiones.lastIndex) {
                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HistorySessionItem(
    session: HistorySessionItem,
    onViewMore: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val sesion = session.sesion

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "${sesion.fecha} · ${sesion.hora}",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = session.nombrePrograma,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onBackground,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Estado: ${sesion.estado}",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sesion.nivelDolor?.let {
                    "Dolor: $it/10"
                } ?: "Dolor: No registrado",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onBackground
            )

            Text(
                text = "Ver más",
                style = MaterialTheme.typography.labelLarge,
                color = colors.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    onViewMore()
                }
            )
        }
    }
}
