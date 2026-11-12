package com.rehabtrack.app.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rehabtrack.app.ui.viewmodel.SessionDetailUiState
import com.rehabtrack.app.data.local.entity.RegistroSerieEntity

private val SessionBlue = Color(0xFF1976D2)
private val SessionText = Color(0xFF263238)
private val SessionSecondary = Color(0xFF64748B)
private val SessionBorder = Color(0xFFCBD5E1)
private val SessionLightBlue = Color(0xFFE3F2FD)

@Composable
fun SessionDetailScreen(
    navController: NavController,
    uiState: SessionDetailUiState
) {

    if (uiState.cargando) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = SessionBlue)
        }
        return
    }

    if (uiState.mensajeError != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.mensajeError,
                    color = SessionText,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { navController.popBackStack() }
                ) {
                    Text("Regresar")
                }
            }
        }
        return
    }

    val sesion = uiState.sesion ?: return

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {

            // Encabezado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SessionBlue)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(54.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Detalle de sesión",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(48.dp))
            }

            // Contenido
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp)
                    .navigationBarsPadding()
            ) {

                Spacer(Modifier.height(22.dp))

                // Información del paciente
                Text(
                    text = uiState.nombrePaciente,
                    color = SessionText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    text = "Sesión del ${sesion.fecha}",
                    color = SessionSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(22.dp))

                // Indicadores de la sesión
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SessionStatisticCard(
                        label = "Dolor final",
                        value = "${sesion.nivelDolor ?: "--"}/10",
                        modifier = Modifier.weight(1f)
                    )

                    SessionStatisticCard(
                        label = "Estado",
                        value = sesion.estado,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Ejercicios realizados
                if (uiState.ejercicios.isEmpty()) {
                    Text(
                        text = "No hay ejercicios registrados en esta sesión.",
                        color = SessionSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    uiState.ejercicios.forEach { ejercicio ->

                        ExerciseDetailCard(
                            exercise = ejercicio.nombre,
                            details = "${ejercicio.registros.size} de " +
                                    "${ejercicio.seriesAsignadas} series registradas",
                            registros = ejercicio.registros
                        )

                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionStatisticCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    secondaryText: String? = null
) {
    Column(
        modifier = modifier
            .height(96.dp)
            .background(
                SessionLightBlue,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 5.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            color = SessionBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = value,
            color = SessionText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        if (secondaryText != null) {
            Spacer(Modifier.height(3.dp))

            Text(
                text = secondaryText,
                color = SessionSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ExerciseDetailCard(
    exercise: String,
    details: String,
    registros: List<RegistroSerieEntity>
) {
    val totalRepeticiones = registros.sumOf {
        it.repeticionesRealizadas
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = SessionBorder,
                shape = RoundedCornerShape(9.dp)
            )
            .padding(horizontal = 13.dp, vertical = 11.dp)
    ) {
        Text(
            text = exercise,
            color = SessionText,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = details,
            color = SessionSecondary,
            fontSize = 11.sp
        )

        Spacer(Modifier.height(10.dp))

        registros.sortedBy { it.numeroSerie }.forEach { registro ->

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Serie ${registro.numeroSerie}",
                    color = SessionSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = "${registro.repeticionesRealizadas} repeticiones",
                    color = SessionText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        HorizontalDivider(color = SessionBorder)

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total realizado",
                color = SessionText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "$totalRepeticiones repeticiones",
                color = SessionBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}