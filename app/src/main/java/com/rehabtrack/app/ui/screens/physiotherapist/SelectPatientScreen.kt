package com.rehabtrack.app.ui.screens.physiotherapist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rehabtrack.app.data.local.entity.PacienteEntity
import com.rehabtrack.app.ui.viewmodel.SelectPatientViewModel

private val SelectBlue = Color(0xFF1976D2)
private val SelectText = Color(0xFF263238)
private val SelectSecondary = Color(0xFF64748B)
private val SelectBorder = Color(0xFFCBD5E1)

@Composable
fun SelectPatientScreen(
    idUsuario: Int,
    onBackClick: () -> Unit,
    selectPatientViewModel: SelectPatientViewModel = viewModel()
) {
    val uiState by selectPatientViewModel.uiState.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var selectedPatient by remember {
        mutableStateOf<PacienteEntity?>(null)
    }

    LaunchedEffect(Unit) {
        selectPatientViewModel.cargarPacientes()
    }

    val filteredPatients = uiState.pacientes.filter {
        it.nombre.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SelectBlue)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(62.dp)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color.White,
                    modifier = Modifier
                        .size(27.dp)
                        .clickable(onClick = onBackClick)
                )

                Spacer(Modifier.width(18.dp))

                Text(
                    text = "Seleccionar paciente",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = {
                        selectedPatient?.let {
                            selectPatientViewModel.asignarPaciente(
                                idUsuario = idUsuario,
                                idPaciente = it.idPaciente
                            )
                            selectedPatient = null
                        }
                    },
                    enabled = selectedPatient != null &&
                            !uiState.asignando &&
                            !uiState.cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SelectBlue
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (uiState.asignando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Asignar paciente")
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 22.dp)
        ) {
            Spacer(Modifier.height(25.dp))

            Text(
                text = "Pacientes disponibles",
                color = SelectText,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Selecciona un paciente para asignarlo a tu seguimiento.",
                color = SelectSecondary,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(22.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(47.dp)
                    .border(
                        1.dp,
                        SelectBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = SelectSecondary
                )

                Spacer(Modifier.width(10.dp))

                BasicTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        selectedPatient = null
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = SelectText,
                        fontSize = 14.sp
                    ),
                    decorationBox = { innerTextField ->
                        if (searchText.isEmpty()) {
                            Text(
                                "Buscar paciente",
                                color = SelectSecondary
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(22.dp))

            Text(
                text = "${uiState.pacientes.size} pacientes disponibles",
                color = SelectSecondary,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(12.dp))

            when {
                uiState.cargando -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SelectBlue)
                    }
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                filteredPatients.isEmpty() -> {
                    Text(
                        text = if (searchText.isBlank()) {
                            "No hay pacientes disponibles."
                        } else {
                            "No se encontraron pacientes."
                        },
                        color = SelectSecondary,
                        fontSize = 14.sp
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = filteredPatients,
                            key = { it.idPaciente }
                        ) { patient ->

                            PatientSelectionCard(
                                patient = patient,
                                selected = selectedPatient?.idPaciente ==
                                        patient.idPaciente,
                                onClick = {
                                    selectedPatient = patient
                                    selectPatientViewModel.limpiarMensajes()
                                }
                            )
                        }
                    }
                }
            }

            uiState.mensaje?.let {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = it,
                    color = Color(0xFF2E7D32),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun PatientSelectionCard(
    patient: PacienteEntity,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) SelectBlue else SelectBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = SelectBlue,
            modifier = Modifier.size(30.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = patient.nombre,
                color = SelectText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Nacimiento: ${patient.fechaNacimiento}",
                color = SelectSecondary,
                fontSize = 12.sp
            )
        }

        if (selected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Seleccionado",
                tint = SelectBlue
            )
        }
    }
}