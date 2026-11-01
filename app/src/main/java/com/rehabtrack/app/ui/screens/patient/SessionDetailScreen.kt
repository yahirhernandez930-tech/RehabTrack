package com.rehabtrack.app.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import com.rehabtrack.app.R

private val SessionBlue = Color(0xFF1976D2)
private val SessionText = Color(0xFF263238)
private val SessionSecondary = Color(0xFF64748B)
private val SessionBorder = Color(0xFFCBD5E1)
private val SessionLightBlue = Color(0xFFE3F2FD)

private data class CompletedExercise(
    val name: String,
    val details: String
)

private val exampleExercises = listOf(
    CompletedExercise(
        "Sentadilla asistida",
        "3 series · 10 repeticiones"
    ),
    CompletedExercise(
        "Flexión de rodilla",
        "3 series · 8 repeticiones"
    ),
    CompletedExercise(
        "Extensión de rodilla",
        "2 series · 10 repeticiones"
    )
)

@Composable
fun SessionDetailScreen(
    navController: NavController,
    patientName: String = "María López"
) {

    val showEvidence = remember { mutableStateOf(false) }

    if (showEvidence.value) {
        AlertDialog(
            onDismissRequest = {
                showEvidence.value = false
            },
            title = {
                Text("Sentadilla asistida")
            },
            text = {
                Image(
                    painter = painterResource(
                        id = R.drawable.evidencia_sentadilla
                    ),
                    contentDescription = "Fotografía ampliada de sentadilla asistida",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp),
                    contentScale = ContentScale.Fit
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEvidence.value = false
                    }
                ) {
                    Text("Cerrar")
                }
            }
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
                    text = patientName,
                    color = SessionText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    text = "Sesión del 30 de septiembre de 2026",
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
                        label = "Duración",
                        value = "20 min",
                        modifier = Modifier.weight(1f)
                    )

                    SessionStatisticCard(
                        label = "Dolor final",
                        value = "3/10",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Ejercicios realizados
                Text(
                    text = "Ejercicios realizados",
                    color = SessionText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                exampleExercises.forEach { exercise ->
                    ExerciseDetailCard(exercise)

                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(12.dp))

                // Evidencia fotográfica
                Text(
                    text = "Evidencia fotográfica",
                    color = SessionText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(14.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Sentadilla asistida",
                        color = SessionText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(10.dp))

                    Image(
                        painter = painterResource(
                            id = R.drawable.evidencia_sentadilla
                        ),
                        contentDescription = "Evidencia de sentadilla asistida",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                showEvidence.value = true
                            },
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.height(8.dp))

                    TextButton(
                        onClick = {
                            showEvidence.value = true
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Ver evidencia fotográfica")
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
    exercise: CompletedExercise
) {
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
            text = exercise.name,
            color = SessionText,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = exercise.details,
            color = SessionSecondary,
            fontSize = 11.sp
        )
    }
}