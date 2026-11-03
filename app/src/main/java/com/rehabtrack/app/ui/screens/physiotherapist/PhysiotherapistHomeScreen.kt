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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rehabtrack.app.ui.components.PhysiotherapistBottomBar

private val RehabBlue = Color(0xFF1976D2)
private val RehabText = Color(0xFF263238)
private val RehabSecondary = Color(0xFF64748B)
private val RehabBorder = Color(0xFFCBD5E1)
private val RehabLightBlue = Color(0xFFE3F2FD)
private val RehabOrange = Color(0xFFF9A825)

private data class UpcomingSession(
    val patientName: String,
    val treatment: String,
    val time: String
)

private data class PatientReview(
    val patientName: String,
    val treatment: String,
    val reason: String
)

@Composable
fun PhysiotherapistHomeScreen(
    onPatientClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onPatientsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {

    val upcomingSessions = remember {
        listOf(
            UpcomingSession(
                patientName = "María López",
                treatment = "Rehabilitación de rodilla",
                time = "10:00 AM"
            ),
            UpcomingSession(
                patientName = "Carlos Pérez",
                treatment = "Rehabilitación de rodilla",
                time = "11:30 AM"
            ),
            UpcomingSession(
                patientName = "Ana Martínez",
                treatment = "Rehabilitación de hombro",
                time = "01:00 PM"
            )
        )
    }

    val patientsToReview = remember {
        listOf(
            PatientReview(
                patientName = "Carlos Pérez",
                treatment = "Rehabilitación de rodilla",
                reason = "Revisar progreso"
            ),
            PatientReview(
                patientName = "José Hernández",
                treatment = "Rehabilitación lumbar",
                reason = "Seguimiento pendiente"
            ),
            PatientReview(
                patientName = "Ana Martínez",
                treatment = "Rehabilitación de hombro",
                reason = "Actualizar evaluación"
            )
        )
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

                Spacer(Modifier.width(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        title = "Pacientes activos",
                        value = "12",
                        modifier = Modifier.weight(1f)
                    )

                    SummaryCard(
                        title = "Sesiones de hoy",
                        value = "4",
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
                        value = patientsToReview.size.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    SummaryCard(
                        title = "Programas activos",
                        value = "8",
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

                if (upcomingSessions.isEmpty()) {
                    Text(
                        text = "No tienes sesiones programadas.",
                        color = RehabSecondary,
                        fontSize = 14.sp
                    )
                } else {
                    upcomingSessions.forEach { session ->
                        UpcomingSessionCard(
                            session = session,
                            onClick = {
                                onPatientClick(session.patientName)
                            }
                        )
                        Spacer(Modifier.height(12.dp))
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
                        text = "${patientsToReview.size} pendientes",
                        color = RehabOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(14.dp))

                if (patientsToReview.isEmpty()) {
                    Text(
                        text = "No tienes revisiones pendientes.",
                        color = RehabSecondary,
                        fontSize = 14.sp
                    )
                } else {
                    patientsToReview.forEach { patient ->
                        PatientReviewCard(
                            patient = patient,
                            onClick = {
                                onPatientClick(patient.patientName)
                            }
                        )

                        Spacer(Modifier.height(12.dp))
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

@Composable
private fun UpcomingSessionCard(
    session: UpcomingSession,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                RehabBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.patientName,
                color = RehabText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = session.treatment,
                color = RehabSecondary,
                fontSize = 13.sp
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = session.time,
                color = RehabBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(5.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver detalles",
                tint = RehabText,
                modifier = Modifier.size(23.dp)
            )
        }
    }
}

@Composable
private fun PatientReviewCard(
    patient: PatientReview,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                RehabBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Text(
            text = patient.patientName,
            color = RehabText,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = patient.treatment,
            color = RehabSecondary,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = patient.reason,
            color = RehabOrange,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}