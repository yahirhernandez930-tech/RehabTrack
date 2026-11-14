package com.rehabtrack.app.ui.screens.physiotherapist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rehabtrack.app.ui.components.PhysiotherapistBottomBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rehabtrack.app.ui.viewmodel.PhysiotherapistHomeViewModel
import com.rehabtrack.app.ui.viewmodel.PhysiotherapistHomeUiState

private val RehabBlue = Color(0xFF1976D2)
private val RehabText = Color(0xFF263238)
private val RehabSecondary = Color(0xFF64748B)
private val RehabBorder = Color(0xFFCBD5E1)
private val RehabLightBlue = Color(0xFFE3F2FD)
private val RehabOrange = Color(0xFFF9A825)

@Composable
fun PhysiotherapistHomeScreen(
    idUsuario: Int,
    onPatientClick: (Int) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onPatientsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    homeViewModel: PhysiotherapistHomeViewModel = viewModel()
) {

    val uiState by homeViewModel.uiState.collectAsState()
    val resumen = uiState as? PhysiotherapistHomeUiState.Success

    LaunchedEffect(idUsuario) {
        homeViewModel.cargarResumen(idUsuario)
    }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            PhysiotherapistBottomBar(
                selectedItem = "home",
                onHomeClick = onHomeClick,
                onPatientsClick = onPatientsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RehabBlue)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(62.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RehabTrack",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                Spacer(Modifier.height(28.dp))

                Text(
                    text = "Hola, fisioterapeuta",
                    color = RehabText,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    text = "Este es el resumen de tu actividad.",
                    color = RehabSecondary,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(28.dp))

                // Resumen general
                Text(
                    text = "Resumen general",
                    color = RehabText,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "Pacientes activos",
                        value = when (uiState) {
                            is PhysiotherapistHomeUiState.Success ->
                                resumen?.pacientesActivos?.toString() ?: "0"

                            PhysiotherapistHomeUiState.Loading,
                            PhysiotherapistHomeUiState.Idle -> "..."

                            is PhysiotherapistHomeUiState.Error -> "—"
                        },
                        modifier = Modifier.weight(1f)
                    )

                    SummaryCard(
                        title = "Sesiones de hoy",
                        value = when (uiState) {
                            is PhysiotherapistHomeUiState.Success ->
                                resumen?.sesionesHoy?.toString() ?: "0"

                            PhysiotherapistHomeUiState.Loading,
                            PhysiotherapistHomeUiState.Idle -> "..."

                            is PhysiotherapistHomeUiState.Error -> "—"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "Requieren revisión",
                        value = when (uiState) {
                            is PhysiotherapistHomeUiState.Success ->
                                resumen?.pacientesRevision?.size?.toString() ?: "0"

                            PhysiotherapistHomeUiState.Loading,
                            PhysiotherapistHomeUiState.Idle -> "..."

                            is PhysiotherapistHomeUiState.Error -> "—"
                        },
                        modifier = Modifier.weight(1f)
                    )

                    SummaryCard(
                        title = "Programas activos",
                        value = when (uiState) {
                            is PhysiotherapistHomeUiState.Success ->
                                resumen?.programasActivos?.toString() ?: "0"

                            PhysiotherapistHomeUiState.Loading,
                            PhysiotherapistHomeUiState.Idle -> "..."

                            is PhysiotherapistHomeUiState.Error -> "—"
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(28.dp))

                // Próximas sesiones
                Text(
                    text = "Próximas sesiones",
                    color = RehabText,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(14.dp))

                Spacer(Modifier.height(14.dp))

                when (uiState) {

                    is PhysiotherapistHomeUiState.Success -> {

                        val sesiones = resumen?.proximasSesiones.orEmpty()

                        if (sesiones.isEmpty()) {
                            Text(
                                text = "No tienes sesiones programadas.",
                                color = RehabSecondary,
                                fontSize = 14.sp
                            )
                        } else {
                            sesiones.forEachIndexed { index, sesion ->

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            1.dp,
                                            RehabBorder,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            onPatientClick(sesion.idPaciente)
                                        }
                                        .padding(14.dp)
                                ) {
                                    Text(
                                        text = sesion.nombrePaciente,
                                        color = RehabText,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(Modifier.height(5.dp))

                                    Text(
                                        text = sesion.nombrePrograma,
                                        color = RehabSecondary,
                                        fontSize = 13.sp
                                    )

                                    Spacer(Modifier.height(5.dp))

                                    Text(
                                        text = "${sesion.fecha} · ${sesion.hora}",
                                        color = RehabBlue,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                if (index < sesiones.lastIndex) {
                                    Spacer(Modifier.height(10.dp))
                                }
                            }
                        }
                    }

                    PhysiotherapistHomeUiState.Loading,
                    PhysiotherapistHomeUiState.Idle -> {
                        Text(
                            text = "Cargando sesiones...",
                            color = RehabSecondary,
                            fontSize = 14.sp
                        )
                    }

                    is PhysiotherapistHomeUiState.Error -> {
                        Text(
                            text = "No se pudieron cargar las sesiones.",
                            color = RehabSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Pacientes que requieren atención
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Atención pendiente",
                        color = RehabText,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = when (uiState) {
                            is PhysiotherapistHomeUiState.Success ->
                                if (resumen?.pacientesRevision.isNullOrEmpty()) {
                                    "Sin registros"
                                } else {
                                    "${resumen?.pacientesRevision?.size} pacientes"
                                }

                            PhysiotherapistHomeUiState.Loading,
                            PhysiotherapistHomeUiState.Idle -> "Cargando..."

                            is PhysiotherapistHomeUiState.Error -> "No disponible"
                        },
                        color = RehabSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(14.dp))

                when (uiState) {

                    is PhysiotherapistHomeUiState.Success -> {

                        val pacientes = resumen?.pacientesRevision.orEmpty()

                        if (pacientes.isEmpty()) {
                            Text(
                                text = "No hay pacientes pendientes de revisión.",
                                color = RehabSecondary,
                                fontSize = 14.sp
                            )
                        } else {
                            pacientes.forEach { paciente ->

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            1.dp,
                                            RehabBorder,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            onPatientClick(paciente.idPaciente)
                                        }
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = paciente.nombrePaciente,
                                            color = RehabText,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Spacer(Modifier.height(4.dp))

                                        Text(
                                            text = "Última sesión: ${paciente.fechaSesion}",
                                            color = RehabSecondary,
                                            fontSize = 12.sp
                                        )
                                    }

                                    Text(
                                        text = "${paciente.nivelDolor}/10",
                                        color = RehabOrange,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }

                    PhysiotherapistHomeUiState.Loading,
                    PhysiotherapistHomeUiState.Idle -> {
                        Text(
                            text = "Cargando pacientes...",
                            color = RehabSecondary,
                            fontSize = 14.sp
                        )
                    }

                    is PhysiotherapistHomeUiState.Error -> {
                        Text(
                            text = "No se pudo cargar la información.",
                            color = RehabSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Acceso a la lista completa
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            RehabBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(onClick = onPatientsClick)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Gestionar pacientes",
                            color = RehabText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "Consultar todos tus pacientes asignados",
                            color = RehabSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Ver pacientes",
                        tint = RehabBlue,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(100.dp)
            .background(RehabLightBlue, RoundedCornerShape(13.dp))
            .padding(horizontal = 5.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = RehabBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )

        Text(
            text = value,
            color = RehabText,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )
    }
}