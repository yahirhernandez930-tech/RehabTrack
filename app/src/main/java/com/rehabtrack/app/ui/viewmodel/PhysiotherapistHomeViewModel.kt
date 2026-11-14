package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.SesionEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ProximaSesionHomeData(
    val idSesion: Int,
    val idPaciente: Int,
    val nombrePaciente: String,
    val nombrePrograma: String,
    val fecha: String,
    val hora: String
)

data class AtencionPendienteData(
    val idPaciente: Int,
    val nombrePaciente: String,
    val nivelDolor: Int,
    val fechaSesion: String
)

sealed interface PhysiotherapistHomeUiState {
    data object Idle : PhysiotherapistHomeUiState
    data object Loading : PhysiotherapistHomeUiState

    data class Success(
        val pacientesActivos: Int,
        val sesionesHoy: Int,
        val pacientesRevision: List<AtencionPendienteData>,
        val programasActivos: Int,
        val proximasSesiones: List<ProximaSesionHomeData>
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

    private val pacienteRepository =
        provider.pacienteRepository

    private val programaRepository =
        provider.programaRepository

    private val sesionRepository =
        provider.sesionRepository

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
                val resultado = withContext(Dispatchers.IO) {

                    val fisioterapeuta =
                        fisioterapeutaRepository
                            .obtenerFisioterapeutaPorUsuario(idUsuario)
                            ?: throw Exception(
                                "No se encontró el perfil del fisioterapeuta."
                            )

                    // Recuperar las asignaciones activas
                    val asignaciones =
                        asignacionRepository.obtenerPacientesAsignados(
                            fisioterapeuta.idFisioterapeuta
                        )

                    // Recuperar los datos completos de cada paciente
                    val pacientes = asignaciones
                        .mapNotNull { asignacion ->
                            pacienteRepository.obtenerPacientePorId(
                                asignacion.idPaciente
                            )
                        }
                        .distinctBy { it.idPaciente }

                    // Programas del fisioterapeuta
                    val programas =
                        programaRepository.obtenerProgramasFisioterapeuta(
                            fisioterapeuta.idFisioterapeuta
                        )

                    val programasActivos = programas.filter {
                        it.estado.equals("Activo", ignoreCase = true)
                    }

                    // Recuperar las sesiones de todos sus programas
                    val sesionesPorPrograma = programas.associate { programa ->
                        programa.idPrograma to
                                sesionRepository.obtenerSesionesPrograma(
                                    programa.idPrograma
                                )
                    }

                    val sesionesConPrograma = programas.flatMap { programa ->
                        sesionesPorPrograma[programa.idPrograma].orEmpty()
                            .map { sesion ->
                                programa to sesion
                            }
                    }

                    val formatoFecha = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    )

                    val formatoFechaHora = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm",
                        Locale.getDefault()
                    )

                    val ahora = Date()
                    val fechaHoy = formatoFecha.format(ahora)

                    // Sesiones programadas para hoy.
                    val sesionesHoy = sesionesConPrograma.count { (_, sesion) ->
                        sesion.fecha == fechaHoy &&
                                !sesion.estado.equals(
                                    "Cancelada",
                                    ignoreCase = true
                                )
                    }

                    // Próximas sesiones pendientes.
                    val proximasSesiones = sesionesConPrograma
                        .filter { (_, sesion) ->
                            sesion.estado.equals(
                                "Pendiente",
                                ignoreCase = true
                            ) &&
                                    runCatching {
                                        val fecha = formatoFechaHora.parse(
                                            "${sesion.fecha} ${sesion.hora}"
                                        )
                                        fecha != null && !fecha.before(ahora)
                                    }.getOrDefault(false)
                        }
                        .sortedWith(
                            compareBy<Pair<com.rehabtrack.app.data.local.entity.ProgramaEntity, SesionEntity>> {
                                it.second.fecha
                            }.thenBy {
                                it.second.hora
                            }
                        )
                        .take(3)
                        .mapNotNull { (programa, sesion) ->

                            val paciente = pacientes.firstOrNull {
                                it.idPaciente == programa.idPaciente
                            } ?: return@mapNotNull null

                            ProximaSesionHomeData(
                                idSesion = sesion.idSesion,
                                idPaciente = paciente.idPaciente,
                                nombrePaciente = paciente.nombre,
                                nombrePrograma = programa.nombre,
                                fecha = sesion.fecha,
                                hora = sesion.hora
                            )
                        }

                    // Atención pendiente:
                    // última sesión finalizada de cada paciente.
                    val pacientesRevision = pacientes.mapNotNull { paciente ->

                        val sesionesPaciente = sesionesConPrograma
                            .filter { (programa, _) ->
                                programa.idPaciente == paciente.idPaciente
                            }
                            .map { it.second }
                            .filter {
                                it.estado.equals(
                                    "Finalizada",
                                    ignoreCase = true
                                )
                            }

                        val ultimaSesion = sesionesPaciente.maxWithOrNull(
                            compareBy<SesionEntity> { it.fecha }
                                .thenBy { it.hora }
                        )

                        if (
                            ultimaSesion != null &&
                            ultimaSesion.nivelDolor != null &&
                            ultimaSesion.nivelDolor >= 7
                        ) {
                            AtencionPendienteData(
                                idPaciente = paciente.idPaciente,
                                nombrePaciente = paciente.nombre,
                                nivelDolor = ultimaSesion.nivelDolor,
                                fechaSesion = ultimaSesion.fecha
                            )
                        } else {
                            null
                        }
                    }

                    PhysiotherapistHomeUiState.Success(
                        pacientesActivos = pacientes.size,
                        sesionesHoy = sesionesHoy,
                        pacientesRevision = pacientesRevision,
                        programasActivos = programasActivos.size,
                        proximasSesiones = proximasSesiones
                    )
                }

                _uiState.value = resultado

            } catch (e: Exception) {
                _uiState.value =
                    PhysiotherapistHomeUiState.Error(
                        e.message ?: "No se pudo cargar el resumen."
                    )
            }
        }
    }
}
