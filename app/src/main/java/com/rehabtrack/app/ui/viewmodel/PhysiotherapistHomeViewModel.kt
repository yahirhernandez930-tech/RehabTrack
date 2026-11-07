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

sealed interface PhysiotherapistHomeUiState {
    data object Idle : PhysiotherapistHomeUiState
    data object Loading : PhysiotherapistHomeUiState

    data class Success(
        val pacientesActivos: Int
    ) : PhysiotherapistHomeUiState

    data class Error(
        val mensaje: String
    ) : PhysiotherapistHomeUiState
}

class PhysiotherapistHomeViewModel(application: Application) :
    AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val fisioterapeutaRepository =
        provider.fisioterapeutaRepository

    private val asignacionRepository =
        provider.asignacionRepository

    private val _uiState =
        MutableStateFlow<PhysiotherapistHomeUiState>(
            PhysiotherapistHomeUiState.Idle
        )

    val uiState: StateFlow<PhysiotherapistHomeUiState> =
        _uiState.asStateFlow()

    fun cargarResumen(idUsuario: Int) {

        if (_uiState.value == PhysiotherapistHomeUiState.Loading) return

        _uiState.value = PhysiotherapistHomeUiState.Loading

        viewModelScope.launch {
            try {
                val cantidadPacientes = withContext(Dispatchers.IO) {

                    // 1. Obtener el perfil del fisioterapeuta.
                    val fisioterapeuta =
                        fisioterapeutaRepository
                            .obtenerFisioterapeutaPorUsuario(idUsuario)
                            ?: throw Exception(
                                "No se encontró el perfil del fisioterapeuta."
                            )

                    // 2. Consultar sus pacientes asignados.
                    val pacientes =
                        asignacionRepository.obtenerPacientesAsignados(
                            fisioterapeuta.idFisioterapeuta
                        )

                    // 3. Obtener la cantidad.
                    pacientes.size
                }

                _uiState.value =
                    PhysiotherapistHomeUiState.Success(
                        pacientesActivos = cantidadPacientes
                    )

            } catch (e: Exception) {
                _uiState.value =
                    PhysiotherapistHomeUiState.Error(
                        e.message
                            ?: "No se pudo cargar el resumen."
                    )
            }
        }
    }
}
