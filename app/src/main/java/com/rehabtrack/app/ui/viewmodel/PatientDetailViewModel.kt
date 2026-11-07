package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.PacienteEntity
import com.rehabtrack.app.data.local.entity.ProgramaEntity
import com.rehabtrack.app.data.local.entity.SesionEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface PatientDetailUiState {

    data object Idle : PatientDetailUiState

    data object Loading : PatientDetailUiState

    data class Success(
        val paciente: PacienteEntity,
        val programa: ProgramaEntity?,
        val sesiones: List<SesionEntity> = emptyList()
    ) : PatientDetailUiState

    data class Error(
        val mensaje: String
    ) : PatientDetailUiState
}

class PatientDetailViewModel(application: Application) :
    AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val pacienteRepository =
        provider.pacienteRepository

    private val programaRepository =
        provider.programaRepository

    private val fisioterapeutaRepository =
        provider.fisioterapeutaRepository

    private val asignacionRepository =
        provider.asignacionRepository

    private val sesionRepository =
        provider.sesionRepository

    private val _uiState =
        MutableStateFlow<PatientDetailUiState>(
            PatientDetailUiState.Idle
        )

    val uiState: StateFlow<PatientDetailUiState> =
        _uiState.asStateFlow()

    // Cargar información del paciente, programa y sesiones.

    fun cargarPaciente(
        idUsuario: Int,
        idPaciente: Int
    ) {
        if (_uiState.value == PatientDetailUiState.Loading) return

        _uiState.value = PatientDetailUiState.Loading

        viewModelScope.launch {
            try {
                val resultado = withContext(Dispatchers.IO) {

                    // 1. Obtener el perfil del fisioterapeuta.
                    val fisioterapeuta =
                        fisioterapeutaRepository
                            .obtenerFisioterapeutaPorUsuario(idUsuario)
                            ?: throw Exception(
                                "No se encontró el perfil del fisioterapeuta."
                            )

                    // 2. Comprobar la asignación activa.
                    val asignacion =
                        asignacionRepository
                            .obtenerAsignacionActiva(idPaciente)
                            ?: throw Exception(
                                "El paciente no tiene una asignación activa."
                            )

                    // 3. Validar que pertenezca al fisioterapeuta.
                    if (
                        asignacion.idFisioterapeuta !=
                        fisioterapeuta.idFisioterapeuta
                    ) {
                        throw Exception(
                            "No tienes acceso a la información de este paciente."
                        )
                    }

                    // 4. Recuperar los datos del paciente.
                    val paciente =
                        pacienteRepository.obtenerPacientePorId(idPaciente)
                            ?: throw Exception(
                                "No se encontró la información del paciente."
                            )

                    // 5. Recuperar su programa activo.
                    val programa =
                        programaRepository.obtenerProgramaActivo(idPaciente)

                    // 6. Recuperar las sesiones del programa.
                    val sesiones = programa?.let {
                        sesionRepository.obtenerSesionesPrograma(
                            it.idPrograma
                        )
                    } ?: emptyList()

                    Triple(paciente, programa, sesiones)
                }

                _uiState.value = PatientDetailUiState.Success(
                    paciente = resultado.first,
                    programa = resultado.second,
                    sesiones = resultado.third
                )

            } catch (e: Exception) {
                _uiState.value = PatientDetailUiState.Error(
                    e.message
                        ?: "No se pudo cargar la información del paciente."
                )
            }
        }
    }

    // Programar una nueva sesión.

    fun programarSesion(
        fecha: String,
        hora: String
    ) {
        val estadoActual = _uiState.value

        if (estadoActual !is PatientDetailUiState.Success) return

        val programa = estadoActual.programa ?: run {
            _uiState.value = PatientDetailUiState.Error(
                "El paciente no tiene un programa activo."
            )
            return
        }

        if (fecha.isBlank() || hora.isBlank()) {
            _uiState.value = PatientDetailUiState.Error(
                "La fecha y la hora son obligatorias."
            )
            return
        }

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    sesionRepository.insertar(
                        SesionEntity(
                            idPrograma = programa.idPrograma,
                            fecha = fecha,
                            hora = hora
                        )
                    )
                }

                cargarSesiones()

            } catch (e: Exception) {
                _uiState.value = PatientDetailUiState.Error(
                    e.message ?: "No se pudo programar la sesión."
                )
            }
        }
    }

    // Consultar nuevamente las sesiones.

    fun cargarSesiones() {
        val estadoActual = _uiState.value

        if (estadoActual !is PatientDetailUiState.Success) return

        val programa = estadoActual.programa ?: return

        viewModelScope.launch {
            try {
                val sesiones = withContext(Dispatchers.IO) {
                    sesionRepository.obtenerSesionesPrograma(
                        programa.idPrograma
                    )
                }

                actualizarSesionesEnEstado(sesiones)

            } catch (e: Exception) {
                _uiState.value = PatientDetailUiState.Error(
                    e.message ?: "No se pudieron cargar las sesiones."
                )
            }
        }
    }

    // Actualizar el estado de una sesión.

    fun actualizarEstadoSesion(
        idSesion: Int,
        nuevoEstado: String
    ) {
        val estadosPermitidos = listOf(
            "Finalizada",
            "No asistió",
            "Cancelada"
        )

        if (nuevoEstado !in estadosPermitidos) {
            return
        }

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {

                    val sesion =
                        sesionRepository.obtenerSesionPorId(idSesion)
                            ?: throw Exception(
                                "No se encontró la sesión."
                            )

                    // Solo se pueden modificar sesiones pendientes.
                    if (sesion.estado != "Pendiente") {
                        throw Exception(
                            "Esta sesión ya no puede modificarse."
                        )
                    }

                    sesionRepository.actualizar(
                        sesion.copy(
                            estado = nuevoEstado
                        )
                    )
                }

                cargarSesiones()

            } catch (e: Exception) {
                _uiState.value = PatientDetailUiState.Error(
                    e.message
                        ?: "No se pudo actualizar el estado de la sesión."
                )
            }
        }
    }

    // Registrar el resultado de una sesión.

    fun registrarResultadoSesion(
        idSesion: Int,
        nivelDolor: Int?,
        observaciones: String?
    ) {
        if (nivelDolor != null && nivelDolor !in 0..10) {
            _uiState.value = PatientDetailUiState.Error(
                "El nivel de dolor debe estar entre 0 y 10."
            )
            return
        }

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {

                    val sesion =
                        sesionRepository.obtenerSesionPorId(idSesion)
                            ?: throw Exception(
                                "No se encontró la sesión."
                            )

                    sesionRepository.actualizar(
                        sesion.copy(
                            nivelDolor = nivelDolor,
                            observaciones = observaciones
                        )
                    )
                }

                cargarSesiones()

            } catch (e: Exception) {
                _uiState.value = PatientDetailUiState.Error(
                    e.message
                        ?: "No se pudo registrar el resultado de la sesión."
                )
            }
        }
    }

    // Actualizar las sesiones sin modificar los demás datos.

    private fun actualizarSesionesEnEstado(
        sesiones: List<SesionEntity>
    ) {
        val estadoActual = _uiState.value

        if (estadoActual is PatientDetailUiState.Success) {
            _uiState.value = estadoActual.copy(
                sesiones = sesiones
            )
        }
    }
}