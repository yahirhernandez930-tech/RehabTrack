package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.PacienteEntity
import com.rehabtrack.app.data.local.entity.UsuarioEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import com.rehabtrack.app.data.security.PasswordUtils
import com.rehabtrack.app.data.local.entity.AsignacionEntity
import com.rehabtrack.app.data.local.entity.FisioterapeutaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

sealed interface AuthState {
    data object Idle : AuthState
    data object Loading : AuthState
    data object RegistroExitoso : AuthState
    data class LoginExitoso(
        val idUsuario: Int,
        val rol: String
    ) : AuthState
    data class Error(val mensaje: String) : AuthState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)
    private val authRepository = provider.authRepository
    private val usuarioRepository = provider.usuarioRepository
    private val fisioterapeutaRepository = provider.fisioterapeutaRepository

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun limpiarEstado() {
        _authState.value = AuthState.Idle
    }

    fun registrarPaciente(
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        fechaNacimiento: String,
        correo: String,
        password: String
    ) {
        if (_authState.value == AuthState.Loading) return

        val correoNormalizado = correo.trim().lowercase()
        val nombreCompleto = listOf(
            nombre.trim(),
            apellidoPaterno.trim(),
            apellidoMaterno.trim()
        ).filter { it.isNotBlank() }.joinToString(" ")

        if (nombre.isBlank() || apellidoPaterno.isBlank()) {
            _authState.value = AuthState.Error("Completa los campos obligatorios")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correoNormalizado).matches()) {
            _authState.value = AuthState.Error("El correo electrónico no es válido")
            return
        }

        val fecha = try {
            LocalDate.parse(fechaNacimiento)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(
                "Introduce la fecha en formato AAAA-MM-DD"
            )
            return
        }

        if (fecha.isAfter(LocalDate.now())) {
            _authState.value = AuthState.Error(
                "La fecha de nacimiento no puede ser futura"
            )
            return
        }

        val passwordValida =
            password.length in 8..12 &&
                    password.any { it.isUpperCase() } &&
                    password.any { it.isLowerCase() } &&
                    password.any { it.isDigit() } &&
                    password.any { !it.isLetterOrDigit() }

        if (!passwordValida) {
            _authState.value = AuthState.Error(
                "La contraseña no cumple los requisitos"
            )
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val correoExiste = withContext(Dispatchers.IO) {
                    usuarioRepository.obtenerUsuarioPorCorreo(correoNormalizado)
                }

                if (correoExiste != null) {
                    _authState.value = AuthState.Error(
                        "Este correo electrónico ya está registrado"
                    )
                    return@launch
                }

                val passwordHash = withContext(Dispatchers.Default) {
                    PasswordUtils.hashPassword(password)
                }

                val usuario = UsuarioEntity(
                    correo = correoNormalizado,
                    passwordHash = passwordHash,
                    rol = "PACIENTE"
                )

                val paciente = PacienteEntity(
                    idUsuario = 0,
                    nombre = nombreCompleto,
                    fechaNacimiento = fecha.toString(),
                    telefono = null
                )

                withContext(Dispatchers.IO) {
                    authRepository.registrarPaciente(usuario, paciente)
                }

                _authState.value = AuthState.RegistroExitoso

            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    if (e is SQLiteConstraintException ||
                        e.cause is SQLiteConstraintException
                    ) {
                        "Este correo electrónico ya está registrado"
                    } else {
                        "No se pudo completar el registro"
                    }
                )
            }
        }
    }

    fun registrarPacienteConAsignacion(
        nombreCompleto: String,
        fechaNacimiento: String,
        telefono: String,
        correo: String,
        password: String,
        idUsuarioFisioterapeuta: Int
    ) {
        if (_authState.value == AuthState.Loading) return

        val nombre = nombreCompleto.trim()
        val correoNormalizado = correo.trim().lowercase()
        val telefonoNormalizado = telefono.trim()

        if (nombre.isBlank()) {
            _authState.value = AuthState.Error("Ingresa el nombre completo")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correoNormalizado).matches()) {
            _authState.value = AuthState.Error("El correo electrónico no es válido")
            return
        }

        val fecha = try {
            val formatter = DateTimeFormatter
                .ofPattern("dd/MM/uuuu")
                .withResolverStyle(ResolverStyle.STRICT)

            LocalDate.parse(fechaNacimiento, formatter)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(
                "Introduce una fecha válida en formato DD/MM/AAAA"
            )
            return
        }

        if (fecha.isAfter(LocalDate.now())) {
            _authState.value = AuthState.Error(
                "La fecha de nacimiento no puede ser futura"
            )
            return
        }

        if (telefonoNormalizado.isNotBlank() &&
            !telefonoNormalizado.matches(Regex("""\d{10}"""))
        ) {
            _authState.value = AuthState.Error(
                "El teléfono debe contener 10 dígitos"
            )
            return
        }

        val passwordValida =
            password.length in 8..12 &&
                    password.any { it.isUpperCase() } &&
                    password.any { it.isLowerCase() } &&
                    password.any { it.isDigit() } &&
                    password.any { !it.isLetterOrDigit() }

        if (!passwordValida) {
            _authState.value = AuthState.Error(
                "Usa 8–12 caracteres, mayúscula, minúscula, número y símbolo"
            )
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val correoExiste = withContext(Dispatchers.IO) {
                    usuarioRepository.obtenerUsuarioPorCorreo(correoNormalizado)
                }

                if (correoExiste != null) {
                    _authState.value = AuthState.Error(
                        "Este correo electrónico ya está registrado"
                    )
                    return@launch
                }

                // Obtener el perfil del fisioterapeuta que inició sesión.
                val fisioterapeuta = withContext(Dispatchers.IO) {
                    fisioterapeutaRepository
                        .obtenerFisioterapeutaPorUsuario(
                            idUsuarioFisioterapeuta
                        )
                }

                if (fisioterapeuta == null) {
                    _authState.value = AuthState.Error(
                        "No se encontró el perfil del fisioterapeuta"
                    )
                    return@launch
                }

                val passwordHash = withContext(Dispatchers.Default) {
                    PasswordUtils.hashPassword(password)
                }

                val usuario = UsuarioEntity(
                    correo = correoNormalizado,
                    passwordHash = passwordHash,
                    rol = "PACIENTE"
                )

                val paciente = PacienteEntity(
                    idUsuario = 0,
                    nombre = nombre,
                    fechaNacimiento = fecha.toString(),
                    telefono = telefonoNormalizado.ifBlank { null }
                )

                val asignacion = AsignacionEntity(
                    idPaciente = 0,
                    idFisioterapeuta = fisioterapeuta.idFisioterapeuta,
                    fechaInicio = LocalDate.now().toString(),
                    fechaFin = null,
                    activa = true
                )

                // Registrar usuario, paciente y asignación en una transacción.
                withContext(Dispatchers.IO) {
                    authRepository.registrarPacienteConAsignacion(
                        usuario,
                        paciente,
                        asignacion
                    )
                }

                _authState.value = AuthState.RegistroExitoso

            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    if (e is SQLiteConstraintException ||
                        e.cause is SQLiteConstraintException
                    ) {
                        "Este correo electrónico ya está registrado"
                    } else {
                        "No se pudo completar el registro del paciente"
                    }
                )
            }
        }
    }

    fun iniciarSesion(correo: String, password: String) {
        if (_authState.value == AuthState.Loading) return

        if (correo.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error(
                "Introduce tu correo y contraseña"
            )
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val usuario = withContext(Dispatchers.IO) {
                    usuarioRepository.obtenerUsuarioPorCorreo(
                        correo.trim().lowercase()
                    )
                }

                if (usuario == null) {
                    _authState.value = AuthState.Error(
                        "Correo o contraseña incorrectos"
                    )
                    return@launch
                }

                val passwordCorrecta = withContext(Dispatchers.Default) {
                    PasswordUtils.verifyPassword(
                        password,
                        usuario.passwordHash
                    )
                }

                if (!passwordCorrecta) {
                    _authState.value = AuthState.Error(
                        "Correo o contraseña incorrectos"
                    )
                    return@launch
                }

                if (usuario.rol !in listOf("PACIENTE", "FISIOTERAPEUTA")) {
                    _authState.value = AuthState.Error(
                        "El usuario tiene un rol no reconocido"
                    )
                    return@launch
                }

                _authState.value = AuthState.LoginExitoso(
                    idUsuario = usuario.idUsuario,
                    rol = usuario.rol
                )

            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    "No se pudo iniciar sesión"
                )
            }
        }
    }
}
