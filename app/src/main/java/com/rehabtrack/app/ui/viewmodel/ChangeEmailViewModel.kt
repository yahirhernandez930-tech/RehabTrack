package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChangeEmailUiState(
    val correoActual: String = "",
    val cargando: Boolean = true,
    val guardando: Boolean = false,
    val mensajeError: String? = null,
    val mensajeExito: String? = null
)

class ChangeEmailViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)
    private val usuarioRepository = provider.usuarioRepository

    private val _uiState = MutableStateFlow(ChangeEmailUiState())
    val uiState = _uiState.asStateFlow()

    fun cargarCorreo(idUsuario: Int) {
        viewModelScope.launch {
            try {
                val usuario = withContext(Dispatchers.IO) {
                    usuarioRepository.obtenerUsuarioPorId(idUsuario)
                }

                if (usuario == null) {
                    throw Exception("No se encontró el usuario.")
                }

                _uiState.value = ChangeEmailUiState(
                    correoActual = usuario.correo,
                    cargando = false
                )

            } catch (e: Exception) {
                _uiState.value = ChangeEmailUiState(
                    cargando = false,
                    mensajeError = e.message
                        ?: "No se pudo cargar el correo."
                )
            }
        }
    }

    fun cambiarCorreo(
        idUsuario: Int,
        nuevoCorreo: String
    ) {
        if (_uiState.value.guardando) return

        val correoNormalizado = nuevoCorreo.trim().lowercase()
        val correoActual = _uiState.value.correoActual

        if (correoNormalizado.isBlank()) {
            _uiState.value = _uiState.value.copy(
                mensajeError = "Introduce un nuevo correo electrónico.",
                mensajeExito = null
            )
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correoNormalizado).matches()) {
            _uiState.value = _uiState.value.copy(
                mensajeError = "El correo electrónico no es válido.",
                mensajeExito = null
            )
            return
        }

        if (correoNormalizado == correoActual) {
            _uiState.value = _uiState.value.copy(
                mensajeError = "El nuevo correo debe ser diferente al actual.",
                mensajeExito = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                guardando = true,
                mensajeError = null,
                mensajeExito = null
            )

            try {
                withContext(Dispatchers.IO) {

                    val usuario = usuarioRepository
                        .obtenerUsuarioPorId(idUsuario)
                        ?: throw Exception("No se encontró el usuario.")

                    val correoExistente = usuarioRepository
                        .obtenerUsuarioPorCorreo(correoNormalizado)

                    if (correoExistente != null &&
                        correoExistente.idUsuario != idUsuario
                    ) {
                        throw Exception(
                            "Este correo electrónico ya está registrado."
                        )
                    }

                    usuarioRepository.actualizarUsuario(
                        usuario.copy(correo = correoNormalizado)
                    )
                }

                _uiState.value = _uiState.value.copy(
                    correoActual = correoNormalizado,
                    guardando = false,
                    mensajeExito = "Correo electrónico actualizado correctamente."
                )

            } catch (e: Exception) {
                val mensaje = when {
                    e is SQLiteConstraintException ||
                            e.cause is SQLiteConstraintException ->
                        "Este correo electrónico ya está registrado."

                    else -> e.message
                        ?: "No se pudo actualizar el correo."
                }

                _uiState.value = _uiState.value.copy(
                    guardando = false,
                    mensajeError = mensaje
                )
            }
        }
    }
}