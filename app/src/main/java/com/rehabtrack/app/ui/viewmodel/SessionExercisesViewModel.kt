package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SessionExerciseData(
    val idEjercicio: Int,
    val nombre: String,
    val descripcion: String,
    val instrucciones: String,
    val series: Int,
    val repeticiones: Int,
    val videoUrl: String?
)

sealed interface SessionExercisesUiState {
    data object Loading : SessionExercisesUiState

    data class Success(
        val ejercicios: List<SessionExerciseData>
    ) : SessionExercisesUiState

    data class Error(
        val mensaje: String
    ) : SessionExercisesUiState
}

class SessionExercisesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val sesionRepository = provider.sesionRepository
    private val programaEjercicioRepository =
        provider.programaEjercicioRepository
    private val ejercicioRepository = provider.ejercicioRepository

    private val _uiState =
        MutableStateFlow<SessionExercisesUiState>(
            SessionExercisesUiState.Loading
        )

    val uiState = _uiState.asStateFlow()

    fun cargarEjercicios(idSesion: Int) {
        viewModelScope.launch {
            _uiState.value = SessionExercisesUiState.Loading

            try {
                val ejercicios = withContext(Dispatchers.IO) {

                    val sesion = sesionRepository
                        .obtenerSesionPorId(idSesion)
                        ?: throw Exception("No se encontró la sesión.")

                    val asignaciones =
                        programaEjercicioRepository
                            .obtenerEjerciciosPrograma(
                                sesion.idPrograma
                            )

                    if (asignaciones.isEmpty()) {
                        throw Exception(
                            "La sesión no tiene ejercicios asignados."
                        )
                    }

                    asignaciones.mapNotNull { asignacion ->

                        val ejercicio = ejercicioRepository
                            .obtenerPorId(asignacion.idEjercicio)

                        ejercicio?.let {
                            SessionExerciseData(
                                idEjercicio = it.idEjercicio,
                                nombre = it.nombre,
                                descripcion = it.descripcion,
                                instrucciones = it.instrucciones,
                                series = asignacion.series,
                                repeticiones = asignacion.repeticiones,
                                videoUrl = it.videoUrl
                            )
                        }
                    }
                }

                if (ejercicios.isEmpty()) {
                    throw Exception(
                        "No se encontraron ejercicios válidos."
                    )
                }

                _uiState.value =
                    SessionExercisesUiState.Success(ejercicios)

            } catch (e: Exception) {
                _uiState.value = SessionExercisesUiState.Error(
                    e.message ?: "No se pudieron cargar los ejercicios."
                )
            }
        }
    }
}