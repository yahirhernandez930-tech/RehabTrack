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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import com.rehabtrack.app.util.EvidenciaStorage
import java.io.File
import com.rehabtrack.app.data.local.entity.EvidenciaEntity
import androidx.compose.foundation.clickable
import androidx.compose.ui.window.Dialog

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
    onRegistrarSerie: (Int, Int) -> Unit,
    onFotografiaCapturada: (String) -> Unit = {},
    evidencias: List<EvidenciaEntity> = emptyList(),
    guardandoEvidencia: Boolean = false,
    mensajeErrorEvidencia: String? = null
) {
    val colors = MaterialTheme.colorScheme

    val context = LocalContext.current

    var evidenciaSeleccionada by remember {
        mutableStateOf<String?>(null)
    }

    if (evidenciaSeleccionada != null) {
        val bitmapAmpliado = remember(evidenciaSeleccionada) {
            evidenciaSeleccionada?.let { ruta ->
                cargarImagenPrevia(File(ruta))
            }
        }

        Dialog(
            onDismissRequest = {
                evidenciaSeleccionada = null
            }
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = colors.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Evidencia fotográfica",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (bitmapAmpliado != null) {
                        Image(
                            bitmap = bitmapAmpliado.asImageBitmap(),
                            contentDescription = "Fotografía ampliada",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            text = "No se pudo cargar la fotografía.",
                            color = colors.error
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            evidenciaSeleccionada = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }

    // Archivo que se mostrará en la vista previa.
    var fotografiaActual by remember {
        mutableStateOf<File?>(null)
    }

    // Archivo preparado para recibir la siguiente fotografía.
    var archivoPendiente by remember {
        mutableStateOf<File?>(null)
    }

    var uriFotografia by remember {
        mutableStateOf<Uri?>(null)
    }

    var errorFotografia by remember {
        mutableStateOf<String?>(null)
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { fotografiaGuardada ->

        val archivo = archivoPendiente

        if (
            fotografiaGuardada &&
            archivo != null &&
            archivo.exists() &&
            archivo.length() > 0
        ) {
            // Actualizamos el estado de la vista previa
            fotografiaActual = archivo

            errorFotografia = null

            onFotografiaCapturada(archivo.absolutePath)

        } else {
            archivo?.delete()

            errorFotografia = "No se pudo capturar la fotografía."
        }

        archivoPendiente = null
    }

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

                    Text(
                        text = "${evidencias.size} fotografías guardadas",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val archivoVistaPrevia = fotografiaActual
                        ?: evidencias.lastOrNull()?.rutaArchivo?.let { File(it) }

                    val bitmap = remember(archivoVistaPrevia) {
                        archivoVistaPrevia?.let { cargarImagenPrevia(it) }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(width = 180.dp, height = 150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Evidencia fotográfica del ejercicio",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = "Sin fotografía",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }

                    // Galería de evidencias guardadas
                    if (evidencias.isNotEmpty()) {

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Fotografías guardadas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackground,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        evidencias.chunked(2).forEach { fila ->

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {

                                fila.forEach { evidencia ->

                                    val bitmapMiniatura = remember(evidencia.rutaArchivo) {
                                        cargarImagenPrevia(File(evidencia.rutaArchivo))
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(110.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(colors.surfaceVariant)
                                            .clickable {
                                                evidenciaSeleccionada = evidencia.rutaArchivo
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {

                                        if (bitmapMiniatura != null) {
                                            Image(
                                                bitmap = bitmapMiniatura.asImageBitmap(),
                                                contentDescription = "Evidencia fotográfica",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Text(
                                                text = "Imagen no disponible",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = colors.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                // Mantiene el ancho de la cuadrícula
                                // cuando existe una cantidad impar de fotografías.
                                if (fila.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            try {
                                val archivo = EvidenciaStorage.crearArchivoEvidencia(context)
                                val uri = EvidenciaStorage.obtenerUri(context, archivo)

                                // Guardamos el archivo pendiente, sin modificar la vista previa.
                                archivoPendiente = archivo
                                uriFotografia = uri

                                launcherCamara.launch(uri)

                            } catch (e: Exception) {
                                errorFotografia = "No se pudo abrir la cámara."
                            }
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

                    if (guardandoEvidencia) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Guardando evidencia...",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }

                    mensajeErrorEvidencia?.let { error ->
                        Text(
                            text = error,
                            color = colors.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                errorFotografia?.let { error ->
                    Text(
                        text = error,
                        color = colors.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
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

private fun cargarImagenPrevia(archivo: File): Bitmap? {
    return try {
        val opciones = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(archivo.absolutePath, opciones)

        val anchoObjetivo = 360
        val altoObjetivo = 300

        var escala = 1

        while (
            opciones.outWidth / escala > anchoObjetivo * 2 ||
            opciones.outHeight / escala > altoObjetivo * 2
        ) {
            escala *= 2
        }

        val opcionesFinales = BitmapFactory.Options().apply {
            inSampleSize = escala
        }

        BitmapFactory.decodeFile(
            archivo.absolutePath,
            opcionesFinales
        )
    } catch (e: Exception) {
        android.util.Log.e(
            "RehabTrackCamera",
            "Error al cargar vista previa",
            e
        )
        null
    }
}