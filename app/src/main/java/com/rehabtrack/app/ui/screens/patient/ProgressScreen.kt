package com.rehabtrack.app.ui.screens.patient

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rehabtrack.app.ui.components.PatientBottomBar
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.rehabtrack.app.ui.viewmodel.ProgressUiState
import androidx.compose.material3.CircularProgressIndicator
import com.rehabtrack.app.ui.viewmodel.PainProgressPoint

@Composable
fun ProgressScreen(
    navController: NavController,
    idUsuario: Int,
    uiState: ProgressUiState
) {
    val colors = MaterialTheme.colorScheme
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            PatientBottomBar(
                navController = navController,
                currentRoute = currentRoute,
                idUsuario = idUsuario
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(colors.background)
        ) {
            // Encabezado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primary)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RehabTrack",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Progreso",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    uiState.cargando -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(40.dp)
                        )
                    }

                    uiState.mensajeError != null -> {
                        Text(
                            text = uiState.mensajeError,
                            color = colors.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }

                    uiState.nombrePrograma.isBlank() -> {
                        Text(
                            text = "No tienes un programa de rehabilitación activo.",
                            color = colors.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    }

                    else -> {
                        Text(
                            text = "Resumen del programa",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = uiState.nombrePrograma,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onBackground
                        )

                        Spacer(modifier = Modifier.height(26.dp))

                        Text(
                            text = "Programa completado",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${uiState.porcentajeCompletado}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onBackground,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // Barra de progreso dinámica
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(
                                    colors.outlineVariant,
                                    RoundedCornerShape(4.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(
                                        uiState.porcentajeCompletado / 100f
                                    )
                                    .height(4.dp)
                                    .background(
                                        colors.primary,
                                        RoundedCornerShape(4.dp)
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(36.dp))

                        // Estadísticas
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "${uiState.sesionesCompletadas} / ${uiState.totalSesiones}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = colors.onBackground,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Sesiones\ncompletadas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onBackground,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = uiState.ejerciciosCompletados.toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = colors.onBackground,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Ejercicios completados",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onBackground,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(34.dp))

                        Text(
                            text = "Evolución del dolor",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        if (uiState.evolucionDolor.isEmpty()) {
                            Text(
                                text = "Todavía no hay registros de dolor.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onBackground
                            )
                        } else {
                            PainProgressChart(
                                values = uiState.evolucionDolor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(155.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PainProgressChart(
    values: List<PainProgressPoint>,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .width(28.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                listOf("10", "8", "6", "4", "2", "0").forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp)
            ) {
                val left = 8.dp.toPx()
                val top = 4.dp.toPx()
                val right = size.width - 4.dp.toPx()
                val bottom = size.height - 4.dp.toPx()

                val axisColor = colors.onBackground
                val lineColor = colors.primary

                // Ejes
                drawLine(
                    color = axisColor,
                    start = Offset(left, top),
                    end = Offset(left, bottom),
                    strokeWidth = 1.dp.toPx()
                )

                drawLine(
                    color = axisColor,
                    start = Offset(left, bottom),
                    end = Offset(right, bottom),
                    strokeWidth = 1.dp.toPx()
                )

                // Marcas del eje vertical
                for (i in 0..5) {
                    val y = top + (bottom - top) * i / 5f

                    drawLine(
                        color = axisColor,
                        start = Offset(left - 5.dp.toPx(), y),
                        end = Offset(left + 5.dp.toPx(), y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Posiciones de los registros
                val points = values.mapIndexed { index, point ->
                    val x = if (values.size == 1) {
                        (left + right) / 2f
                    } else {
                        left + (right - left) * index /
                                (values.size - 1).toFloat()
                    }

                    val y = bottom -
                            (point.nivelDolor / 10f) * (bottom - top)

                    Offset(x, y)
                }

                // Líneas entre registros
                for (i in 0 until points.lastIndex) {
                    drawLine(
                        color = lineColor,
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Puntos
                points.forEach { point ->
                    drawCircle(
                        color = lineColor,
                        radius = 3.dp.toPx(),
                        center = point
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            values.forEach { point ->
                Text(
                    text = point.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onBackground,
                    maxLines = 1
                )
            }
        }
    }
}