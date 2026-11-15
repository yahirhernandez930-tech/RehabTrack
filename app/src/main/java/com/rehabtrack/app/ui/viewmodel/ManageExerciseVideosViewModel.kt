package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.EjercicioEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ManageExerciseVideosUiState(
    val ejercicios: List<EjercicioEntity> = emptyList(),
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val mensajeError: String? = null,
    val mensajeExito: String? = null
)

class ManageExerciseVideosViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)
    private val ejercicioRepository = provider.ejercicioRepository

    private val _uiState = MutableStateFlow(
        ManageExerciseVideosUiState()
    )

    val uiState = _uiState.asStateFlow()

    fun cargarEjercicios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                cargando = true,
                mensajeError = null
            )

            try {
                val ejercicios = withContext(Dispatchers.IO) {
                    ejercicioRepository.obtenerTodos()
                }

                _uiState.value = _uiState.value.copy(
                    ejercicios = ejercicios,
                    cargando = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    mensajeError = e.message
                        ?: "No se pudo cargar el catálogo."
                )
            }
        }
    }

    fun actualizarVideo(
        idEjercicio: Int,
        nuevaUrl: String
    ) {
        if (_uiState.value.guardando) return

        val url = nuevaUrl.trim()

        if (url.isNotEmpty() &&
            !(url.startsWith("https://", ignoreCase = true) ||
                    url.startsWith("http://", ignoreCase = true))
        ) {
            _uiState.value = _uiState.value.copy(
                mensajeError = "La URL debe comenzar con http:// o https://",
                mensajeExito = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                guardando = true,
                mensajeError = null,
                mensajeExito = null
            )

            try {
                withContext(Dispatchers.IO) {
                    val ejercicio = ejercicioRepository
                        .obtenerPorId(idEjercicio)
                        ?: throw Exception("No se encontró el ejercicio.")

                    ejercicioRepository.actualizar(
                        ejercicio.copy(
                            videoUrl = url.ifBlank { null }
                        )
                    )
                }

                val ejerciciosActualizados = withContext(Dispatchers.IO) {
                    ejercicioRepository.obtenerTodos()
                }

                _uiState.value = _uiState.value.copy(
                    ejercicios = ejerciciosActualizados,
                    guardando = false,
                    mensajeExito = if (url.isBlank()) {
                        "Se quitó el video del ejercicio."
                    } else {
                        "La URL del video se actualizó correctamente."
                    }
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    guardando = false,
                    mensajeError = e.message
                        ?: "No se pudo actualizar el video."
                )
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.value = _uiState.value.copy(
            mensajeError = null,
            mensajeExito = null
        )
    }
}