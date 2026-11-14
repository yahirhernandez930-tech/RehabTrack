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

data class ProfileUiState(
    val nombreCompleto: String = "",
    val correo: String = "",
    val cargando: Boolean = true,
    val mensajeError: String? = null
)

class ProfileViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)

    private val usuarioRepository = provider.usuarioRepository
    private val pacienteRepository = provider.pacienteRepository

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarPerfil(idUsuario: Int) {
        viewModelScope.launch {

            _uiState.value = ProfileUiState()

            try {
                val perfil = withContext(Dispatchers.IO) {

                    val usuario = usuarioRepository
                        .obtenerUsuarioPorId(idUsuario)
                        ?: throw Exception("No se encontró el usuario.")

                    val paciente = pacienteRepository
                        .obtenerPacientePorUsuario(idUsuario)
                        ?: throw Exception("No se encontró el perfil del paciente.")

                    ProfileUiState(
                        nombreCompleto = paciente.nombre,
                        correo = usuario.correo,
                        cargando = false
                    )
                }

                _uiState.value = perfil

            } catch (e: Exception) {
                _uiState.value = ProfileUiState(
                    cargando = false,
                    mensajeError = e.message
                        ?: "No se pudo cargar el perfil."
                )
            }
        }
    }
}