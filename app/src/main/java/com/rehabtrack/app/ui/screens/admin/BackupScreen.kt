package com.rehabtrack.app.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rehabtrack.app.data.local.RehabDatabase
import com.rehabtrack.app.util.BackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import android.content.Intent
import androidx.activity.compose.LocalActivity

@Composable
fun BackupScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current

    val database = remember {
        RehabDatabase.getDatabase(context)
    }

    val scope = rememberCoroutineScope()

    var cargando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    var archivoPendiente by remember {
        mutableStateOf<File?>(null)
    }

    var directorioPreparado by remember {
        mutableStateOf<File?>(null)
    }

    var mostrarConfirmacion by remember {
        mutableStateOf(false)
    }

    // Selector para guardar respaldos.
    val selectorRespaldo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri: Uri? ->

        val archivo = archivoPendiente

        if (uri != null && archivo != null) {
            scope.launch {
                cargando = true

                try {
                    withContext(Dispatchers.IO) {
                        val salida = context.contentResolver
                            .openOutputStream(uri)
                            ?: throw Exception(
                                "No se pudo abrir el archivo de destino."
                            )

                        salida.use { output ->
                            archivo.inputStream().use { input ->
                                input.copyTo(output)
                            }
                        }
                    }

                    mensaje = "Respaldo guardado correctamente."
                    archivo.delete()

                } catch (e: Exception) {
                    mensaje = "Error al guardar el respaldo: ${e.message}"
                } finally {
                    cargando = false
                    archivoPendiente = null
                }
            }
        } else {
            mensaje = "Guardado cancelado."
            archivoPendiente = null
        }
    }

    // Selector para elegir un respaldo existente.
    val selectorRestauracion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->

        if (uri != null) {
            scope.launch {
                cargando = true
                mensaje = ""

                try {
                    // Copiar el ZIP al almacenamiento privado.
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
                        // Validar y extraer el respaldo temporalmente.
                        val directorio = BackupManager.prepararRestauracion(
                            archivoZip = archivoZip,
                            context = context
                        )

                        directorioPreparado = directorio
                        mensaje = "Respaldo válido. Está listo para revisar su restauración."

                    } finally {
                        archivoZip.delete()
                    }

                } catch (e: Exception) {
                    directorioPreparado = null
                    mensaje = "No se pudo validar el respaldo: ${e.message}"
                } finally {
                    cargando = false
                }
            }
        }
    }

    Scaffold { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Copias de seguridad",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Desde esta sección podrás crear y restaurar copias de seguridad de RehabTrack."
            )

            // Crear respaldo.
            Button(
                onClick = {
                    scope.launch {
                        cargando = true
                        mensaje = ""

                        try {
                            val archivo = BackupManager.crearRespaldo(
                                context,
                                database
                            )

                            archivoPendiente = archivo
                            selectorRespaldo.launch(archivo.name)

                        } catch (e: Exception) {
                            mensaje = "Error al crear el respaldo: ${e.message}"
                        } finally {
                            cargando = false
                        }
                    }
                },
                enabled = !cargando
            ) {
                Text("Crear respaldo")
            }

            // Seleccionar respaldo.
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
                enabled = !cargando
            ) {
                Text("Seleccionar respaldo")
            }

            // Mostrar si existe un respaldo preparado.
            if (directorioPreparado != null) {
                Text(
                    text = "Respaldo seleccionado y validado.",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = {
                        mostrarConfirmacion = true
                    },
                    enabled = !cargando
                ) {
                    Text("Revisar restauración")
                }
            }

            if (cargando) {
                CircularProgressIndicator()

                Text("Procesando respaldo...")
            }

            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = onBack,
                enabled = !cargando
            ) {
                Text("Volver")
            }
        }
    }

    // Confirmación informativa.

// Confirmación y ejecución de la restauración.
    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = {
                if (!cargando) {
                    mostrarConfirmacion = false
                }
            },
            title = {
                Text("Confirmar restauración")
            },
            text = {
                Text(
                    "El respaldo ha superado las validaciones. " +
                            "Los datos actuales de RehabTrack serán sustituidos. " +
                            "Se conservará una copia de seguridad de los datos anteriores."
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !cargando,
                    onClick = {
                        val directorio = directorioPreparado

                        if (directorio != null) {
                            scope.launch {
                                cargando = true
                                mensaje = "Restaurando datos..."

                                try {
                                    val directorioSeguridad =
                                        BackupManager.aplicarRestauracion(
                                            context = context,
                                            directorioPreparado = directorio
                                        )

                                    mensaje = "Restauración aplicada. Preparando reinicio..."

                                    // La copia anterior se conserva.
                                    // No se elimina todavía.

                                    val intent = context.packageManager
                                        .getLaunchIntentForPackage(context.packageName)
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
                                    cargando = false
                                    mensaje = "Error durante la restauración: ${e.message}"
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
                    enabled = !cargando,
                    onClick = {
                        mostrarConfirmacion = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}