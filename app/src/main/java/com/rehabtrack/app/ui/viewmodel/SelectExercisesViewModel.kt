package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.EjercicioEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectExercisesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val ejercicioRepository =
        RehabRepositoryProvider(application).ejercicioRepository

    private val _ejercicios =
        MutableStateFlow<List<EjercicioEntity>>(emptyList())

    val ejercicios: StateFlow<List<EjercicioEntity>> =
        _ejercicios.asStateFlow()

    private val _cargando = MutableStateFlow(true)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()

    init {
        cargarEjercicios()
    }

    // Consultar todos los ejercicios del catálogo.
    fun cargarEjercicios() {
        viewModelScope.launch {
            _cargando.value = true

            try {
                _ejercicios.value = withContext(Dispatchers.IO) {
                    ejercicioRepository.obtenerTodos()
                }
            } catch (e: Exception) {
                // El estado de carga se restablece en finally.
            } finally {
                _cargando.value = false
            }
        }
    }

    // Registrar un ejercicio nuevo.
    fun registrarEjercicio(
        nombre: String,
        descripcion: String,
        instrucciones: String,
        videoUrl: String,
        onResultado: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val ejerciciosActualizados = withContext(Dispatchers.IO) {

                    val catalogo = ejercicioRepository.obtenerTodos()

                    val duplicado = catalogo.any {
                        it.nombre.trim().equals(
                            nombre.trim(),
                            ignoreCase = true
                        )
                    }

                    if (duplicado) {
                        throw Exception(
                            "Ya existe un ejercicio con ese nombre."
                        )
                    }

                    ejercicioRepository.insertar(
                        EjercicioEntity(
                            nombre = nombre.trim(),
                            descripcion = descripcion.trim(),
                            instrucciones = instrucciones.trim(),
                            videoUrl = videoUrl.trim().ifBlank { null }
                        )
                    )

                    ejercicioRepository.obtenerTodos()
                }

                _ejercicios.value = ejerciciosActualizados

                onResultado(null)

            } catch (e: Exception) {

                onResultado(
                    e.message ?: "No se pudo registrar el ejercicio."
                )
            }
        }
    }


    // Actualizar un ejercicio existente.
    fun actualizarEjercicio(
        ejercicio: EjercicioEntity,
        nombre: String,
        descripcion: String,
        instrucciones: String,
        videoUrl: String,
        onResultado: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val ejerciciosActualizados = withContext(Dispatchers.IO) {

                    val catalogo = ejercicioRepository.obtenerTodos()

                    val duplicado = catalogo.any {
                        it.idEjercicio != ejercicio.idEjercicio &&
                                it.nombre.trim().equals(
                                    nombre.trim(),
                                    ignoreCase = true
                                )
                    }

                    if (duplicado) {
                        throw Exception(
                            "Ya existe otro ejercicio con ese nombre."
                        )
                    }

                    ejercicioRepository.actualizar(
                        ejercicio.copy(
                            nombre = nombre.trim(),
                            descripcion = descripcion.trim(),
                            instrucciones = instrucciones.trim(),
                            videoUrl = videoUrl.trim().ifBlank { null }
                        )
                    )

                    ejercicioRepository.obtenerTodos()
                }

                _ejercicios.value = ejerciciosActualizados

                onResultado(null)

            } catch (e: Exception) {

                onResultado(
                    e.message ?: "No se pudo actualizar el ejercicio."
                )
            }
        }
    }
}