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

data class HistorySessionItem(
    val sesion: SesionEntity,
    val nombrePrograma: String
)

data class HistoryUiState(
    val sesiones: List<HistorySessionItem> = emptyList(),
    val cargando: Boolean = true,
    val mensajeError: String? = null
)

class HistoryViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val pacienteRepository = provider.pacienteRepository
    private val programaRepository = provider.programaRepository
    private val sesionRepository = provider.sesionRepository

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarHistorial(
        idUsuario: Int? = null,
        idPaciente: Int? = null
    ) {
        viewModelScope.launch {

            _uiState.value = HistoryUiState()

            try {
                val historial = withContext(Dispatchers.IO) {

                    // Resolver el identificador del paciente.
                    val pacienteId = idPaciente
                        ?: idUsuario?.let {
                            pacienteRepository
                                .obtenerPacientePorUsuario(it)
                                ?.idPaciente
                        }
                        ?: throw Exception(
                            "No se encontró el paciente."
                        )

                    // Obtener todos los programas del paciente.
                    val programas = programaRepository
                        .obtenerProgramasPaciente(pacienteId)

                    // Obtener las sesiones de cada programa.
                    programas.flatMap { programa ->

                        sesionRepository
                            .obtenerSesionesPrograma(
                                programa.idPrograma
                            )
                            .map { sesion ->

                                HistorySessionItem(
                                    sesion = sesion,
                                    nombrePrograma = programa.nombre
                                )
                            }
                    }.sortedWith(
                        compareByDescending<HistorySessionItem> {
                            it.sesion.fecha
                        }.thenByDescending {
                            it.sesion.hora
                        }
                    )
                }

                _uiState.value = HistoryUiState(
                    sesiones = historial,
                    cargando = false
                )

            } catch (e: Exception) {

                _uiState.value = HistoryUiState(
                    cargando = false,
                    mensajeError = e.message
                        ?: "No se pudo cargar el historial."
                )
            }
        }
    }
}