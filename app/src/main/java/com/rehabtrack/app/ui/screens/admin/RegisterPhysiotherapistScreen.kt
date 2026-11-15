package com.rehabtrack.app.ui.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rehabtrack.app.ui.components.RehabTextField
import com.rehabtrack.app.ui.viewmodel.AuthState
import com.rehabtrack.app.ui.viewmodel.AuthViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.text.intl.Locale

@Composable
fun RegisterPhysiotherapistScreen(
    onBack: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    // Estados de los campos del formulario.
    var nombreCompleto by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    // Estados de validación local.
    var nombreError by remember { mutableStateOf<String?>(null) }
    var especialidadError by remember { mutableStateOf<String?>(null) }
    var correoError by remember { mutableStateOf<String?>(null) }

    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmarPasswordError by remember { mutableStateOf(false) }
    var telefonoError by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()
    val correoDuplicado = (authState as? AuthState.Error)
        ?.mensaje
        ?.contains("correo electrónico ya está registrado", ignoreCase = true)
        ?: false

    // Regresar al panel cuando el registro haya finalizado correctamente.
    LaunchedEffect(authState) {
        if (authState is AuthState.RegistroExitoso) {
            authViewModel.limpiarEstado()
            onRegistrationSuccess()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Barra superior azul.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(62.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        authViewModel.limpiarEstado()
                        onBack()
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Regresar al panel",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Registrar fisioterapeuta",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Contenido desplazable del formulario.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Completa los datos para crear una nueva cuenta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Nombre completo.
                RehabTextField(
                    value = nombreCompleto,
                    onValueChange = {
                        nombreCompleto = it.take(80)
                        nombreError = null
                        authViewModel.limpiarEstado()
                    },
                    label = "Nombre completo",
                    modifier = Modifier.fillMaxWidth()
                )

                if (nombreError != null) {
                    Text(
                        text = nombreError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Especialidad.
                RehabTextField(
                    value = especialidad,
                    onValueChange = {
                        especialidad = it.take(60)
                        especialidadError = null
                        authViewModel.limpiarEstado()
                    },
                    label = "Especialidad",
                    modifier = Modifier.fillMaxWidth()
                )

                if (especialidadError != null) {
                    Text(
                        text = especialidadError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Teléfono opcional.
                RehabTextField(
                    value = telefono,
                    onValueChange = {
                        telefono = it.filter { caracter -> caracter.isDigit() }.take(10)
                        telefonoError = false
                        authViewModel.limpiarEstado()
                    },
                    label = "Teléfono (opcional)",
                    modifier = Modifier.fillMaxWidth()
                )

                if (telefonoError) {
                    Text(
                        text = "El teléfono debe contener exactamente 10 dígitos.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Correo electrónico.
                RehabTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        correoError = null
                        authViewModel.limpiarEstado()
                    },
                    label = "Correo electrónico",
                    modifier = Modifier.fillMaxWidth()
                )

                if (correoError != null || correoDuplicado) {
                    Text(
                        text = correoError
                            ?: "Este correo electrónico ya está registrado.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contraseña.
                RehabTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                        confirmarPasswordError = false
                        authViewModel.limpiarEstado()
                    },
                    label = "Contraseña",
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirmación de contraseña.
                RehabTextField(
                    value = confirmarPassword,
                    onValueChange = {
                        confirmarPassword = it
                        confirmarPasswordError = false
                    },
                    label = "Confirmar contraseña",
                    isPassword = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (confirmarPasswordError) {
                    Text(
                        text = if (confirmarPassword.isBlank()) {
                            "Debes confirmar tu contraseña."
                        } else {
                            "Las contraseñas no coinciden."
                        },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "La contraseña debe tener entre 8 y 12 caracteres, incluyendo mayúsculas, minúsculas, números y símbolos.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )

                // Mensajes del proceso de registro.
                when (val state = authState) {

                    is AuthState.Error -> {
                        if (!correoDuplicado) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = state.mensaje,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    AuthState.Loading -> {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Registrando fisioterapeuta...",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    else -> Unit
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Botón de registro.
                Button(
                    onClick = {
                        // Validar nombre completo.
                        val nombreValido = nombreCompleto.trim().matches(
                            Regex("""^[\p{L}]+(?:[ '\-][\p{L}]+)*$""")
                        )

                        nombreError = when {
                            nombreCompleto.isBlank() ->
                                "El nombre completo es obligatorio."

                            !nombreValido ->
                                "El nombre solo puede contener letras, espacios, apóstrofes y guiones."

                            else -> null
                        }

                        // Validar especialidad.
                        val especialidadValida = especialidad.trim().matches(
                            Regex("""^[\p{L}]+(?:[ \-][\p{L}]+)*$""")
                        )

                        especialidadError = when {
                            especialidad.isBlank() ->
                                "La especialidad es obligatoria."

                            !especialidadValida ->
                                "La especialidad solo puede contener letras, espacios y guiones."

                            else -> null
                        }

                        // Validar correo electrónico.
                        correoError = when {
                            correo.isBlank() ->
                                "El correo electrónico es obligatorio."

                            !android.util.Patterns.EMAIL_ADDRESS
                                .matcher(correo.trim())
                                .matches() ->
                                "Ingresa un correo electrónico válido."

                            else -> null
                        }

                        // Validar contraseña.
                        val erroresPassword = mutableListOf<String>()

                        if (password.isBlank()) {
                            erroresPassword.add("La contraseña es obligatoria.")
                        } else {
                            if (password.length < 8) {
                                erroresPassword.add("tener al menos 8 caracteres")
                            }

                            if (password.length > 12) {
                                erroresPassword.add("tener como máximo 12 caracteres")
                            }

                            if (!password.any { it.isUpperCase() }) {
                                erroresPassword.add("incluir al menos una mayúscula")
                            }

                            if (!password.any { it.isLowerCase() }) {
                                erroresPassword.add("incluir al menos una minúscula")
                            }

                            if (!password.any { it.isDigit() }) {
                                erroresPassword.add("incluir al menos un número")
                            }

                            if (!password.any { !it.isLetterOrDigit() }) {
                                erroresPassword.add("incluir al menos un símbolo")
                            }
                        }

                        passwordError = when {
                            erroresPassword.isEmpty() -> null

                            password.isBlank() -> erroresPassword.first()

                            else -> "La contraseña debe " +
                                    erroresPassword.joinToString(", ") + "."
                        }

                        // Validar confirmación.
                        confirmarPasswordError =
                            confirmarPassword.isBlank() ||
                                    confirmarPassword != password

                        // Validar teléfono.
                        telefonoError = telefono.isNotBlank() &&
                                !telefono.matches(Regex("^\\d{10}$"))

                        val camposValidos =
                            nombreError == null &&
                                    especialidadError == null &&
                                    correoError == null &&
                                    passwordError == null &&
                                    !telefonoError &&
                                    !confirmarPasswordError

                        if (camposValidos) {
                            authViewModel.registrarFisioterapeuta(
                                nombreCompleto = nombreCompleto.trim(),
                                especialidad = especialidad.trim(),
                                telefono = telefono.trim(),
                                correo = correo.trim(),
                                password = password
                            )
                        }
                    },
                    enabled = authState != AuthState.Loading,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Registrar fisioterapeuta",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}