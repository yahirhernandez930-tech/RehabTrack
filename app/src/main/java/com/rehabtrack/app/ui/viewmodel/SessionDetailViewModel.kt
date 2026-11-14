package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.RegistroSerieEntity
import com.rehabtrack.app.data.local.entity.SesionEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.rehabtrack.app.data.local.entity.EvidenciaEntity

data class ExerciseDetailData(
    val idEjercicio: Int,
    val nombre: String,
    val seriesAsignadas: Int,
    val repeticionesObjetivo: Int,
    val registros: List<RegistroSerieEntity>,
    val evidencias: List<EvidenciaEntity> = emptyList()
) {
    val totalRepeticiones: Int
        get() = registros.sumOf { it.repeticionesRealizadas }
}

data class SessionDetailUiState(
    val sesion: SesionEntity? = null,
    val nombrePaciente: String = "",
    val nombrePrograma: String = "",
    val ejercicios: List<ExerciseDetailData> = emptyList(),
    val cargando: Boolean = true,
    val mensajeError: String? = null
)

class SessionDetailViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val sesionRepository = provider.sesionRepository
    private val programaRepository = provider.programaRepository
    private val pacienteRepository = provider.pacienteRepository
    private val programaEjercicioRepository = provider.programaEjercicioRepository
    private val ejercicioRepository = provider.ejercicioRepository
    private val registroSerieRepository = provider.registroSerieRepository
    private val evidenciaRepository = provider.evidenciaRepository

    private val _uiState = MutableStateFlow(SessionDetailUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarDetalle(idSesion: Int) {
        viewModelScope.launch {
            _uiState.value = SessionDetailUiState()

            try {
                val detalle = withContext(Dispatchers.IO) {

                    val sesion = sesionRepository
                        .obtenerSesionPorId(idSesion)
                        ?: throw Exception("No se encontró la sesión.")

                    val programaActual = programaRepository
                        .obtenerProgramaPorId(sesion.idPrograma)
                        ?: throw Exception("No se encontró el programa de la sesión.")

                    val paciente = pacienteRepository
                        .obtenerPacientePorId(
                            programaActual.idPaciente
                        )
                        ?: throw Exception("No se encontró el paciente.")

                    val asignaciones =
                        programaEjercicioRepository
                            .obtenerEjerciciosPrograma(
                                sesion.idPrograma
                            )

                    val ejercicios = asignaciones.mapNotNull { asignacion ->

                        val registros = registroSerieRepository
                            .obtenerRegistrosEjercicio(
                                idSesion = idSesion,
                                idEjercicio = asignacion.idEjercicio
                            )
                            .sortedBy { it.numeroSerie }

                        val evidencias = evidenciaRepository
                            .obtenerEvidenciasEjercicio(
                                idSesion = idSesion,
                                idEjercicio = asignacion.idEjercicio
                            )
                            .sortedBy { it.fechaCaptura }

                        if (registros.isEmpty() && evidencias.isEmpty()) {
                            null
                        } else {
                            val ejercicio = ejercicioRepository
                                .obtenerPorId(asignacion.idEjercicio)

                            ejercicio?.let {
                                ExerciseDetailData(
                                    idEjercicio = it.idEjercicio,
                                    nombre = it.nombre,
                                    seriesAsignadas = asignacion.series,
                                    repeticionesObjetivo =
                                        asignacion.repeticiones,
                                    registros = registros,
                                    evidencias = evidencias
                                )
                            }
                        }
                    }

                    SessionDetailUiState(
                        sesion = sesion,
                        nombrePaciente = paciente.nombre,
                        nombrePrograma = programaActual.nombre,
                        ejercicios = ejercicios,
                        cargando = false
                    )
                }

                _uiState.value = detalle

            } catch (e: Exception) {
                _uiState.value = SessionDetailUiState(
                    cargando = false,
                    mensajeError = e.message
                        ?: "No se pudo cargar el detalle de la sesión."
                )
            }
        }
    }
}