package com.rehabtrack.app.ui.screens.physiotherapist

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rehabtrack.app.data.local.entity.EjercicioEntity
import com.rehabtrack.app.ui.viewmodel.ManageExerciseVideosViewModel
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Snackbar
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon

private val RehabBlue = Color(0xFF1976D2)
private val RehabText = Color(0xFF263238)
private val RehabSecondary = Color(0xFF64748B)
private val RehabGreen = Color(0xFF2E7D32)
private val RehabRed = Color(0xFFD32F2F)

@Composable
fun ManageExerciseVideosScreen(
    idUsuario: Int,
    onProfileClick: () -> Unit = {},
    videosViewModel: ManageExerciseVideosViewModel = viewModel()
) {
    val uiState by videosViewModel.uiState.collectAsState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var ejercicioSeleccionado by remember {
        mutableStateOf<EjercicioEntity?>(null)
    }

    LaunchedEffect(
        uiState.mensajeExito,
        uiState.mensajeError
    ) {
        val mensaje = uiState.mensajeError
            ?: uiState.mensajeExito

        if (mensaje != null) {

            // Cerrar el diálogo solamente si el guardado fue exitoso.
            if (uiState.mensajeExito != null) {
                ejercicioSeleccionado = null
            }

            snackbarHostState.showSnackbar(mensaje)

            videosViewModel.limpiarMensajes()
        }
    }

    LaunchedEffect(Unit) {
        videosViewModel.cargarEjercicios()
    }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = Color(0xFF263238),
                    contentColor = Color.White
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RehabBlue)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(62.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onProfileClick
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar al perfil",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Administrar videos",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Videos demostrativos",
                    color = RehabText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Administra los videos asociados a cada ejercicio.",
                    color = RehabSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(Modifier.height(18.dp))

                Text(
                    text = "Ejercicios registrados: ${uiState.ejercicios.size}",
                    color = RehabSecondary,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(12.dp))

                if (uiState.cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = RehabBlue
                    )
                } else if (uiState.ejercicios.isEmpty()) {
                    Text(
                        text = "No hay ejercicios registrados en el catálogo.",
                        color = RehabSecondary
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.ejercicios,
                            key = { it.idEjercicio }
                        ) { ejercicio ->
                            ExerciseVideoCard(
                                ejercicio = ejercicio,
                                onEditClick = {
                                    ejercicioSeleccionado = ejercicio
                                    videosViewModel.limpiarMensajes()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    ejercicioSeleccionado?.let { ejercicio ->
        EditExerciseVideoDialog(
            ejercicio = ejercicio,
            guardando = uiState.guardando,
            onDismiss = {
                if (!uiState.guardando) {
                    ejercicioSeleccionado = null
                    videosViewModel.limpiarMensajes()
                }
            },
            onSave = { url ->
                videosViewModel.actualizarVideo(
                    idEjercicio = ejercicio.idEjercicio,
                    nuevaUrl = url
                )
            }
        )
    }
}

@Composable
private fun ExerciseVideoCard(
    ejercicio: EjercicioEntity,
    onEditClick: () -> Unit
) {
    val tieneVideo = !ejercicio.videoUrl.isNullOrBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = ejercicio.nombre,
            color = RehabText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = if (tieneVideo) "Video configurado" else "Sin video asignado",
            color = if (tieneVideo) RehabGreen else RehabSecondary,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = RehabBlue
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null
            )
            Spacer(Modifier.padding(horizontal = 4.dp))
            Text(
                text = if (tieneVideo) "Editar video" else "Asignar video"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditExerciseVideoDialog(
    ejercicio: EjercicioEntity,
    guardando: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val context = LocalContext.current

    var url by remember(ejercicio.idEjercicio) {
        mutableStateOf(ejercicio.videoUrl.orEmpty())
    }

    var mensajePrueba by remember(ejercicio.idEjercicio) {
        mutableStateOf<String?>(null)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Video del ejercicio")
        },
        text = {
            Column {
                Text(
                    text = ejercicio.nombre,
                    color = RehabText,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        mensajePrueba = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("URL del video")
                    },
                    placeholder = {
                        Text("https://...")
                    },
                    singleLine = true,
                    enabled = !guardando
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Los enlaces de YouTube se abrirán en la aplicación o navegador. Los videos MP4 o HLS compatibles se reproducirán dentro de RehabTrack.",
                    color = RehabSecondary,
                    style = MaterialTheme.typography.bodySmall
                )


                if (url.startsWith("https://") || url.startsWith("http://")) {
                    TextButton(
                        onClick = {
                            try {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(url.trim())
                                )
                                context.startActivity(intent)

                                mensajePrueba =
                                    "Enlace enviado al navegador o aplicación compatible."
                            } catch (e: android.content.ActivityNotFoundException) {
                                mensajePrueba =
                                    "No se encontró una aplicación para abrir este enlace."
                            }
                        },
                        enabled = !guardando
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Text("Probar enlace")
                    }
                }

                mensajePrueba?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = RehabGreen,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(
                            start = 8.dp,
                            top = 2.dp,
                            bottom = 6.dp
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(url)
                },
                enabled = !guardando
            ) {
                if (guardando) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar")
                }
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = {
                        onSave("")
                    },
                    enabled = !guardando && !ejercicio.videoUrl.isNullOrBlank()
                ) {
                    Text(
                        text = "Quitar video",
                        color = RehabRed
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    enabled = !guardando
                ) {
                    Text("Cancelar")
                }
            }
        }
    )
}