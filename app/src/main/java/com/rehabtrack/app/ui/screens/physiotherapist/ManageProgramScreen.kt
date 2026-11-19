package com.rehabtrack.app.ui.screens.physiotherapist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavController
import com.rehabtrack.app.navigation.Routes
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rehabtrack.app.ui.viewmodel.ManageProgramViewModel
import com.rehabtrack.app.ui.viewmodel.EjercicioAsignadoData
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

private val ProgramBlue = Color(0xFF1976D2)
private val ProgramText = Color(0xFF263238)
private val ProgramSecondary = Color(0xFF64748B)
private val ProgramBorder = Color(0xFFCBD5E1)
private val ProgramField = Color(0xFFF1F5F9)
private const val MAX_SESIONES = 50

private data class AssignedExercise(
    val name: String,
    val series: Int,
    val repetitions: Int
)

private val assignedExerciseListSaver =
    listSaver<SnapshotStateList<AssignedExercise>, Any>(
        save = { exercises ->
            exercises.flatMap { exercise ->
                listOf(
                    exercise.name,
                    exercise.series,
                    exercise.repetitions
                )
            }
        },
        restore = { saved ->
            mutableStateListOf<AssignedExercise>().apply {
                saved.chunked(3).forEach { values ->
                    add(
                        AssignedExercise(
                            name = values[0] as String,
                            series = values[1] as Int,
                            repetitions = values[2] as Int
                        )
                    )
                }
            }
        }
    )

@Composable
fun ManageProgramScreen(
    navController: NavController,
    idUsuario: Int,
    idPaciente: Int
) {

    val viewModel: ManageProgramViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(idUsuario, idPaciente) {
        viewModel.cargarPaciente(idUsuario, idPaciente)
    }

    var programName by remember {
        mutableStateOf("Rehabilitación de rodilla")
    }

    var duration by rememberSaveable { mutableStateOf("12") }

    var durationTouched by rememberSaveable {
        mutableStateOf(false)
    }

    val durationError = when {
        duration.isBlank() ->
            "Este campo es obligatorio."

        duration.toIntOrNull() == null ->
            "Introduce un número entero válido."

        duration.toInt() < 1 ->
            "El mínimo permitido es 1 sesión."

        duration.toInt() > MAX_SESIONES ->
            "El máximo permitido es $MAX_SESIONES sesiones."

        else -> null
    }

    var datosInicialesCargados by rememberSaveable {
        mutableStateOf(false)
    }

    val exercises = rememberSaveable(
        saver = assignedExerciseListSaver
    ) {
        mutableStateListOf(
            AssignedExercise("Sentadilla asistida", 3, 10),
            AssignedExercise("Flexión de rodilla", 3, 8)
        )
    }

    LaunchedEffect(
        uiState.cargandoPaciente,
        uiState.programaExistente,
        uiState.ejerciciosPrograma
    ) {
        if (
            !uiState.cargandoPaciente &&
            !datosInicialesCargados
        ) {
            val programa = uiState.programaExistente

            if (programa != null) {

                // Recuperar información del programa.
                programName = programa.nombre
                duration = programa.duracionSesiones.toString()

                // Recuperar ejercicios y sus configuraciones.
                exercises.clear()

                exercises.addAll(
                    uiState.ejerciciosPrograma.map { ejercicio ->
                        AssignedExercise(
                            name = ejercicio.nombre,
                            series = ejercicio.series,
                            repetitions = ejercicio.repeticiones
                        )
                    }
                )
            }

            datosInicialesCargados = true
        }
    }

    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var editingExerciseIndex by remember { mutableStateOf<Int?>(null) }

    var newExerciseName by remember { mutableStateOf("") }
    var newExerciseSeries by remember { mutableStateOf("3") }
    var newExerciseRepetitions by remember { mutableStateOf("10") }

    var showDeleteExerciseDialog by remember { mutableStateOf(false) }
    var deletingExerciseIndex by remember { mutableIntStateOf(-1) }

    var showSaveDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }

    var validationMessage by remember { mutableStateOf("") }

    var exerciseValidationMessage by remember { mutableStateOf("") }

    val manageProgramEntry = remember(navController) {
        navController.currentBackStackEntry!!
    }

    val selectedExerciseNames by manageProgramEntry
        .savedStateHandle
        .getStateFlow(
            "selected_exercises",
            arrayListOf<String>()
        )
        .collectAsState()

    LaunchedEffect(selectedExerciseNames) {
        if(selectedExerciseNames.isNotEmpty()) {

            selectedExerciseNames.forEach { name ->
                if (exercises.none {
                        it.name.equals(name, ignoreCase = true)
                    }
                ) {
                    exercises.add(
                        AssignedExercise(
                            name = name,
                            series = 3,
                            repetitions = 10
                        )
                    )
                }
            }

            manageProgramEntry.savedStateHandle.set(
                "selected_exercises",
                arrayListOf<String>()
            )
        }
    }

    LaunchedEffect(uiState.mensajeExito, uiState.mensajeError) {
        if (
            uiState.mensajeExito != null ||
            uiState.mensajeError != null
        ) {
            showResultDialog = true
        }
    }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // Encabezado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ProgramBlue)
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
                    text = if (uiState.programaExistente != null) {
                        "Gestionar programa"
                    } else {
                        "Crear programa"
                    },
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 26.dp)
                    .navigationBarsPadding()
            ) {
                Spacer(Modifier.height(18.dp))

                FormLabel("Paciente")

                Text(
                    text = if (uiState.cargandoPaciente) {
                        "Cargando paciente..."
                    } else {
                        uiState.nombrePaciente.ifBlank { "Paciente no disponible" }
                    },
                    color = ProgramText,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp)
                        .background(
                            ProgramField,
                            RoundedCornerShape(9.dp)
                        )
                        .border(
                            1.dp,
                            ProgramBorder,
                            RoundedCornerShape(9.dp)
                        )
                        .padding(horizontal = 15.dp, vertical = 15.dp)
                )

                Spacer(Modifier.height(17.dp))

                FormLabel("Nombre del programa")

                OutlinedTextField(
                    value = programName,
                    onValueChange = {
                        programName = it
                        validationMessage = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp)
                        .height(54.dp),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = ProgramText
                    ),
                    shape = RoundedCornerShape(9.dp)
                )

                Spacer(Modifier.height(17.dp))

                FormLabel("Duración del programa")

                OutlinedTextField(
                    value = duration,
                    onValueChange = { value ->
                        duration = value.filter(Char::isDigit)
                        durationTouched = true
                        validationMessage = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp),
                    singleLine = true,
                    label = {
                        Text("Número de sesiones")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    isError = durationTouched && durationError != null,
                    supportingText = {
                        if (durationTouched && durationError != null) {
                            Text(
                                text = durationError,
                                color = Color(0xFFD32F2F)
                            )
                        } else {
                            Text("Introduce entre 1 y $MAX_SESIONES sesiones.")
                        }
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        color = ProgramText
                    ),
                    shape = RoundedCornerShape(9.dp)
                )

                Spacer(Modifier.height(23.dp))

                Text(
                    text = "Ejercicios asignados",
                    color = ProgramText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(15.dp))

                exercises.forEachIndexed { index, exercise ->
                    ExerciseAssignmentCard(
                        exercise = exercise,
                        onEdit = {
                            editingExerciseIndex = index
                            newExerciseName = exercise.name
                            newExerciseSeries = exercise.series.toString()
                            newExerciseRepetitions = exercise.repetitions.toString()
                            exerciseValidationMessage = ""
                            showAddExerciseDialog = true
                        },
                        onDelete = {
                            deletingExerciseIndex = index
                            showDeleteExerciseDialog = true
                        }
                    )
                    Spacer(Modifier.height(15.dp))
                }

                OutlinedButton(
                    onClick = {
                        navController.navigate(Routes.SelectExercises.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .height(42.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ProgramBlue
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Agregar ejercicios",
                        fontSize = 14.sp
                    )
                }

                if (validationMessage.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = validationMessage,
                        color = Color(0xFFD32F2F),
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(18.dp))

                Button(
                    onClick = {
                        validationMessage = when {
                            programName.isBlank() ->
                                "Escribe el nombre del programa."

                            exercises.isEmpty() ->
                                "Agrega al menos un ejercicio."

                            durationError != null -> {
                                durationTouched = true
                                durationError
                            }

                            uiState.nombrePaciente.isBlank() ->
                                "No se puede guardar porque el paciente no está disponible."

                            else -> ""
                        }

                        if (validationMessage.isEmpty()) {
                            showSaveDialog = true
                        }
                    },
                    enabled = !uiState.guardando &&
                            !uiState.cargandoPaciente &&
                            uiState.nombrePaciente.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth(0.70f)
                        .height(43.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProgramBlue
                    )
                ) {
                    Text(
                        text = when {
                            uiState.guardando -> "Guardando..."

                            uiState.programaExistente != null ->
                                "Guardar cambios"

                            else -> "Guardar programa"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(25.dp))
            }
        }
    }

    if (showAddExerciseDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddExerciseDialog = false
                editingExerciseIndex = null
                exerciseValidationMessage = ""
            },
            title = {
                Text(
                    if (editingExerciseIndex == null) {
                        "Agregar ejercicio"
                    } else {
                        "Editar ejercicio"
                    }
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newExerciseName,
                        onValueChange = { newExerciseName = it },
                        label = { Text("Nombre del ejercicio") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newExerciseSeries,
                        onValueChange = { newExerciseSeries = it.filter(Char::isDigit) },
                        label = { Text("Series") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newExerciseRepetitions,
                        onValueChange = {
                            newExerciseRepetitions = it.filter(Char::isDigit)
                        },
                        label = { Text("Repeticiones") },
                        singleLine = true
                    )

                    if (exerciseValidationMessage.isNotEmpty()) {
                        Text(
                            text = exerciseValidationMessage,
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val series = newExerciseSeries.toIntOrNull()
                        val repetitions = newExerciseRepetitions.toIntOrNull()

                        exerciseValidationMessage = when {
                            newExerciseName.isBlank() ->
                                "Escribe el nombre del ejercicio."

                            series == null || series <= 0 ->
                                "Las series deben ser mayores que cero."

                            repetitions == null || repetitions <= 0 ->
                                "Las repeticiones deben ser mayores que cero"

                            else -> ""
                        }

                        if (exerciseValidationMessage.isEmpty()) {
                            val exercise = AssignedExercise(
                                name = newExerciseName.trim(),
                                series = series!!,
                                repetitions = repetitions!!
                            )

                            val index = editingExerciseIndex

                            if (index == null) {
                                exercises.add(exercise)
                            } else if (index in exercises.indices) {
                                exercises[index] = exercise
                            }

                            editingExerciseIndex = null
                            exerciseValidationMessage = ""
                            showAddExerciseDialog = false
                        }
                    }
                ) {
                    Text(
                        if (editingExerciseIndex == null) {
                            "Agregar"
                        } else {
                            "Guardar cambios"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddExerciseDialog = false
                        editingExerciseIndex = null
                        exerciseValidationMessage = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDeleteExerciseDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteExerciseDialog = false
                deletingExerciseIndex = -1
            },
            title = {
                Text("Eliminar ejercicio")
            },
            text = {
                val index = deletingExerciseIndex

                Text(
                    if (index in exercises.indices) {
                        "¿Deseas eliminar \"${exercises[index].name}\" " +
                                "del programa?"
                    } else {
                        "¿Deseas eliminar este ejercicio?"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val index = deletingExerciseIndex

                        if (index in exercises.indices) {
                            exercises.removeAt(index)
                        }

                        deletingExerciseIndex = -1
                        showDeleteExerciseDialog = false
                    }
                ) {
                    Text(
                        "Eliminar",
                        color = Color(0xFFD32F2F)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteExerciseDialog = false
                        deletingExerciseIndex = -1
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = {
                showSaveDialog = false
            },
            title = {
                Text(
                    if (uiState.programaExistente != null) {
                        "Confirmar cambios"
                    } else {
                        "Confirmar programa"
                    }
                )
            },
            text = {
                Text(
                    if (uiState.programaExistente != null) {
                        "¿Deseas guardar los cambios del programa " +
                                "\"$programName\" para ${uiState.nombrePaciente}?"
                    } else {
                        "¿Deseas guardar el programa \"$programName\" " +
                                "para ${uiState.nombrePaciente}?"
                    }
                )
            },
            confirmButton = {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    TextButton(
                        onClick = {
                            viewModel.guardarPrograma(
                                idUsuario = idUsuario,
                                idPaciente = idPaciente,
                                nombrePrograma = programName,
                                duracionSesiones = duration.toInt(),
                                ejercicios = exercises.map { exercise ->
                                    EjercicioAsignadoData(
                                        nombre = exercise.name,
                                        series = exercise.series,
                                        repeticiones = exercise.repetitions
                                    )
                                }
                            )
                            showSaveDialog = false
                        }
                    ) {
                        Text(
                            if (uiState.programaExistente != null) {
                                "Guardar cambios"
                            } else {
                                "Guardar"
                            }
                        )
                    }

                    TextButton(
                        onClick = {
                            showSaveDialog = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }

    if (showResultDialog) {
        val error = uiState.mensajeError
        val exito = uiState.mensajeExito

        AlertDialog(
            onDismissRequest = {
                showResultDialog = false
                viewModel.limpiarMensajes()
            },
            title = {
                Text(
                    if (error != null) {
                        "Error al guardar"
                    } else {
                        "Programa guardado"
                    }
                )
            },
            text = {
                Text(
                    error ?: exito.orEmpty()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResultDialog = false
                        viewModel.limpiarMensajes()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        color = ProgramText,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun ExerciseAssignmentCard(
    exercise: AssignedExercise,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                ProgramBorder,
                RoundedCornerShape(9.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exercise.name,
                color = ProgramText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar ${exercise.name}",
                    tint = ProgramBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Eliminar ${exercise.name}",
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(Modifier.height(6.dp))

        Text(
            text = "${exercise.series} series",
            color = ProgramSecondary,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = "${exercise.repetitions} repeticiones",
            color = ProgramSecondary,
            fontSize = 13.sp
        )
    }
}
