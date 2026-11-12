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
import com.rehabtrack.app.data.local.entity.SesionEntity
import androidx.compose.foundation.layout.navigationBarsPadding
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import java.util.Locale
import androidx.compose.material3.RadioButton

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
                            .navigationBarsPadding()
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

                        // Sección de sesiones de rehabilitación
                        Spacer(modifier = Modifier.height(25.dp))

                        SesionSection(
                            sesiones = state.sesiones,
                            programaActivo = state.programa != null,
                            onProgramarSesion = { fecha, hora ->
                                patientDetailViewModel.programarSesion(
                                    fecha = fecha,
                                    hora = hora
                                )
                            },
                            onActualizarEstado = { idSesion, nuevoEstado ->
                                patientDetailViewModel.actualizarEstadoSesion(
                                    idSesion = idSesion,
                                    nuevoEstado = nuevoEstado
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(25.dp))

                        Button(
                            onClick = {
                                navController.navigate(
                                    "${com.rehabtrack.app.navigation.Routes.History.route}/patient/$idPaciente"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(45.dp),
                            shape = RoundedCornerShape(9.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DetailBlue
                            )
                        ) {
                            Text(
                                text = "Consultar historial de sesiones",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
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


@Composable
private fun SesionSection(
    sesiones: List<SesionEntity>,
    programaActivo: Boolean,
    onProgramarSesion: (String, String) -> Unit,
    onActualizarEstado: (Int, String) -> Unit
) {
    var mostrarDialogo by remember {
        mutableStateOf(false)
    }

    var sesionSeleccionada by remember {
        mutableStateOf<SesionEntity?>(null)
    }

    val totalSesiones = sesiones.size

    val sesionesFinalizadas = sesiones.count {
        it.estado.equals("Finalizada", ignoreCase = true)
    }

    val sesionesPendientes = sesiones.count {
        it.estado.equals("Pendiente", ignoreCase = true)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sesiones de rehabilitación",
            color = DetailText,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatisticCard(
                value = totalSesiones.toString(),
                label = "Total",
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                value = sesionesFinalizadas.toString(),
                label = "Finalizadas",
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                value = sesionesPendientes.toString(),
                label = "Pendientes",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!programaActivo) {
            Text(
                text = "Primero debes registrar un programa de rehabilitación.",
                color = DetailSecondary,
                fontSize = 13.sp
            )
        } else {
            Button(
                onClick = {
                    mostrarDialogo = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shape = RoundedCornerShape(9.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DetailBlue
                )
            ) {
                Text(
                    text = "Programar sesión",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            if (sesiones.isEmpty()) {
                Text(
                    text = "Todavía no hay sesiones programadas para este paciente.",
                    color = DetailSecondary,
                    fontSize = 13.sp
                )
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    sesiones.forEach { sesion ->
                        SesionItem(
                            sesion = sesion,
                            onGestionar = {
                                sesionSeleccionada = sesion
                            }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        SesionProgrammingDialog(
            onDismiss = {
                mostrarDialogo = false
            },
            onConfirm = { fecha, hora ->
                onProgramarSesion(fecha, hora)
                mostrarDialogo = false
            }
        )
    }

    sesionSeleccionada?.let { sesion ->
        SesionManagementDialog(
            sesion = sesion,
            onDismiss = {
                sesionSeleccionada = null
            },
            onConfirm = { nuevoEstado ->
                onActualizarEstado(
                    sesion.idSesion,
                    nuevoEstado
                )
                sesionSeleccionada = null
            }
        )
    }
}

@Composable
private fun SesionItem(
    sesion: SesionEntity,
    onGestionar: () -> Unit
) {
    val estadoColor = when (sesion.estado.lowercase()) {
        "finalizada" -> DetailGreen
        "pendiente" -> Color(0xFFB45309)
        "cancelada" -> Color(0xFFDC2626)
        "no asistió" -> Color(0xFFDC2626)
        else -> DetailSecondary
    }

    val estadoBackground = when (sesion.estado.lowercase()) {
        "finalizada" -> DetailGreenBackground
        "pendiente" -> Color(0xFFFEF3C7)
        "cancelada" -> Color(0xFFFEE2E2)
        "no asistió" -> Color(0xFFFEE2E2)
        else -> DetailLightBlue
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = DetailBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(13.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sesión ${sesion.idSesion}",
                color = DetailText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = sesion.estado,
                color = estadoColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .background(
                        estadoBackground,
                        RoundedCornerShape(50.dp)
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    )
            )
        }

        Spacer(modifier = Modifier.height(9.dp))

        Text(
            text = "Fecha: ${sesion.fecha}",
            color = DetailSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Hora: ${sesion.hora}",
            color = DetailSecondary,
            fontSize = 12.sp
        )

        if (sesion.nivelDolor != null) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Nivel de dolor: ${sesion.nivelDolor}/10",
                color = DetailSecondary,
                fontSize = 12.sp
            )
        }

        if (!sesion.observaciones.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Observaciones: ${sesion.observaciones}",
                color = DetailSecondary,
                fontSize = 12.sp
            )
        }

        if (sesion.estado == "Pendiente") {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onGestionar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gestionar sesión")
            }
        }
    }
}

@Composable
private fun SesionProgrammingDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    val context = LocalContext.current

    var fechaSeleccionada by remember {
        mutableStateOf("")
    }

    var horaSeleccionada by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Programar sesión",
                fontWeight = FontWeight.Bold,
                color = DetailText
            )
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "Selecciona la fecha y hora de la sesión.",
                    color = DetailSecondary,
                    fontSize = 13.sp
                )

                // Selector de fecha
                OutlinedButton(
                    onClick = {

                        val calendario = Calendar.getInstance()

                        DatePickerDialog(
                            context,
                            { _, year, month, day ->

                                fechaSeleccionada = String.format(
                                    Locale.US,
                                    "%04d-%02d-%02d",
                                    year,
                                    month + 1,
                                    day
                                )
                            },
                            calendario.get(Calendar.YEAR),
                            calendario.get(Calendar.MONTH),
                            calendario.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            datePicker.minDate =
                                Calendar.getInstance().timeInMillis
                        }.show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (fechaSeleccionada.isEmpty()) {
                            "Seleccionar fecha"
                        } else {
                            fechaSeleccionada
                        }
                    )
                }

                // Selector de hora
                OutlinedButton(
                    onClick = {

                        val calendario = Calendar.getInstance()

                        TimePickerDialog(
                            context,
                            { _, hour, minute ->

                                horaSeleccionada = String.format(
                                    Locale.US,
                                    "%02d:%02d",
                                    hour,
                                    minute
                                )
                            },
                            calendario.get(Calendar.HOUR_OF_DAY),
                            calendario.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (horaSeleccionada.isEmpty()) {
                            "Seleccionar hora"
                        } else {
                            horaSeleccionada
                        }
                    )
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        fechaSeleccionada,
                        horaSeleccionada
                    )
                },
                enabled = fechaSeleccionada.isNotEmpty() &&
                        horaSeleccionada.isNotEmpty()
            ) {
                Text("Programar")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
private fun SesionManagementDialog(
    sesion: SesionEntity,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var estadoSeleccionado by remember(sesion.idSesion) {
        mutableStateOf("")
    }

    val opciones = listOf(
        "Finalizada",
        "No asistió",
        "Cancelada"
    )

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Gestionar sesión ${sesion.idSesion}",
                color = DetailText,
                fontWeight = FontWeight.Bold
            )
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Fecha: ${sesion.fecha}\nHora: ${sesion.hora}",
                    color = DetailSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Selecciona el nuevo estado:",
                    color = DetailText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                opciones.forEach { opcion ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = estadoSeleccionado == opcion,
                            onClick = {
                                estadoSeleccionado = opcion
                            }
                        )

                        Text(
                            text = opcion,
                            color = DetailText,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(estadoSeleccionado)
                },
                enabled = estadoSeleccionado.isNotEmpty()
            ) {
                Text("Guardar cambios")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}