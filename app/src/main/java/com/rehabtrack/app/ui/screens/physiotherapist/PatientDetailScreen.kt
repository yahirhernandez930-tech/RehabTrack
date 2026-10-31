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
    patientName: String = "María López",
    treatment: String = "Rehabilitación de rodilla",
    status: String = "En seguimiento",
    progress: Int = 58,
    completedSessions: Int = 7,
    plannedSessions: Int = 12,
    averagePain: Int = 3,
    onManageProgram: () -> Unit = {},
    onViewHistory: () -> Unit = {}
) {
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

            // Contenido
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
                        .background(DetailLightBlue, RoundedCornerShape(12.dp))
                        .padding(5.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = patientName,
                    color = DetailText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = treatment,
                    color = Color.Black,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(9.dp))

                Text(
                    text = status,
                    color = DetailGreen,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .background(
                            DetailGreenBackground,
                            RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Progreso del programa
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Progreso del programa",
                        color = DetailText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Programa completado",
                            color = DetailText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "$progress%",
                            color = DetailBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { (progress.coerceIn(0, 100)) / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = DetailBlue,
                        trackColor = Color(0xFFCBD5E1),
                        drawStopIndicator = {}
                    )
                }

                Spacer(modifier = Modifier.height(29.dp))

                // Indicadores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatisticCard(
                        value = "$completedSessions/$plannedSessions",
                        label = "Sesiones completadas",
                        modifier = Modifier.weight(1f)
                    )

                    StatisticCard(
                        value = "$averagePain/10",
                        label = "Dolor promedio",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Programa actual
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Programa actual",
                        color = DetailText,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = DetailBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Text(
                            text = treatment,
                            color = DetailText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = "$plannedSessions sesiones planificadas",
                            color = DetailSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Acciones
                Button(
                    onClick = onManageProgram,
                    modifier = Modifier
                        .fillMaxWidth(0.68f)
                        .height(43.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DetailBlue
                    )
                ) {
                    Text(
                        text = "Gestionar programa",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onViewHistory,
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(43.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DetailBlue
                    )
                ) {
                    Text(
                        text = "Consultar historial de sesiones",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))
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
