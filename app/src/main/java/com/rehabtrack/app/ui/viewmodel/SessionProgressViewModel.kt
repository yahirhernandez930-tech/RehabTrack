package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.RegistroSerieEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SessionProgressUiState(
    val registros: List<RegistroSerieEntity> = emptyList(),
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val mensajeError: String? = null
)

class SessionProgressViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val registroSerieRepository =
        provider.registroSerieRepository

    private val _uiState = MutableStateFlow(
        SessionProgressUiState()
    )

    val uiState = _uiState.asStateFlow()

    fun cargarProgreso(
        idSesion: Int,
        idEjercicio: Int
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                cargando = true,
                mensajeError = null
            )

            try {
                val registros = withContext(Dispatchers.IO) {
                    registroSerieRepository.obtenerRegistrosEjercicio(
                        idSesion,
                        idEjercicio
                    )
                }

                _uiState.value = SessionProgressUiState(
                    registros = registros,
                    cargando = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    mensajeError = e.message
                        ?: "No se pudo cargar el progreso."
                )
            }
        }
    }

    fun registrarSerie(
        idSesion: Int,
        idEjercicio: Int,
        numeroSerie: Int,
        repeticionesRealizadas: Int
    ) {
        if (_uiState.value.guardando) return

        if (repeticionesRealizadas <= 0 || numeroSerie <= 0) {
            _uiState.value = _uiState.value.copy(
                mensajeError = "Los valores de la serie no son válidos."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                guardando = true,
                mensajeError = null
            )

            try {
                val registro = RegistroSerieEntity(
                    idSesion = idSesion,
                    idEjercicio = idEjercicio,
                    numeroSerie = numeroSerie,
                    repeticionesRealizadas = repeticionesRealizadas
                )

                withContext(Dispatchers.IO) {
                    registroSerieRepository.insertar(registro)
                }

                val registrosActualizados = withContext(Dispatchers.IO) {
                    registroSerieRepository.obtenerRegistrosEjercicio(
                        idSesion,
                        idEjercicio
                    )
                }

                _uiState.value = SessionProgressUiState(
                    registros = registrosActualizados,
                    cargando = false,
                    guardando = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    guardando = false,
                    mensajeError = e.message
                        ?: "No se pudo guardar la serie."
                )
            }
        }
    }
}