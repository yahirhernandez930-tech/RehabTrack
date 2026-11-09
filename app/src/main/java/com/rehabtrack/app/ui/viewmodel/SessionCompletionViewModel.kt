package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.SesionEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

data class SessionCompletionUiState(
    val sesion: SesionEntity? = null,
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val finalizada: Boolean = false,
    val mensajeError: String? = null
)

class SessionCompletionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)
    private val sesionRepository = provider.sesionRepository

    private val _uiState = MutableStateFlow(
        SessionCompletionUiState()
    )

    val uiState = _uiState.asStateFlow()

    fun cargarSesion(idSesion: Int) {
        viewModelScope.launch {
            _uiState.value = SessionCompletionUiState()

            try {
                val sesion = withContext(Dispatchers.IO) {
                    sesionRepository.obtenerSesionPorId(idSesion)
                } ?: throw Exception("No se encontró la sesión.")

                _uiState.value = SessionCompletionUiState(
                    sesion = sesion,
                    cargando = false
                )

            } catch (e: Exception) {
                _uiState.value = SessionCompletionUiState(
                    cargando = false,
                    mensajeError = e.message
                        ?: "No se pudo cargar la sesión."
                )
            }
        }
    }

    fun finalizarSesion(
        idSesion: Int,
        nivelDolor: Int,
        observaciones: String
    ) {
        if (_uiState.value.guardando) return

        if (nivelDolor !in 0..10) {
            _uiState.value = _uiState.value.copy(
                mensajeError = "Selecciona un nivel de dolor válido."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                guardando = true,
                mensajeError = null
            )

            try {
                withContext(Dispatchers.IO) {
                    val sesion = sesionRepository
                        .obtenerSesionPorId(idSesion)
                        ?: throw Exception("No se encontró la sesión.")

                    sesionRepository.actualizar(
                        sesion.copy(
                            nivelDolor = nivelDolor,
                            observaciones = observaciones
                                .trim()
                                .ifBlank { null },
                            estado = "Finalizada"
                        )
                    )
                }

                val sesionActualizada = withContext(Dispatchers.IO) {
                    sesionRepository.obtenerSesionPorId(idSesion)
                }

                _uiState.value = SessionCompletionUiState(
                    sesion = sesionActualizada,
                    cargando = false,
                    finalizada = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    guardando = false,
                    mensajeError = e.message
                        ?: "No se pudo finalizar la sesión."
                )
            }
        }
    }
}