package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.EjercicioEntity
import com.rehabtrack.app.data.local.entity.ProgramaEntity
import com.rehabtrack.app.data.local.entity.ProgramaEjercicioEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

data class ManageProgramUiState(
    val nombrePaciente: String = "",
    val cargandoPaciente: Boolean = true,
    val guardando: Boolean = false,
    val programaExistente: ProgramaEntity? = null,
    val ejerciciosPrograma: List<EjercicioAsignadoData> = emptyList(),
    val mensajeError: String? = null,
    val mensajeExito: String? = null
)

data class EjercicioAsignadoData(
    val nombre: String,
    val series: Int,
    val repeticiones: Int
)

class ManageProgramViewModel(application: Application) :
    AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val pacienteRepository = provider.pacienteRepository
    private val fisioterapeutaRepository = provider.fisioterapeutaRepository
    private val asignacionRepository = provider.asignacionRepository
    private val programaRepository = provider.programaRepository
    private val ejercicioRepository = provider.ejercicioRepository
    private val programaEjercicioRepository =
        provider.programaEjercicioRepository

    private val _uiState = MutableStateFlow(ManageProgramUiState())

    val uiState: StateFlow<ManageProgramUiState> =
        _uiState.asStateFlow()

    // Carga los datos del paciente y su programa activo.
    fun cargarPaciente(idUsuario: Int, idPaciente: Int) {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                cargandoPaciente = true,
                mensajeError = null
            )

            try {
                val resultado = withContext(Dispatchers.IO) {

                    val fisioterapeuta =
                        fisioterapeutaRepository
                            .obtenerFisioterapeutaPorUsuario(idUsuario)
                            ?: throw Exception(
                                "No se encontró el perfil del fisioterapeuta."
                            )

                    val asignacion =
                        asignacionRepository
                            .obtenerAsignacionActiva(idPaciente)

                    if (
                        asignacion == null ||
                        asignacion.idFisioterapeuta !=
                        fisioterapeuta.idFisioterapeuta
                    ) {
                        throw Exception(
                            "Este paciente no está asignado a tu cuenta."
                        )
                    }

                    val paciente =
                        pacienteRepository.obtenerPacientePorId(idPaciente)
                            ?: throw Exception(
                                "No se encontró el paciente."
                            )

                    val programa =
                        programaRepository.obtenerProgramaActivo(idPaciente)

                    val ejerciciosAsignados =
                        if (programa != null) {

                            val relaciones =
                                programaEjercicioRepository
                                    .obtenerEjerciciosPrograma(
                                        programa.idPrograma
                                    )

                            relaciones.map { relacion ->

                                val ejercicio =
                                    ejercicioRepository.obtenerPorId(
                                        relacion.idEjercicio
                                    ) ?: throw Exception(
                                        "No se encontró un ejercicio asociado al programa."
                                    )

                                EjercicioAsignadoData(
                                    nombre = ejercicio.nombre,
                                    series = relacion.series,
                                    repeticiones = relacion.repeticiones
                                )
                            }

                        } else {
                            emptyList()
                        }

                    Triple(
                        paciente.nombre,
                        programa,
                        ejerciciosAsignados
                    )
                }

                _uiState.value = _uiState.value.copy(
                    nombrePaciente = resultado.first,
                    programaExistente = resultado.second,
                    ejerciciosPrograma = resultado.third,
                    cargandoPaciente = false
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    cargandoPaciente = false,
                    mensajeError = e.message
                        ?: "No se pudo cargar la información del paciente."
                )
            }
        }
    }

    // Inserta un programa nuevo o actualiza el existente.
    fun guardarPrograma(
        idUsuario: Int,
        idPaciente: Int,
        nombrePrograma: String,
        duracionSesiones: Int,
        ejercicios: List<EjercicioAsignadoData>
    ) {
        if (_uiState.value.guardando) return

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                guardando = true,
                mensajeError = null,
                mensajeExito = null
            )

            try {
                val resultado = withContext(Dispatchers.IO) {

                    provider.ejecutarTransaccion {

                        val fisioterapeuta =
                            fisioterapeutaRepository
                                .obtenerFisioterapeutaPorUsuario(idUsuario)
                                ?: throw Exception(
                                    "No se encontró el fisioterapeuta."
                                )

                        val asignacion =
                            asignacionRepository
                                .obtenerAsignacionActiva(idPaciente)

                        if (
                            asignacion == null ||
                            asignacion.idFisioterapeuta !=
                            fisioterapeuta.idFisioterapeuta
                        ) {
                            throw Exception(
                                "El paciente ya no está asignado a tu cuenta."
                            )
                        }

                        if (
                            pacienteRepository.obtenerPacientePorId(
                                idPaciente
                            ) == null
                        ) {
                            throw Exception(
                                "El paciente ya no existe."
                            )
                        }

                        // Comprobar si ya existe un programa activo.
                        val programaExistente =
                            programaRepository.obtenerProgramaActivo(
                                idPaciente
                            )

                        val programa: ProgramaEntity
                        val idPrograma: Int

                        if (programaExistente != null) {

                            // Actualizar el programa conservando su ID y fecha.
                            programa = programaExistente.copy(
                                nombre = nombrePrograma.trim(),
                                duracionSesiones = duracionSesiones
                            )

                            programaRepository.actualizar(programa)

                            idPrograma = programa.idPrograma

                            // Eliminar las relaciones anteriores.
                            programaEjercicioRepository
                                .eliminarEjerciciosPrograma(idPrograma)

                        } else {

                            // Crear un programa nuevo.
                            programa = ProgramaEntity(
                                idPaciente = idPaciente,
                                idFisioterapeuta =
                                    fisioterapeuta.idFisioterapeuta,
                                nombre = nombrePrograma.trim(),
                                duracionSesiones = duracionSesiones,
                                estado = "Activo",
                                fechaCreacion = LocalDate.now().toString()
                            )

                            idPrograma =
                                programaRepository.insertar(programa).toInt()
                        }

                        // Recuperar el catálogo de ejercicios.
                        val catalogoEjercicios =
                            ejercicioRepository.obtenerTodos()

                        ejercicios.forEach { ejercicio ->

                            val existente =
                                catalogoEjercicios.firstOrNull {
                                    it.nombre.equals(
                                        ejercicio.nombre.trim(),
                                        ignoreCase = true
                                    )
                                }

                            val idEjercicio =
                                if (existente != null) {
                                    existente.idEjercicio
                                } else {
                                    ejercicioRepository.insertar(
                                        EjercicioEntity(
                                            nombre = ejercicio.nombre.trim(),
                                            descripcion = "",
                                            instrucciones = ""
                                        )
                                    ).toInt()
                                }

                            // Registrar la relación con el programa.
                            programaEjercicioRepository.insertar(
                                ProgramaEjercicioEntity(
                                    idPrograma = idPrograma,
                                    idEjercicio = idEjercicio,
                                    series = ejercicio.series,
                                    repeticiones = ejercicio.repeticiones
                                )
                            )
                        }

                        Pair(
                            programa,
                            ejercicios
                        )
                    }
                }

                _uiState.value = _uiState.value.copy(
                    guardando = false,
                    programaExistente = resultado.first,
                    ejerciciosPrograma = resultado.second,
                    mensajeExito = "El programa se guardó correctamente."
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    guardando = false,
                    mensajeError = e.message
                        ?: "No se pudo guardar el programa."
                )
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.value = _uiState.value.copy(
            mensajeError = null,
            mensajeExito = null
        )
    }
}