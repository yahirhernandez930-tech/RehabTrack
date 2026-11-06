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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rehabtrack.app.ui.components.PhysiotherapistBottomBar
import com.rehabtrack.app.ui.viewmodel.PatientsViewModel
import com.rehabtrack.app.ui.viewmodel.PatientsUiState

private val RehabBlue = Color(0xFF1976D2)
private val RehabText = Color(0xFF263238)
private val RehabSecondary = Color(0xFF64748B)
private val RehabBorder = Color(0xFFCBD5E1)
private val RehabGreen = Color(0xFF2E7D32)
private val RehabOrange = Color(0xFFF9A825)

private data class PatientItem(
    val idPaciente: Int,
    val name: String,
    val fechaNacimiento: String
)

@Composable
fun PatientsScreen(
    idUsuario: Int,
    onHomeClick: () -> Unit = {},
    onPatientsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onPatientClick: (Int) -> Unit = {},
    onSelectExistingPatientClick: () -> Unit = {},
    onRegisterNewPatientClick: () -> Unit = {},
    patientsViewModel: PatientsViewModel = viewModel()
) {
    var searchText by remember { mutableStateOf("") }

    var mostrarDialogoAgregar by remember { mutableStateOf(false)}

    val uiState by patientsViewModel.uiState.collectAsState()

    LaunchedEffect(idUsuario) {
        patientsViewModel.cargarPacientes(idUsuario)
    }

    val pacientesReales = (uiState as? PatientsUiState.Success)
        ?.pacientes
        .orEmpty()

    val patients = pacientesReales.map { paciente ->
        PatientItem(
            idPaciente = paciente.idPaciente,
            name = paciente.nombre,
            fechaNacimiento = paciente.fechaNacimiento
        )
    }

    val filteredPatients = patients.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0,0,0,0),
        bottomBar = {
            PhysiotherapistBottomBar(
                selectedItem = "patients",
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

            // Encabezado
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
                    text = "Mis pacientes",
                    color = RehabText,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(22.dp))

                // Buscador
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(47.dp)
                        .border(1.dp, RehabBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = RehabSecondary,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    BasicTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = RehabText,
                            fontSize = 14.sp
                        ),
                        decorationBox = { innerTextField ->
                            if (searchText.isEmpty()) {
                                Text(
                                    text = "Buscar paciente",
                                    color = RehabSecondary,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(25.dp))

                // Contador y botón para agregar pacientes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pacientes asignados",
                            color = RehabText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "${patients.size} pacientes",
                            color = RehabSecondary,
                            fontSize = 13.sp
                        )
                    }

                    Row(
                        modifier = Modifier
                            .background(
                                RehabBlue,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable{
                                mostrarDialogoAgregar = true
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Agregar paciente",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            text = "Agregar",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (mostrarDialogoAgregar) {
                        AlertDialog(
                            onDismissRequest = {
                                mostrarDialogoAgregar = false
                            },
                            title = {
                                Text("Agregar paciente")
                            },
                            text = {
                                Text(
                                    "¿Cómo deseas agregar al paciente?"
                                )
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        mostrarDialogoAgregar = false
                                        onSelectExistingPatientClick()
                                    }
                                ) {
                                    Text("Paciente existente")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        mostrarDialogoAgregar = false
                                        onRegisterNewPatientClick()
                                    }
                                ) {
                                    Text("Registrar nuevo")
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(25.dp))

                // Lista de pacientes
                when (val state = uiState) {

                    PatientsUiState.Idle,
                    PatientsUiState.Loading -> {
                        Text(
                            text = "Cargando pacientes...",
                            color = RehabSecondary,
                            fontSize = 14.sp
                        )
                    }

                    is PatientsUiState.Error -> {
                        Text(
                            text = state.mensaje,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }

                    is PatientsUiState.Success -> {

                        if (filteredPatients.isEmpty()) {
                            Text(
                                text = if (searchText.isBlank()) {
                                    "Todavía no tienes pacientes asignados."
                                } else {
                                    "No se encontraron pacientes."
                                },
                                color = RehabSecondary,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            filteredPatients.forEach { patient ->

                                PatientListCard(
                                    patient = patient,
                                    onClick = {
                                        onPatientClick(patient.idPaciente)
                                    }
                                )

                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun PatientListCard(
    patient: PatientItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                RehabBorder,
                RoundedCornerShape(13.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = patient.name,
                color = RehabText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(7.dp))

            Text(
                text = "Nacimiento: ${patient.fechaNacimiento}",
                color = RehabSecondary,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(9.dp))

            Text(
                text = "Asignación activa",
                color = RehabGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Ver paciente",
            tint = Color(0xFF222222),
            modifier = Modifier.size(28.dp)
        )
    }
}