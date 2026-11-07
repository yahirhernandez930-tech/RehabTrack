package com.rehabtrack.app.ui.screens.patient

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.rehabtrack.app.ui.viewmodel.NextSessionUiState

@Composable
fun NextSessionScreen(
    navController: NavController,
    uiState: NextSessionUiState,
    onStartSession: () -> Unit
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
            // Encabezado azul
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

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Próxima sesión",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.size(48.dp))
            }

            // Contenido desplazable
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                when (val estado = uiState) {

                    NextSessionUiState.Loading -> {
                        Text(
                            text = "Cargando información de la sesión...",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = colors.onSurfaceVariant
                        )
                    }

                    is NextSessionUiState.Error -> {
                        Text(
                            text = estado.mensaje,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = colors.error
                        )
                    }

                    is NextSessionUiState.Success -> {

                        val sesion = estado.sesion

                        Text(
                            text = "Sesión ${sesion.idSesion}",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = colors.onBackground,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${sesion.fecha} · ${sesion.hora}",
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onBackground,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE3F2FD)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = colors.primary
                                    )

                                    Text(
                                        text = "Sesión ${sesion.estado}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = colors.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Fecha programada: ${sesion.fecha} a las ${sesion.hora}.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Botón inferior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(top = 0.dp, bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onStartSession,
                    enabled = uiState is NextSessionUiState.Success,
                    modifier = Modifier
                        .fillMaxWidth(0.68f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary
                    )
                ) {
                    Text("Comenzar")
                }
            }
        }
    }
}

@Composable
private fun ExercisePreviewCard(
    name: String,
    series: Int,
    repetitions: Int
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        border = BorderStroke(
            1.dp,
            colors.outline
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Series: $series\nRepeticiones: $repetitions",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface
            )
        }
    }
}
