package com.rehabtrack.app.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rehabtrack.app.ui.components.RehabTextField
import com.rehabtrack.app.ui.viewmodel.AuthState
import com.rehabtrack.app.ui.viewmodel.AuthViewModel
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.LocalActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import com.rehabtrack.app.util.BackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun AdminSetupScreen(
    onSetupSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var confirmarPasswordError by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    val context = LocalContext.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()

    var cargandoRestauracion by remember {
        mutableStateOf(false)
    }

    var mensajeRestauracion by remember {
        mutableStateOf("")
    }

    var directorioPreparado by remember {
        mutableStateOf<File?>(null)
    }

    var mostrarConfirmacionRestauracion by remember {
        mutableStateOf(false)
    }

    val selectorRestauracion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->

        if (uri != null) {
            scope.launch {
                cargandoRestauracion = true
                mensajeRestauracion = ""

                try {
                    // Copiar el ZIP al almacenamiento temporal privado.
                    val archivoZip = withContext(Dispatchers.IO) {
                        val archivo = File(
                            context.cacheDir,
                            "respaldo_seleccionado_${System.currentTimeMillis()}.zip"
                        )

                        try {
                            val input = context.contentResolver
                                .openInputStream(uri)
                                ?: throw Exception(
                                    "No se pudo leer el archivo seleccionado."
                                )

                            input.use { entrada ->
                                archivo.outputStream().use { salida ->
                                    entrada.copyTo(salida)
                                }
                            }

                            archivo
                        } catch (e: Exception) {
                            archivo.delete()
                            throw e
                        }
                    }

                    try {
                        // Validar y preparar el respaldo.
                        val directorio = BackupManager.prepararRestauracion(
                            archivoZip = archivoZip,
                            context = context
                        )

                        directorioPreparado = directorio

                        mensajeRestauracion =
                            "Respaldo válido. Está listo para restaurarse."

                    } finally {
                        archivoZip.delete()
                    }

                } catch (e: Exception) {
                    directorioPreparado = null
                    mensajeRestauracion =
                        "No se pudo validar el respaldo: ${e.message}"
                } finally {
                    cargandoRestauracion = false
                }
            }
        } else {
            mensajeRestauracion = "No se seleccionó ningún respaldo."
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.RegistroExitoso) {
            authViewModel.limpiarEstado()
            onSetupSuccess()
        }
    }

    Scaffold(
        contentWindowInsets =
            androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "RehabTrack",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Configuración inicial",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Crea la cuenta del administrador principal para comenzar a utilizar RehabTrack.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            RehabTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    authViewModel.limpiarEstado()
                },
                label = "Correo electrónico",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            RehabTextField(
                value = password,
                onValueChange = {
                    password = it
                    confirmarPasswordError = false
                    authViewModel.limpiarEstado()
                },
                label = "Contraseña",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            RehabTextField(
                value = confirmarPassword,
                onValueChange = {
                    confirmarPassword = it
                    confirmarPasswordError = false
                },
                label = "Confirmar contraseña",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (confirmarPasswordError) {
                Text(
                    text = "Las contraseñas no coinciden.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "La contraseña debe tener entre 8 y 12 caracteres, incluyendo mayúsculas, minúsculas, números y símbolos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            when (val state = authState) {
                is AuthState.Error -> {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = state.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                AuthState.Loading -> {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Creando cuenta administrativa...",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> Unit
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    confirmarPasswordError =
                        password != confirmarPassword ||
                                confirmarPassword.isBlank()

                    if (
                        correo.isNotBlank() &&
                        password.isNotBlank() &&
                        !confirmarPasswordError
                    ) {
                        authViewModel.inicializarAdministrador(
                            correo = correo.trim(),
                            password = password
                        )
                    }
                },
                enabled = authState != AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Crear cuenta administrativa",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Ya tienes una copia de seguridad?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {
                    selectorRestauracion.launch(
                        arrayOf(
                            "application/zip",
                            "application/x-zip-compressed",
                            "application/octet-stream"
                        )
                    )
                },
                enabled = !cargandoRestauracion &&
                        authState != AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Restaurar desde respaldo")
            }

            if (cargandoRestauracion) {
                Spacer(modifier = Modifier.height(12.dp))

                CircularProgressIndicator()

                Text(
                    text = "Validando respaldo...",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (mensajeRestauracion.isNotEmpty()) {
                Text(
                    text = mensajeRestauracion,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (directorioPreparado != null)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (directorioPreparado != null) {
                Button(
                    onClick = {
                        mostrarConfirmacionRestauracion = true
                    },
                    enabled = !cargandoRestauracion,
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Text("Confirmar restauración")
                }
            }
        }
        if (mostrarConfirmacionRestauracion) {
            AlertDialog(
                onDismissRequest = {
                    if (!cargandoRestauracion) {
                        mostrarConfirmacionRestauracion = false
                    }
                },
                title = {
                    Text("Confirmar restauración")
                },
                text = {
                    Text(
                        "Se restaurarán los pacientes, sesiones, ejercicios, " +
                                "registros, fotografías y cuentas administrativas " +
                                "incluidos en el respaldo. Esta operación sustituirá " +
                                "los datos actuales de RehabTrack."
                    )
                },
                confirmButton = {
                    TextButton(
                        enabled = !cargandoRestauracion,
                        onClick = {
                            val directorio = directorioPreparado

                            if (directorio != null) {
                                scope.launch {
                                    cargandoRestauracion = true
                                    mensajeRestauracion = "Restaurando datos..."

                                    try {
                                        withContext(Dispatchers.IO) {
                                            BackupManager.aplicarRestauracion(
                                                context = context,
                                                directorioPreparado = directorio
                                            )
                                        }

                                        mensajeRestauracion =
                                            "Restauración aplicada. Reiniciando RehabTrack..."

                                        val intent = context.packageManager
                                            .getLaunchIntentForPackage(
                                                context.packageName
                                            )
                                            ?: throw IllegalStateException(
                                                "No se pudo obtener el inicio de RehabTrack."
                                            )

                                        intent.addFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK or
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        )

                                        context.startActivity(intent)
                                        activity?.finish()

                                    } catch (e: Exception) {
                                        cargandoRestauracion = false

                                        mensajeRestauracion =
                                            "Error durante la restauración: ${e.message}"
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Restaurar")
                    }
                },
                dismissButton = {
                    TextButton(
                        enabled = !cargandoRestauracion,
                        onClick = {
                            mostrarConfirmacionRestauracion = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}