package com.rehabtrack.app.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rehabtrack.app.ui.components.PatientBottomBar
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rehabtrack.app.ui.viewmodel.HomeViewModel
import com.rehabtrack.app.ui.viewmodel.HomeUiState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.style.TextAlign
import com.rehabtrack.app.navigation.Routes
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    idUsuario: Int
) {
    val colors = MaterialTheme.colorScheme
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val viewModel: HomeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    val formatoFechaHora = SimpleDateFormat(
        "yyyy-MM-dd HH:mm",
        Locale.US
    )

    val ahora = Date()

    val puedeComenzar = (uiState as? HomeUiState.Success)
        ?.proximaSesion
        ?.let { sesion ->
            sesion.estado == "Pendiente" &&
                    runCatching {
                        val fechaSesion = formatoFechaHora.parse(
                            "${sesion.fecha} ${sesion.hora}"
                        )
                        fechaSesion != null && !fechaSesion.after(ahora)
                    }.getOrDefault(false)
        } ?: false

    LaunchedEffect(idUsuario) {
        viewModel.cargarInicio(idUsuario)
    }

    Scaffold(
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            PatientBottomBar(
                navController = navController,
                currentRoute = currentRoute
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

            when (val estado = uiState) {

                HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cargando información...")
                    }
                }

                is HomeUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = estado.mensaje,
                            color = colors.error
                        )
                    }
                }

                is HomeUiState.Success -> {

                    Column(
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 18.dp
                        )
                    ) {
                        // Saludo
                        Text(
                            text = "Hola, ${estado.paciente.nombre} 👋",
                            style = MaterialTheme.typography.titleLarge,
                            color = colors.onBackground,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Continúa con tu rehabilitación",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(26.dp))

                        // Programa
                        Text(
                            text = "Tu programa",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        Text(
                            text = estado.programa?.nombre
                                ?: "Aún no tienes un programa asignado",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (estado.programa != null) {
                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Programa actual",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.onBackground
                            )
                        }

                        // Progreso
                        Spacer(modifier = Modifier.height(42.dp))

                        Text(
                            text = "Progreso",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Tu avance aparecerá aquí cuando registremos tus sesiones.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )

                        // Próxima sesión
                        Spacer(modifier = Modifier.height(26.dp))

                        Text(
                            text = "Próxima sesión",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        if (estado.proximaSesion != null) {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = colors.surface
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 2.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp)
                                ) {
                                    Text(
                                        text = "Sesión programada",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = colors.onSurface,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "Fecha: ${estado.proximaSesion.fecha}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = colors.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Hora: ${estado.proximaSesion.hora}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = colors.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "Estado: ${estado.proximaSesion.estado}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            navController.navigate(
                                                "${Routes.SessionConfirmation.route}/${estado.proximaSesion.idSesion}"
                                            )
                                        },
                                        enabled = puedeComenzar,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = colors.primary
                                        )
                                    ) {
                                        Text("Comenzar sesión")
                                    }
                                }
                            }

                        } else {

                            Text(
                                text = "No tienes próximas sesiones programadas.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Resumen
                        Spacer(modifier = Modifier.height(26.dp))

                        Text(
                            text = "Resumen",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = "Todavía no hay sesiones registradas.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.onSurfaceVariant
                        )

                    }
                }
            }
        }
    }
}
