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
import com.rehabtrack.app.data.local.entity.SesionEntity

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val paciente: PacienteEntity,
        val programa: ProgramaEntity?,
        val ejercicios: List<EjercicioInicioData>,
        val proximaSesion: SesionEntity?,
        val progreso: Int = 0,
        val sesionesRecientes: List<SesionEntity> = emptyList(),
        val ultimoNivelDolor: Int? = null
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

    private val sesionRepository =
        provider.sesionRepository

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

                    // Recuperar todas las sesiones del programa
                    val sesionesPrograma = if (programa != null) {
                        sesionRepository.obtenerSesionesPrograma(
                            programa.idPrograma
                        )
                    } else {
                        emptyList()
                    }

                    // Identificar las sesiones finalizadas
                    val sesionesFinalizadas = sesionesPrograma.filter {
                        it.estado.equals("Finalizada", ignoreCase = true)
                    }

                    // Calcular el progreso del programa
                    val progreso = if (
                        programa != null &&
                        programa.duracionSesiones > 0
                    ) {
                        (
                                sesionesFinalizadas.size * 100f /
                                        programa.duracionSesiones
                                ).toInt().coerceIn(0, 100)
                    } else {
                        0
                    }

                    // Obtener las tres sesiones finalizadas más recientes
                    val sesionesRecientes = sesionesFinalizadas
                        .sortedWith(
                            compareByDescending<SesionEntity> { it.fecha }
                                .thenByDescending { it.hora }
                        )
                        .take(3)

                    // Obtener el último nivel de dolor registrado
                    val ultimoNivelDolor = sesionesFinalizadas
                        .filter { it.nivelDolor != null }
                        .maxWithOrNull(
                            compareBy<SesionEntity> { it.fecha }
                                .thenBy { it.hora }
                        )
                        ?.nivelDolor

                    // Obtener la próxima sesión pendiente
                    val proximaSesion = if (programa != null) {

                        val sesionesPendientes =
                            sesionRepository.obtenerSesionesPendientes(
                                programa.idPrograma
                            )

                        sesionesPendientes.minWithOrNull(
                            compareBy<SesionEntity> { it.fecha }
                                .thenBy { it.hora }
                        )

                    } else {
                        null
                    }

                    HomeUiState.Success(
                        paciente = paciente,
                        programa = programa,
                        ejercicios = ejercicios,
                        proximaSesion = proximaSesion,
                        progreso = progreso,
                        sesionesRecientes = sesionesRecientes,
                        ultimoNivelDolor = ultimoNivelDolor
                    )
                }

                _uiState.value = resultado

            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    e.message ?: "No se pudo cargar la información."
                )
            }
        }
    }
}