package com.rehabtrack.app.ui.screens.physiotherapist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rehabtrack.app.ui.viewmodel.PatientDetailViewModel
import com.rehabtrack.app.ui.viewmodel.PatientDetailUiState

private val DetailBlue = Color(0xFF1976D2)
private val DetailText = Color(0xFF263238)
private val DetailSecondary = Color(0xFF64748B)
private val DetailBorder = Color(0xFFCBD5E1)
private val DetailLightBlue = Color(0xFFE3F2FD)
private val DetailGreen = Color(0xFF2E7D32)
private val DetailGreenBackground = Color(0xFFDCFCE7)

@Composable
fun PatientDetailScreen(
    navController: NavController,
    idUsuario: Int,
    idPaciente: Int,
    patientDetailViewModel: PatientDetailViewModel = viewModel()
) {
    val uiState by patientDetailViewModel.uiState.collectAsState()

    LaunchedEffect(idUsuario, idPaciente) {
        patientDetailViewModel.cargarPaciente(
            idUsuario = idUsuario,
            idPaciente = idPaciente
        )
    }

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
                    .background(DetailBlue)
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
                    text = "Detalle del paciente",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            // Contenido según el estado del ViewModel
            when (val state = uiState) {

                PatientDetailUiState.Idle,
                PatientDetailUiState.Loading -> {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = DetailBlue
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Cargando información del paciente...",
                            color = DetailSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                is PatientDetailUiState.Error -> {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.mensaje,
                            color = DetailText,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is PatientDetailUiState.Success -> {

                    val paciente = state.paciente

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Spacer(modifier = Modifier.height(25.dp))

                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Foto del paciente",
                            tint = DetailBlue,
                            modifier = Modifier
                                .size(58.dp)
                                .background(
                                    DetailLightBlue,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(5.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = paciente.nombre,
                            color = DetailText,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(9.dp))

                        Text(
                            text = "Paciente asignado",
                            color = DetailGreen,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .background(
                                    DetailGreenBackground,
                                    RoundedCornerShape(50.dp)
                                )
                                .padding(
                                    horizontal = 10.dp,
                                    vertical = 5.dp
                                )
                        )

                        Spacer(modifier = Modifier.height(30.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Información del paciente",
                                color = DetailText,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            PatientInfoItem(
                                label = "Fecha de nacimiento",
                                value = paciente.fechaNacimiento
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            PatientInfoItem(
                                label = "Teléfono",
                                value = paciente.telefono
                                    ?.takeIf { it.isNotBlank() }
                                    ?: "No registrado"
                            )
                        }

                        Spacer(modifier = Modifier.height(25.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = DetailBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Programa de rehabilitación",
                                color = DetailText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(7.dp))

                            if (state.programa == null) {

                                Text(
                                    text = "Todavía no hay un programa registrado para este paciente.",
                                    color = DetailSecondary,
                                    fontSize = 12.sp
                                )

                            } else {

                                Column {
                                    Text(
                                        text = state.programa.nombre,
                                        color = DetailText,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(7.dp))

                                    Text(
                                        text = "Duración: ${state.programa.duracionSesiones} sesiones",
                                        color = DetailSecondary,
                                        fontSize = 12.sp
                                    )

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Text(
                                        text = "Estado: ${state.programa.estado}",
                                        color = DetailSecondary,
                                        fontSize = 12.sp
                                    )

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Text(
                                        text = "Fecha de creación: ${state.programa.fechaCreacion}",
                                        color = DetailSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            Button(
                                onClick = {
                                    navController.navigate(
                                        "${com.rehabtrack.app.navigation.Routes.ManageProgram.route}/$idUsuario/$idPaciente"
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(43.dp),
                                shape = RoundedCornerShape(9.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DetailBlue
                                )
                            ) {
                                Text(
                                    text = if (state.programa == null) {
                                        "Crear programa"
                                    } else {
                                        "Gestionar programa"
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(25.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(92.dp)
            .background(
                DetailLightBlue,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 4.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            color = DetailText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = label,
            color = DetailBlue,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PatientInfoItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = DetailBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = label,
            color = DetailSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = value,
            color = DetailText,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}