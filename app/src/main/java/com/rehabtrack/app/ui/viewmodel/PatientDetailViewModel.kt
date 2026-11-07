package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.PacienteEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.rehabtrack.app.data.local.entity.ProgramaEntity

sealed interface PatientDetailUiState {
    data object Idle : PatientDetailUiState
    data object Loading : PatientDetailUiState

    data class Success(
        val paciente: PacienteEntity,
        val programa: ProgramaEntity?
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

    private val _uiState =
        MutableStateFlow<PatientDetailUiState>(
            PatientDetailUiState.Idle
        )

    val uiState: StateFlow<PatientDetailUiState> =
        _uiState.asStateFlow()

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

                    Pair(paciente, programa)
                }

                _uiState.value = PatientDetailUiState.Success(
                    paciente = resultado.first,
                    programa = resultado.second
                )

            } catch (e: Exception) {
                _uiState.value = PatientDetailUiState.Error(
                    e.message
                        ?: "No se pudo cargar la información del paciente."
                )
            }
        }
    }
}