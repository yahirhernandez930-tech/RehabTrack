package com.rehabtrack.app.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rehabtrack.app.navigation.Routes
import com.rehabtrack.app.ui.viewmodel.SessionCompletionUiState

@Composable
fun SessionCompletionScreen(
    navController: NavController,
    idSesion: Int,
    uiState: SessionCompletionUiState,
    onFinalizarSesion: (Int, String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    var nivelDolor by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    var observaciones by rememberSaveable {
        mutableStateOf("")
    }

    val sesionCargada = uiState.sesion?.idSesion == idSesion

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
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = colors.onPrimary
                    )
                }

                Text(
                    text = "Finalización de sesión",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            // Contenido
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(36.dp))

                if (uiState.cargando) {

                    CircularProgressIndicator()

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Cargando información de la sesión...")

                } else if (uiState.finalizada) {

                    Spacer(modifier = Modifier.height(50.dp))

                    Text(
                        text = "¡Sesión completada!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "El nivel de dolor y las observaciones se guardaron correctamente.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    Button(
                        onClick = {
                            val regresoExitoso = navController.popBackStack(
                                "home/{idUsuario}",
                                inclusive = false
                            )

                            if (!regresoExitoso) {
                                navController.navigate(Routes.Login.route) {
                                    popUpTo(Routes.Login.route) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Volver al inicio")
                    }

                } else if (sesionCargada) {

                    Text(
                        text = "¡Has completado todos los ejercicios!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Antes de finalizar, queremos conocer cómo te sentiste durante la sesión.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Nivel de dolor
                    Text(
                        text = "¿Qué nivel de dolor experimentaste?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = nivelDolor?.toString() ?: "—",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )

                    Slider(
                        value = (nivelDolor ?: 0).toFloat(),
                        onValueChange = { nivelDolor = it.toInt() },
                        valueRange = 0f..10f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "0 - Sin dolor",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )

                        Text(
                            text = "10 - Máximo dolor",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Observaciones
                    Text(
                        text = "Observaciones (opcional)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = observaciones,
                        onValueChange = { observaciones = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        placeholder = {
                            Text("Describe cómo te sentiste durante la sesión...")
                        },
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5
                    )

                    if (uiState.mensajeError != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = uiState.mensajeError,
                            color = colors.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            nivelDolor?.let { nivelSeleccionado ->
                                onFinalizarSesion(
                                    nivelSeleccionado,
                                    observaciones
                                )
                            }
                        },
                        enabled = nivelDolor != null && !uiState.guardando,
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.guardando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = colors.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Finalizar sesión")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}