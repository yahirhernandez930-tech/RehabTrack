package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.rehabtrack.app.data.repository.EjercicioCatalogo

class EjercicioDebugViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    fun consultarEjercicios() {
        viewModelScope.launch {
            try {
                val ejercicios = withContext(Dispatchers.IO) {
                    provider.ejercicioRepository.obtenerTodos()
                }

                ejercicios.forEach { ejercicio ->
                    Log.d(
                        "REHABTRACK_EJERCICIOS",
                        """
                        ID: ${ejercicio.idEjercicio}
                        Nombre: ${ejercicio.nombre}
                        Descripción: ${ejercicio.descripcion}
                        Instrucciones: ${ejercicio.instrucciones}
                        -------------------------
                        """.trimIndent()
                    )
                }

                Log.d(
                    "REHABTRACK_EJERCICIOS",
                    "Total de ejercicios: ${ejercicios.size}"
                )

            } catch (e: Exception) {
                Log.e(
                    "REHABTRACK_EJERCICIOS",
                    "Error al consultar ejercicios",
                    e
                )
            }
        }
    }
    fun cargarCatalogo() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cantidad = EjercicioCatalogo.actualizarContenido(
                    provider.ejercicioRepository
                )

                Log.d(
                    "REHABTRACK_CATALOGO",
                    "Ejercicios actualizados: $cantidad de 7"
                )

            } catch (e: Exception) {
                Log.e(
                    "REHABTRACK_CATALOGO",
                    "Error al actualizar el catálogo",
                    e
                )
            }
        }
    }
}