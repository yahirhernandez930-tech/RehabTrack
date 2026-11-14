package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PainProgressPoint(
    val etiqueta: String,
    val nivelDolor: Float
)

data class ProgressUiState(
    val nombrePrograma: String = "",
    val porcentajeCompletado: Int = 0,
    val sesionesCompletadas: Int = 0,
    val totalSesiones: Int = 0,
    val ejerciciosCompletados: Int = 0,
    val evolucionDolor: List<PainProgressPoint> = emptyList(),
    val cargando: Boolean = true,
    val mensajeError: String? = null
)

class ProgressViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val pacienteRepository = provider.pacienteRepository
    private val programaRepository = provider.programaRepository
    private val sesionRepository = provider.sesionRepository
    private val programaEjercicioRepository =
        provider.programaEjercicioRepository
    private val registroSerieRepository =
        provider.registroSerieRepository

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarProgreso(idUsuario: Int) {

        viewModelScope.launch {

            _uiState.value = ProgressUiState()

            try {

                val progreso = withContext(Dispatchers.IO) {

                    // Obtener paciente asociado al usuario.
                    val paciente = pacienteRepository
                        .obtenerPacientePorUsuario(idUsuario)
                        ?: throw Exception("No se encontró el paciente.")

                    // Obtener programa activo.
                    val programa = programaRepository
                        .obtenerProgramaActivo(paciente.idPaciente)

                    if (programa == null) {
                        return@withContext ProgressUiState(
                            cargando = false
                        )
                    }

                    // Obtener sesiones del programa.
                    val sesiones = sesionRepository
                        .obtenerSesionesPrograma(programa.idPrograma)
                        .sortedWith(
                            compareBy({ it.fecha }, { it.hora })
                        )

                    // Filtrar sesiones finalizadas.
                    val sesionesFinalizadas = sesiones.filter {
                        it.estado.equals("Finalizada", ignoreCase = true)
                    }

                    val totalSesiones = programa.duracionSesiones
                        .coerceAtLeast(0)

                    val sesionesCompletadas = sesionesFinalizadas.size

                    // Calcular porcentaje del programa.
                    val porcentaje = if (totalSesiones > 0) {
                        (
                                sesionesCompletadas * 100f /
                                        totalSesiones
                                ).toInt().coerceIn(0, 100)
                    } else {
                        0
                    }

                    // Obtener ejercicios asignados al programa.
                    val asignaciones = programaEjercicioRepository
                        .obtenerEjerciciosPrograma(programa.idPrograma)

                    var ejerciciosCompletados = 0

                    // Comprobar las series registradas de cada ejercicio
                    // en cada sesión finalizada.
                    sesionesFinalizadas.forEach { sesion ->

                        val registros = registroSerieRepository
                            .obtenerRegistrosSesion(sesion.idSesion)

                        asignaciones.forEach { asignacion ->

                            val registrosEjercicio = registros.filter {
                                it.idEjercicio == asignacion.idEjercicio
                            }

                            val seriesCompletas =
                                asignacion.series > 0 &&
                                        (1..asignacion.series).all { numeroSerie ->
                                            registrosEjercicio.any {
                                                it.numeroSerie == numeroSerie
                                            }
                                        }

                            if (seriesCompletas) {
                                ejerciciosCompletados++
                            }
                        }
                    }

                    // Obtener evolución del dolor.
                    val evolucionDolor = sesionesFinalizadas
                        .filter { it.nivelDolor != null }
                        .map { sesion ->
                            PainProgressPoint(
                                etiqueta = try {
                                    val partes = sesion.fecha.split("-")
                                    "${partes[2]}/${partes[1]}"
                                } catch (e: Exception) {
                                    sesion.fecha
                                },
                                nivelDolor = sesion.nivelDolor!!.toFloat()
                            )
                        }

                    ProgressUiState(
                        nombrePrograma = programa.nombre,
                        porcentajeCompletado = porcentaje,
                        sesionesCompletadas = sesionesCompletadas,
                        totalSesiones = totalSesiones,
                        ejerciciosCompletados = ejerciciosCompletados,
                        evolucionDolor = evolucionDolor,
                        cargando = false
                    )
                }

                _uiState.value = progreso

            } catch (e: Exception) {

                _uiState.value = ProgressUiState(
                    cargando = false,
                    mensajeError = e.message
                        ?: "No se pudo cargar el progreso del paciente."
                )
            }
        }
    }
}