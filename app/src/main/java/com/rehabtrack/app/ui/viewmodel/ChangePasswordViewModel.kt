package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import com.rehabtrack.app.data.security.PasswordUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChangePasswordUiState(
    val guardando: Boolean = false,
    val mensajeError: String? = null,
    val mensajeExito: String? = null
)

class ChangePasswordViewModel(application: Application) :
    AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)
    private val usuarioRepository = provider.usuarioRepository

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun cambiarPassword(
        idUsuario: Int,
        passwordActual: String,
        nuevaPassword: String,
        confirmarPassword: String
    ) {
        if (_uiState.value.guardando) return

        // Validaciones iniciales
        if (passwordActual.isBlank() ||
            nuevaPassword.isBlank() ||
            confirmarPassword.isBlank()
        ) {
            mostrarError("Completa todos los campos.")
            return
        }

        if (nuevaPassword.length !in 8..12) {
            mostrarError("La contraseña debe tener entre 8 y 12 caracteres.")
            return
        }

        if (!nuevaPassword.any { it.isUpperCase() }) {
            mostrarError("La contraseña debe contener una mayúscula.")
            return
        }

        if (!nuevaPassword.any { it.isLowerCase() }) {
            mostrarError("La contraseña debe contener una minúscula.")
            return
        }

        if (!nuevaPassword.any { it.isDigit() }) {
            mostrarError("La contraseña debe contener un número.")
            return
        }

        if (!nuevaPassword.any {
                !it.isLetterOrDigit() && !it.isWhitespace()
            }
        ) {
            mostrarError("La contraseña debe contener un símbolo.")
            return
        }

        if (nuevaPassword != confirmarPassword) {
            mostrarError("Las contraseñas nuevas no coinciden.")
            return
        }

        viewModelScope.launch {
            _uiState.value = ChangePasswordUiState(
                guardando = true
            )

            try {
                withContext(Dispatchers.IO) {

                    val usuario = usuarioRepository
                        .obtenerUsuarioPorId(idUsuario)
                        ?: throw Exception("No se encontró el usuario.")

                    val passwordCorrecta = PasswordUtils.verifyPassword(
                        passwordActual,
                        usuario.passwordHash
                    )

                    if (!passwordCorrecta) {
                        throw Exception("La contraseña actual es incorrecta.")
                    }

                    val mismaPassword = PasswordUtils.verifyPassword(
                        nuevaPassword,
                        usuario.passwordHash
                    )

                    if (mismaPassword) {
                        throw Exception(
                            "La nueva contraseña debe ser diferente a la actual."
                        )
                    }

                    val nuevoHash = PasswordUtils.hashPassword(nuevaPassword)

                    usuarioRepository.actualizarUsuario(
                        usuario.copy(passwordHash = nuevoHash)
                    )
                }

                _uiState.value = ChangePasswordUiState(
                    mensajeExito = "Contraseña actualizada correctamente."
                )

            } catch (e: Exception) {
                _uiState.value = ChangePasswordUiState(
                    mensajeError = e.message
                        ?: "No se pudo actualizar la contraseña."
                )
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        _uiState.value = ChangePasswordUiState(
            mensajeError = mensaje
        )
    }
}