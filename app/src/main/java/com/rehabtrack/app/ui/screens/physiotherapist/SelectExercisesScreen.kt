package com.rehabtrack.app.ui.screens.physiotherapist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.app.Application
import androidx.navigation.NavController
import com.rehabtrack.app.ui.viewmodel.SelectExercisesViewModel
import com.rehabtrack.app.data.local.entity.EjercicioEntity

private val ExerciseBlue = Color(0xFF1976D2)
private val ExerciseText = Color(0xFF263238)
private val ExerciseSecondary = Color(0xFF64748B)
private val ExerciseBorder = Color(0xFFCBD5E1)

@Composable
fun SelectExercisesScreen(
    navController: NavController
) {
    val context = LocalContext.current

    val viewModel: SelectExercisesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                if (modelClass.isAssignableFrom(
                        SelectExercisesViewModel::class.java
                    )
                ) {
                    @Suppress("UNCHECKED_CAST")
                    return SelectExercisesViewModel(
                        context.applicationContext as Application
                    ) as T
                }

                throw IllegalArgumentException(
                    "ViewModel desconocido: ${modelClass.name}"
                )
            }
        }
    )

    val availableExercises by viewModel.ejercicios.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    var searchQuery by remember {
        mutableStateOf("")
    }

    val selectedExercises = remember {
        mutableStateListOf<String>()
    }

    var mostrarFormulario by remember {
        mutableStateOf(false)
    }

    var ejercicioEnEdicion by remember {
        mutableStateOf<EjercicioEntity?>(null)
    }

    var nombreEjercicio by remember {
        mutableStateOf("")
    }

    var descripcionEjercicio by remember {
        mutableStateOf("")
    }

    var instruccionesEjercicio by remember {
        mutableStateOf("")
    }

    var videoUrl by remember {
        mutableStateOf("")
    }

    var mensajeErrorRegistro by remember {
        mutableStateOf<String?>(null)
    }

    var guardandoEjercicio by remember {
        mutableStateOf(false)
    }

    val filteredExercises = availableExercises.filter {
        it.nombre.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(
                                "selected_exercises",
                                ArrayList(selectedExercises)
                            )

                        navController.popBackStack()
                    },
                    enabled = selectedExercises.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExerciseBlue
                    )
                ) {
                    Text (
                        text = "Agregar seleccionados (${selectedExercises.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        )  {

            // Encabezado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ExerciseBlue)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(54.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Seleccionar ejercicio",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(Modifier.width(48.dp))

            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {

                Spacer(Modifier.height(25.dp))

                // Buscador
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    placeholder = {
                        Text(
                            "Buscar ejercicio",
                            fontSize = 12.sp,
                            color = ExerciseSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = ExerciseSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(9.dp)
                )

                Spacer(Modifier.height(17.dp))

                OutlinedButton(
                    onClick = {
                        ejercicioEnEdicion = null
                        nombreEjercicio = ""
                        descripcionEjercicio = ""
                        instruccionesEjercicio = ""
                        videoUrl = ""
                        mensajeErrorRegistro = null
                        mostrarFormulario = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(9.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ExerciseBlue
                    )
                ) {
                    Text(
                        text = "+ Registrar ejercicio",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(17.dp))

                Text(
                    text = "Ejercicios disponibles",
                    color = ExerciseText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(15.dp))

                if (cargando) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = ExerciseBlue
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredExercises) { exercise ->

                            val isSelected =
                                exercise.nombre in selectedExercises

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        ExerciseBorder,
                                        RoundedCornerShape(9.dp)
                                    )
                                    .clickable {
                                        if (isSelected) {
                                            selectedExercises.remove(exercise.nombre)
                                        } else {
                                            selectedExercises.add(exercise.nombre)
                                        }
                                    }
                                    .padding(
                                        horizontal = 13.dp,
                                        vertical = 15.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = exercise.nombre,
                                        color = ExerciseText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(Modifier.height(5.dp))

                                    Text(
                                        text = exercise.descripcion,
                                        color = ExerciseSecondary,
                                        fontSize = 11.sp
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        ejercicioEnEdicion = exercise

                                        nombreEjercicio = exercise.nombre
                                        descripcionEjercicio = exercise.descripcion
                                        instruccionesEjercicio = exercise.instrucciones
                                        videoUrl = exercise.videoUrl ?: ""

                                        mensajeErrorRegistro = null
                                        mostrarFormulario = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar ejercicio",
                                        tint = ExerciseBlue
                                    )
                                }

                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            selectedExercises.add(exercise.nombre)
                                        } else {
                                            selectedExercises.remove(exercise.nombre)
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = ExerciseBlue,
                                        uncheckedColor = ExerciseBlue
                                    )
                                )
                            }
                        }

                        if(filteredExercises.isEmpty()) {
                            item {
                                Text(
                                    text = "No se encontraron ejercicios.",
                                    color = ExerciseSecondary,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(vertical = 20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (mostrarFormulario) {

        AlertDialog(
            onDismissRequest = {
                if (!guardandoEjercicio) {
                    mostrarFormulario = false
                }
            },
            title = {
                Text(
                    text = if (ejercicioEnEdicion == null) {
                        "Registrar ejercicio"
                    } else {
                        "Editar ejercicio"
                    },
                    fontWeight = FontWeight.Bold,
                    color = ExerciseText
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = nombreEjercicio,
                        onValueChange = {
                            if (it.length <= 60) {
                                nombreEjercicio = it
                                mensajeErrorRegistro = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nombre del ejercicio") },
                        supportingText = {
                            Text("${nombreEjercicio.length}/60")
                        },
                        singleLine = true,
                        enabled = !guardandoEjercicio
                    )

                    OutlinedTextField(
                        value = descripcionEjercicio,
                        onValueChange = {
                            if (it.length <= 250) {
                                descripcionEjercicio = it
                                mensajeErrorRegistro = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Descripción") },
                        supportingText = {
                            Text("${descripcionEjercicio.length}/250")
                        },
                        minLines = 2,
                        maxLines = 3,
                        enabled = !guardandoEjercicio
                    )

                    OutlinedTextField(
                        value = instruccionesEjercicio,
                        onValueChange = {
                            if (it.length <= 1000) {
                                instruccionesEjercicio = it
                                mensajeErrorRegistro = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Instrucciones") },
                        supportingText = {
                            Text("${instruccionesEjercicio.length}/1000")
                        },
                        minLines = 3,
                        maxLines = 5,
                        enabled = !guardandoEjercicio
                    )

                    OutlinedTextField(
                        value = videoUrl,
                        onValueChange = {
                            if (it.length <= 500) {
                                videoUrl = it
                                mensajeErrorRegistro = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("URL del video (opcional)") },
                        supportingText = {
                            Text("${videoUrl.length}/500")
                        },
                        singleLine = true,
                        enabled = !guardandoEjercicio
                    )

                    if (mensajeErrorRegistro != null) {
                        Text(
                            text = mensajeErrorRegistro!!,
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {

                        val nombre = nombreEjercicio.trim()
                        val descripcion = descripcionEjercicio.trim()
                        val instrucciones = instruccionesEjercicio.trim()
                        val url = videoUrl.trim()

                        mensajeErrorRegistro = when {
                            nombre.isBlank() ->
                                "El nombre del ejercicio es obligatorio."

                            nombre.length < 3 ->
                                "El nombre debe tener al menos 3 caracteres."

                            nombre.length > 60 ->
                                "El nombre no puede superar 60 caracteres."

                            descripcion.length > 250 ->
                                "La descripción no puede superar 250 caracteres."

                            instrucciones.isBlank() ->
                                "Las instrucciones son obligatorias."

                            instrucciones.length > 1000 ->
                                "Las instrucciones no pueden superar 1000 caracteres."

                            url.length > 500 ->
                                "La URL no puede superar 500 caracteres."

                            url.isNotBlank() && run {
                                val uri = android.net.Uri.parse(url)

                                !(
                                        (uri.scheme == "https" || uri.scheme == "http") &&
                                                !uri.host.isNullOrBlank() &&
                                                uri.host!!.contains(".") &&
                                                !uri.host!!.startsWith(".") &&
                                                !uri.host!!.endsWith(".") &&
                                                uri.userInfo == null
                                        )
                            } ->
                                "Introduce una URL válida que incluya un dominio."
                            else -> null
                        }

                        if (mensajeErrorRegistro == null) {

                            guardandoEjercicio = true

                            val ejercicioActual = ejercicioEnEdicion

                            val onResultado: (String?) -> Unit = { resultado ->
                                guardandoEjercicio = false

                                if (resultado == null) {
                                    mostrarFormulario = false
                                    ejercicioEnEdicion = null
                                } else {
                                    mensajeErrorRegistro = resultado
                                }
                            }

                            if (ejercicioActual == null) {

                                viewModel.registrarEjercicio(
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    instrucciones = instrucciones,
                                    videoUrl = url,
                                    onResultado = onResultado
                                )

                            } else {

                                viewModel.actualizarEjercicio(
                                    ejercicio = ejercicioActual,
                                    nombre = nombre,
                                    descripcion = descripcion,
                                    instrucciones = instrucciones,
                                    videoUrl = url,
                                    onResultado = onResultado
                                )
                            }
                        }
                    },
                    enabled = !guardandoEjercicio,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExerciseBlue
                    )
                ) {
                    if (guardandoEjercicio) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            if (ejercicioEnEdicion == null) {
                                "Guardar ejercicio"
                            } else {
                                "Actualizar ejercicio"
                            }
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarFormulario = false
                    },
                    enabled = !guardandoEjercicio
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = Color.White
        )
    }
}