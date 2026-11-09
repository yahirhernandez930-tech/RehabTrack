package com.rehabtrack.app.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.PaddingValues
import com.rehabtrack.app.navigation.Routes
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import com.rehabtrack.app.data.local.entity.RegistroSerieEntity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator

private val RehabBlueLight = Color(0xFFE3F2FD)

@Composable
fun ExerciseExecutionScreen(
    navController: NavController,
    idSesion: Int,
    indiceEjercicio: Int,
    idEjercicio: Int,
    nombreEjercicio: String,
    totalSeries: Int,
    repeticiones: Int,
    registros: List<RegistroSerieEntity>,
    cargandoProgreso: Boolean,
    guardandoSerie: Boolean,
    mensajeError: String?,
    onRegistrarSerie: (Int, Int) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    var repeticionesActuales by remember {
        mutableIntStateOf(0)
    }

    val registrosValidos = registros.filter { registro ->
        registro.numeroSerie in 1..totalSeries &&
                registro.repeticionesRealizadas > 0
    }

    val completedSeries = registrosValidos
        .map { it.numeroSerie }
        .toSet()

    val repeticionesPorSerie = registrosValidos.associate {
        it.numeroSerie to it.repeticionesRealizadas
    }

    val currentSeries = (1..totalSeries)
        .firstOrNull { it !in completedSeries }
        ?: totalSeries

    val exerciseFinished = totalSeries > 0 &&
            (1..totalSeries).all { it in completedSeries }

    // Reiniciamos el contador al pasar a la siguiente serie.
    LaunchedEffect(currentSeries) {
        repeticionesActuales = 0
    }

    Scaffold(
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (exerciseFinished && !cargandoProgreso) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.background,
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(
                                horizontal = 24.dp,
                                vertical = 12.dp
                            )
                    ) {
                        Button(
                            onClick = {
                                navController.navigate(
                                    "${Routes.ExerciseConfirmation.route}/$idSesion/$indiceEjercicio"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary
                            )
                        ) {
                            Text(
                                text = "Completar ejercicio",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
        ) {
            // Encabezado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primary)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(54.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = colors.onPrimary
                    )
                }

                Text(
                    text = "Realización del ejercicio",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 8.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = nombreEjercicio,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )

                Text(
                    text = "$repeticiones repeticiones por serie",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (cargandoProgreso) {
                    CircularProgressIndicator()
                }

                mensajeError?.let { error ->
                    Text(
                        text = error,
                        color = colors.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Tarjetas de las tres series
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (series in 1.. totalSeries) {
                        val isCompleted = series in completedSeries
                        val isCurrent = series == currentSeries &&
                                !exerciseFinished && !isCompleted
                        val isLocked = !isCompleted && !isCurrent

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(102.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isCurrent || isCompleted) {
                                        RehabBlueLight
                                    } else {
                                        colors.surfaceVariant
                                    }
                                )
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Serie $series",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (isLocked) {
                                    colors.onSurfaceVariant
                                } else {
                                    colors.onBackground
                                },
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            when {
                                isCompleted -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Completada",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colors.primary
                                        )

                                        Text(
                                            text = "${repeticionesPorSerie[series] ?: 0}/$repeticiones",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.primary
                                        )
                                    }
                                }

                                isCurrent -> {
                                    Button(
                                        onClick = {
                                            onRegistrarSerie(
                                                currentSeries,
                                                repeticionesActuales
                                            )
                                        },
                                        enabled = repeticionesActuales > 0 &&
                                                !guardandoSerie &&
                                                !cargandoProgreso,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(34.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = colors.primary
                                        )
                                    ) {
                                        if (guardandoSerie) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                                color = colors.onPrimary
                                            )
                                        } else {
                                            Text(
                                                text = "Registrar",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }

                                else -> {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Serie bloqueada",
                                        modifier = Modifier.size(20.dp),
                                        tint = colors.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Bloqueada",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colors.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Contador de repeticiones de la serie actual
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Repeticiones de la serie $currentSeries",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(28.dp)
                    ) {
                        Button(
                            onClick = {
                                repeticionesActuales =
                                    (repeticionesActuales - 1).coerceAtLeast(0)
                            },
                            enabled = repeticionesActuales > 0 &&
                                    !guardandoSerie &&
                                    !cargandoProgreso &&
                                    !exerciseFinished,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary
                            )
                        ) {
                            Text("−")
                        }

                        Text(
                            text = "$repeticionesActuales / $repeticiones",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackground
                        )

                        Button(
                            onClick = {
                                repeticionesActuales =
                                    (repeticionesActuales + 1).coerceAtMost(repeticiones)
                            },
                            enabled = repeticionesActuales < repeticiones &&
                                    !guardandoSerie &&
                                    !cargandoProgreso &&
                                    !exerciseFinished,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary
                            )
                        ) {
                            Text("+")
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Repeticiones realizadas",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Evidencia fotográfica
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Evidencia fotográfica",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(width = 180.dp, height = 150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sin fotografía",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            // Próximo paso: conectar la cámara del teléfono.
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(180.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary
                        )
                    ) {
                        Text("Tomar fotografía")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!cargandoProgreso &&
                    !exerciseFinished &&
                    completedSeries.size < totalSeries
                ) {
                    Text(
                        text = "Registra la serie ${currentSeries} para continuar",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
