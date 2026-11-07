package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.PacienteEntity
import com.rehabtrack.app.data.local.entity.ProgramaEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val paciente: PacienteEntity,
        val programa: ProgramaEntity?,
        val ejercicios: List<EjercicioInicioData>
    ) : HomeUiState

    data class Error(
        val mensaje: String
    ) : HomeUiState

    data class EjercicioInicioData(
        val nombre: String,
        val descripcion: String,
        val instrucciones: String,
        val series: Int,
        val repeticiones: Int
    )
}

class HomeViewModel(application: Application) :
    AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val pacienteRepository = provider.pacienteRepository
    private val programaRepository = provider.programaRepository

    private val ejercicioRepository = provider.ejercicioRepository

    private val programaEjercicioRepository =
        provider.programaEjercicioRepository

    private val _uiState =
        MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    val uiState: StateFlow<HomeUiState> =
        _uiState.asStateFlow()

    fun cargarInicio(idUsuario: Int) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            try {
                val resultado = withContext(Dispatchers.IO) {

                    val paciente =
                        pacienteRepository.obtenerPacientePorUsuario(idUsuario)
                            ?: throw Exception(
                                "No se encontró el perfil del paciente."
                            )

                    val programa =
                        programaRepository.obtenerProgramaActivo(
                            paciente.idPaciente
                        )

                    val ejercicios = if (programa != null) {

                        programaEjercicioRepository
                            .obtenerEjerciciosPrograma(programa.idPrograma)
                            .mapNotNull { asignacion ->

                                val ejercicio =
                                    ejercicioRepository.obtenerPorId(
                                        asignacion.idEjercicio
                                    )

                                ejercicio?.let {
                                    HomeUiState.EjercicioInicioData(
                                        nombre = it.nombre,
                                        descripcion = it.descripcion,
                                        instrucciones = it.instrucciones,
                                        series = asignacion.series,
                                        repeticiones = asignacion.repeticiones
                                    )
                                }
                            }

                    } else {
                        emptyList()
                    }

                    Triple(paciente, programa, ejercicios)
                }

                _uiState.value = HomeUiState.Success(
                    paciente = resultado.first,
                    programa = resultado.second,
                    ejercicios = resultado.third
                )

            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    e.message ?: "No se pudo cargar la información."
                )
            }
        }
    }
}