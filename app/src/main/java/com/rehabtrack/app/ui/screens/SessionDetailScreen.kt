package com.rehabtrack.app.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding

private data class CompletedExercise(
    val name: String,
    val details: String
)

private val exampleExercises = listOf(
    CompletedExercise(
        name = "Sentadilla asistida",
        details = "3 series · 10 repeticiones"
    ),
    CompletedExercise(
        name = "Flexión de rodilla",
        details = "3 series · 8 repeticiones"
    ),
    CompletedExercise(
        name = "Extensión de rodilla",
        details = "2 series · 10 repeticiones"
    )
)

@Composable
fun SessionDetailScreen(
    navController: NavController
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colors.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primary)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Volver al historial",
                        tint = colors.onPrimary
                    )
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Detalle de sesión",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onPrimary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

                // Equilibra el espacio ocupado por la flecha.
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Sesión del 30 septiembre 2026",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = colors.onBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Fortalecimiento de rodilla",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                color = colors.onBackground,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Resumen de la sesión
            Text(
                text = "Resumen de la sesión",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Duración",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onBackground,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "20 min",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dolor inicial",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "4/10",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dolor final",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "3/10",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Ejercicios realizados
            Text(
                text = "Ejercicios realizados",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            exampleExercises.forEach { exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "✓",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Column {
                        Text(
                            text = exercise.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = exercise.details,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Evidencia fotográfica
            Text(
                text = "Evidencia de la sesión",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                EvidencePlaceholder()
            }
        }
    }
}

@Composable
private fun EvidencePlaceholder() {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(width = 118.dp, height = 118.dp)
            .background(
                color = colors.background,
                shape = RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val stroke = 2.5.dp.toPx()
            val left = stroke / 2
            val top = stroke / 2
            val right = size.width - stroke / 2
            val bottom = size.height - stroke / 2

            // Marco de la imagen.
            drawRoundRect(
                color = Color(0xFF222222),
                topLeft = androidx.compose.ui.geometry.Offset(left, top),
                size = androidx.compose.ui.geometry.Size(
                    right - left,
                    bottom - top
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                    14.dp.toPx(),
                    14.dp.toPx()
                ),
                style = androidx.compose.ui.graphics.drawscope.Stroke(stroke)
            )

            // Sol.
            drawCircle(
                color = Color(0xFF222222),
                radius = 9.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(
                    size.width * 0.31f,
                    size.height * 0.31f
                ),
                style = androidx.compose.ui.graphics.drawscope.Stroke(stroke)
            )

            // Montañas.
            val mountain = androidx.compose.ui.graphics.Path().apply {
                moveTo(size.width * 0.12f, size.height * 0.87f)
                lineTo(size.width * 0.72f, size.height * 0.39f)
                lineTo(size.width * 0.98f, size.height * 0.66f)
            }

            drawPath(
                path = mountain,
                color = Color(0xFF222222),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = stroke,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                )
            )
        }
    }
}
