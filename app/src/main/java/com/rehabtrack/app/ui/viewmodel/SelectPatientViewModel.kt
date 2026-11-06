package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.AsignacionEntity
import com.rehabtrack.app.data.local.entity.PacienteEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

data class SelectPatientUiState(
    val pacientes: List<PacienteEntity> = emptyList(),
    val cargando: Boolean = false,
    val asignando: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null
)

class SelectPatientViewModel(application: Application) :
    AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val fisioterapeutaRepository =
        provider.fisioterapeutaRepository

    private val asignacionRepository =
        provider.asignacionRepository

    private val _uiState = MutableStateFlow(SelectPatientUiState())
    val uiState: StateFlow<SelectPatientUiState> =
        _uiState.asStateFlow()

    fun cargarPacientes() {
        if (_uiState.value.cargando || _uiState.value.asignando) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                cargando = true,
                error = null,
                mensaje = null
            )

            try {
                val pacientes = withContext(Dispatchers.IO) {
                    asignacionRepository.obtenerPacientesDisponibles()
                }

                _uiState.value = _uiState.value.copy(
                    pacientes = pacientes,
                    cargando = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error = "No se pudieron cargar los pacientes."
                )
            }
        }
    }

    fun asignarPaciente(
        idUsuario: Int,
        idPaciente: Int
    ) {
        if (_uiState.value.asignando || _uiState.value.cargando) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                asignando = true,
                error = null,
                mensaje = null
            )

            try {
                withContext(Dispatchers.IO) {

                    // Obtener el perfil del fisioterapeuta.
                    val fisioterapeuta =
                        fisioterapeutaRepository
                            .obtenerFisioterapeutaPorUsuario(idUsuario)
                            ?: throw Exception(
                                "No se encontró el perfil del fisioterapeuta."
                            )

                    // Comprobar que el paciente siga disponible.
                    val asignacionActual =
                        asignacionRepository
                            .obtenerAsignacionActiva(idPaciente)

                    if (asignacionActual != null) {
                        throw Exception(
                            "Este paciente ya tiene un fisioterapeuta asignado."
                        )
                    }

                    // Crear la asignación.
                    val asignacion = AsignacionEntity(
                        idPaciente = idPaciente,
                        idFisioterapeuta =
                            fisioterapeuta.idFisioterapeuta,
                        fechaInicio = LocalDate.now().toString(),
                        activa = true
                    )

                    asignacionRepository.insertar(asignacion)
                }

                // Actualizar la lista de pacientes disponibles.
                val pacientesActualizados = withContext(Dispatchers.IO) {
                    asignacionRepository.obtenerPacientesDisponibles()
                }

                _uiState.value = _uiState.value.copy(
                    pacientes = pacientesActualizados,
                    asignando = false,
                    mensaje = "Paciente asignado correctamente."
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    asignando = false,
                    error = e.message
                        ?: "No se pudo asignar el paciente."
                )
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.value = _uiState.value.copy(
            mensaje = null,
            error = null
        )
    }
}