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

data class PhysiotherapistProfileUiState(
    val nombreCompleto: String = "",
    val correo: String = "",
    val especialidad: String = "",
    val telefono: String = "",
    val cargando: Boolean = true,
    val mensajeError: String? = null
)

class PhysiotherapistProfileViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val usuarioRepository = provider.usuarioRepository
    private val fisioterapeutaRepository = provider.fisioterapeutaRepository

    private val _uiState = MutableStateFlow(PhysiotherapistProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarPerfil(idUsuario: Int) {
        viewModelScope.launch {

            _uiState.value = PhysiotherapistProfileUiState()

            try {
                val perfil = withContext(Dispatchers.IO) {

                    val usuario = usuarioRepository.obtenerUsuarioPorId(idUsuario)
                        ?: throw Exception("No se encontró el usuario.")

                    val fisioterapeuta =
                        fisioterapeutaRepository.obtenerFisioterapeutaPorUsuario(idUsuario)
                            ?: throw Exception("No se encontró el perfil del fisioterapeuta.")

                    PhysiotherapistProfileUiState(
                        nombreCompleto = fisioterapeuta.nombreCompleto,
                        correo = usuario.correo,
                        especialidad = fisioterapeuta.especialidad,
                        telefono = fisioterapeuta.telefono ?: "No registrado",
                        cargando = false
                    )
                }

                _uiState.value = perfil

            } catch (e: Exception) {
                _uiState.value = PhysiotherapistProfileUiState(
                    cargando = false,
                    mensajeError = e.message
                        ?: "No se pudo cargar el perfil del fisioterapeuta."
                )
            }
        }
    }
}