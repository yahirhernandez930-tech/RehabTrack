package com.rehabtrack.app.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rehabtrack.app.navigation.Routes
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.navigationBarsPadding

@Composable
fun ExerciseInfoScreen(
    navController: NavController,
    nombreEjercicio: String,
    descripcion: String,
    instrucciones: String,
    series: Int,
    repeticiones: Int,
    idSesion: Int,
    indiceEjercicio: Int
) {
    val colors = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
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
                    text = "Información del ejercicio",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            // Contenido desplazable
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp)
            ) {

                Spacer(modifier = Modifier.height(28.dp))

                // Nombre del ejercicio
                Text(
                    text = nombreEjercicio,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Espacio para el video demostrativo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFEAF3FC)),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = colors.primary
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = colors.onPrimary,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Video demostrativo",
                            style = MaterialTheme.typography.titleSmall,
                            color = colors.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Disponible próximamente",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Descripción del ejercicio
                Text(
                    text = "Descripción del ejercicio",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = descripcion.ifBlank {
                        "La descripción de este ejercicio estará disponible próximamente."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onBackground
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Cómo realizarlo
                Text(
                    text = "Cómo realizarlo",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = instrucciones.ifBlank {
                        "Las indicaciones para realizar este ejercicio estarán disponibles próximamente."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Resumen de series y repeticiones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEAF3FC))
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Series",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = series.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.onSecondaryContainer
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Repeticiones",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = repeticiones.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Botón para iniciar el ejercicio
                Button(
                    onClick = {
                        navController.navigate(
                            "${Routes.Exercise.route}/$idSesion/$indiceEjercicio"
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.85f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary
                    )
                ) {
                    Text("Iniciar ejercicio")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}