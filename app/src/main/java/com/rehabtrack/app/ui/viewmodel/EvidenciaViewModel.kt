package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.EvidenciaEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class EvidenciaUiState(
    val evidencias: List<EvidenciaEntity> = emptyList(),
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val mensajeError: String? = null
)

class EvidenciaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val evidenciaRepository =
        RehabRepositoryProvider(application).evidenciaRepository

    private val _uiState = MutableStateFlow(EvidenciaUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarEvidencias(
        idSesion: Int,
        idEjercicio: Int
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                cargando = true,
                mensajeError = null
            )

            try {
                val evidencias = withContext(Dispatchers.IO) {
                    evidenciaRepository.obtenerEvidenciasEjercicio(
                        idSesion,
                        idEjercicio
                    )
                }

                _uiState.value = _uiState.value.copy(
                    evidencias = evidencias,
                    cargando = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    mensajeError = e.message
                        ?: "No se pudieron cargar las evidencias."
                )
            }
        }
    }

    fun registrarEvidencia(
        idSesion: Int,
        idEjercicio: Int,
        rutaArchivo: String
    ) {
        if (_uiState.value.guardando) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                guardando = true,
                mensajeError = null
            )

            try {
                val evidencia = EvidenciaEntity(
                    idSesion = idSesion,
                    idEjercicio = idEjercicio,
                    rutaArchivo = rutaArchivo
                )

                withContext(Dispatchers.IO) {
                    evidenciaRepository.registrarEvidencia(evidencia)
                }

                cargarEvidenciasInternamente(idSesion, idEjercicio)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    guardando = false,
                    mensajeError = e.message
                        ?: "No se pudo guardar la evidencia."
                )
            }
        }
    }

    private suspend fun cargarEvidenciasInternamente(
        idSesion: Int,
        idEjercicio: Int
    ) {
        val evidencias = withContext(Dispatchers.IO) {
            evidenciaRepository.obtenerEvidenciasEjercicio(
                idSesion,
                idEjercicio
            )
        }

        _uiState.value = _uiState.value.copy(
            evidencias = evidencias,
            cargando = false,
            guardando = false,
            mensajeError = null
        )
    }
}