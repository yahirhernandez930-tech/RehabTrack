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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.rehabtrack.app.ui.components.PhysiotherapistBottomBar

private val RehabBlue = Color(0xFF1976D2)
private val RehabText = Color(0xFF263238)
private val RehabSecondary = Color(0xFF64748B)
private val RehabBorder = Color(0xFFCBD5E1)
private val RehabLightBlue = Color(0xFFE3F2FD)
private val RehabGreen = Color(0xFF2E7D32)
private val RehabOrange = Color(0xFFF9A825)

private data class PatientPreview(
    val name: String,
    val treatment: String,
    val status: String,
    val needsReview: Boolean
)

@Composable
fun PhysiotherapistHomeScreen(
    onPatientClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onPatientsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }

    val patients = remember {
        listOf(
            PatientPreview(
                name = "María López",
                treatment = "Rehabilitación de rodilla",
                status = "En seguimiento",
                needsReview = false
            ),
            PatientPreview(
                name = "Carlos Pérez",
                treatment = "Rehabilitación de rodilla",
                status = "Revisar progreso",
                needsReview = true
            )
        )
    }

    val filteredPatients = patients.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            PhysiotherapistBottomBar(
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
                    text = "Gestiona la rehabilitación de tus pacientes.",
                    color = RehabText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(29.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(47.dp)
                        .border(1.dp, RehabBorder)
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
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(37.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "Pacientes asignados",
                        value = "12",
                        modifier = Modifier.weight(1f)
                    )

                    SummaryCard(
                        title = "Requieren revisión",
                        value = "3",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(27.dp))

                Text(
                    text = "Mis pacientes",
                    color = RehabText,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(30.dp))

                if (filteredPatients.isEmpty()) {
                    Text(
                        text = "No se encontraron pacientes.",
                        color = RehabSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    filteredPatients.forEach { patient ->
                        PatientCard(
                            patient = patient,
                            onClick = { onPatientClick(patient.name) }
                        )
                        Spacer(Modifier.height(22.dp))
                    }
                }

                Spacer(Modifier.height(10.dp))
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

@Composable
private fun PatientCard(
    patient: PatientPreview,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, RehabBorder, RoundedCornerShape(13.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patient.name,
                    color = RehabText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(7.dp))

                Text(
                    text = patient.treatment,
                    color = RehabSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(9.dp))

                Text(
                    text = patient.status,
                    color = if (patient.needsReview) RehabOrange else RehabGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver paciente",
                tint = Color(0xFF222222),
                modifier = Modifier.size(30.dp)
            )
        }
    }
}