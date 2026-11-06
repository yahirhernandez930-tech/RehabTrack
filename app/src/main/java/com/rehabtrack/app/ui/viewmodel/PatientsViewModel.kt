package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PacienteAsignadoUi(
    val idPaciente: Int,
    val nombre: String,
    val fechaNacimiento: String
)

sealed interface PatientsUiState {
    data object Idle : PatientsUiState
    data object Loading : PatientsUiState

    data class Success(
        val pacientes: List<PacienteAsignadoUi>
    ) : PatientsUiState

    data class Error(
        val mensaje: String
    ) : PatientsUiState
}

class PatientsViewModel(application: Application) :
    AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val fisioterapeutaRepository =
        provider.fisioterapeutaRepository

    private val asignacionRepository =
        provider.asignacionRepository

    private val pacienteRepository =
        provider.pacienteRepository

    private val _uiState =
        MutableStateFlow<PatientsUiState>(PatientsUiState.Idle)

    val uiState: StateFlow<PatientsUiState> =
        _uiState.asStateFlow()

    fun cargarPacientes(idUsuario: Int) {
        if (_uiState.value == PatientsUiState.Loading) return

        _uiState.value = PatientsUiState.Loading

        viewModelScope.launch {
            try {
                val pacientes = withContext(Dispatchers.IO) {

                    // 1. Obtener el perfil del fisioterapeuta.
                    val fisioterapeuta =
                        fisioterapeutaRepository
                            .obtenerFisioterapeutaPorUsuario(idUsuario)
                            ?: throw Exception(
                                "No se encontró el perfil del fisioterapeuta"
                            )

                    // 2. Consultar sus asignaciones activas.
                    val asignaciones =
                        asignacionRepository
                            .obtenerPacientesAsignados(
                                fisioterapeuta.idFisioterapeuta
                            )

                    // 3. Recuperar los perfiles de los pacientes.
                    asignaciones.mapNotNull { asignacion ->

                        pacienteRepository.obtenerPacientePorId(
                            asignacion.idPaciente
                        )?.let { paciente ->

                            PacienteAsignadoUi(
                                idPaciente = paciente.idPaciente,
                                nombre = paciente.nombre,
                                fechaNacimiento = paciente.fechaNacimiento
                            )
                        }
                    }.sortedBy { it.nombre }
                }

                _uiState.value = PatientsUiState.Success(pacientes)

            } catch (e: Exception) {
                _uiState.value = PatientsUiState.Error(
                    e.message ?: "No se pudieron cargar los pacientes"
                )
            }
        }
    }
}