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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private val ExerciseBlue = Color(0xFF1976D2)
private val ExerciseText = Color(0xFF263238)
private val ExerciseSecondary = Color(0xFF64748B)
private val ExerciseBorder = Color(0xFFCBD5E1)

private data class ExerciseOption(
    val name: String,
    val category: String
)

@Composable
fun SelectExercisesScreen(
    navController: NavController
) {
    val availableExercises = listOf(
        ExerciseOption("Sentadilla Asistida", "Fortalecimiento de rodilla"),
        ExerciseOption("Flexión de rodilla", "Movilidad articular"),
        ExerciseOption("Extensión de rodilla", "Fortalecimiento muscular"),
        ExerciseOption("Elevación de pierna", "Control y estabilidad"),
        ExerciseOption("Puente de glúteos", "Fortalecimiento muscular"),
        ExerciseOption("Elevación de talones", "Fortalecimiento muscular"),
        ExerciseOption("Deslizamiento de talón", "Movilidad articular")
    )

    var searchQuery by remember { mutableStateOf("") }

    val selectedExercises = remember {
        mutableStateListOf<String>()
    }

    val filteredExersices = availableExercises.filter {
        it.name.contains(searchQuery, ignoreCase = true)
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

                Text(
                    text = "Ejercicios disponibles",
                    color = ExerciseText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(15.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredExersices) { exercise ->

                        val isSelected =
                            exercise.name in selectedExercises

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
                                        selectedExercises.remove(exercise.name)
                                    } else {
                                        selectedExercises.add(exercise.name)
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
                                    text = exercise.name,
                                    color = ExerciseText,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(Modifier.height(5.dp))

                                Text(
                                    text = exercise.category,
                                    color = ExerciseSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selectedExercises.add(exercise.name)
                                    } else {
                                        selectedExercises.remove(exercise.name)
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = ExerciseBlue,
                                    uncheckedColor = ExerciseBlue
                                )
                            )
                        }
                    }

                    if(filteredExersices.isEmpty()) {
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