package com.rehabtrack.app.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.rehabtrack.app.ui.components.RehabTextField
import com.rehabtrack.app.ui.viewmodel.AuthState
import com.rehabtrack.app.ui.viewmodel.AuthViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showErrors by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    val passwordsMatch = password == confirmPassword

    val passwordValid =
        password.length in 8..12 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }

    val emailValid = Patterns.EMAIL_ADDRESS
        .matcher(email.trim())
        .matches()

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    // Navegar al inicio de sesión cuando el registro termine.
    LaunchedEffect(authState) {
        if (authState is AuthState.RegistroExitoso) {
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(52.dp))

        Text(
            text = "RehabTrack",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Crear nueva cuenta",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        RehabTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                authViewModel.limpiarEstado()
            },
            label = "Nombre",
            modifier = Modifier.fillMaxWidth()
        )

        if (showErrors && nombre.isBlank()) {
            ValidationMessage("El nombre es obligatorio")
        }

        Spacer(modifier = Modifier.height(12.dp))

        RehabTextField(
            value = apellidoPaterno,
            onValueChange = {
                apellidoPaterno = it
                authViewModel.limpiarEstado()
            },
            label = "Apellido paterno",
            modifier = Modifier.fillMaxWidth()
        )

        if (showErrors && apellidoPaterno.isBlank()) {
            ValidationMessage("El apellido paterno es obligatorio")
        }

        Spacer(modifier = Modifier.height(12.dp))

        RehabTextField(
            value = apellidoMaterno,
            onValueChange = { apellidoMaterno = it },
            label = "Apellido materno (opcional)",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Fecha de nacimiento
        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = if (fechaNacimiento.isBlank()) {
                    "Seleccionar fecha de nacimiento"
                } else {
                    "Nacimiento: ${
                        LocalDate.parse(fechaNacimiento).format(dateFormatter)
                    }"
                }
            )
        }

        if (showErrors && fechaNacimiento.isBlank()) {
            ValidationMessage("La fecha de nacimiento es obligatoria")
        }

        Spacer(modifier = Modifier.height(12.dp))

        RehabTextField(
            value = email,
            onValueChange = {
                email = it
                authViewModel.limpiarEstado()
            },
            label = "Correo electrónico",
            modifier = Modifier.fillMaxWidth()
        )

        if (showErrors && !emailValid) {
            ValidationMessage("Introduce un correo electrónico válido")
        }

        Spacer(modifier = Modifier.height(12.dp))

        RehabTextField(
            value = password,
            onValueChange = {
                password = it
                authViewModel.limpiarEstado()
            },
            label = "Contraseña",
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (showErrors && !passwordValid) {
            ValidationMessage(
                "Usa 8–12 caracteres, mayúscula, minúscula, número y símbolo"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        RehabTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirmar contraseña",
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (showErrors && !passwordsMatch) {
            ValidationMessage("Las contraseñas no coinciden")
        }

        // Estado de autenticación
        when (val state = authState) {
            is AuthState.Error -> {
                Spacer(modifier = Modifier.height(12.dp))
                ValidationMessage(state.mensaje)
            }

            AuthState.Loading -> {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Registrando cuenta...",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            else -> Unit
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                showErrors = true

                val formValid =
                    nombre.isNotBlank() &&
                            apellidoPaterno.isNotBlank() &&
                            fechaNacimiento.isNotBlank() &&
                            emailValid &&
                            passwordValid &&
                            passwordsMatch

                if (formValid) {
                    authViewModel.registrarPaciente(
                        nombre = nombre,
                        apellidoPaterno = apellidoPaterno,
                        apellidoMaterno = apellidoMaterno,
                        fechaNacimiento = fechaNacimiento,
                        correo = email,
                        password = password
                    )
                }
            },
            enabled = authState != AuthState.Loading,
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Crear cuenta",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¿Ya tienes una cuenta?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )
        }
    }

    // Selector de fecha
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            yearRange = 1900..LocalDate.now().year
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            fechaNacimiento = Instant
                                .ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                                .toString()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun ValidationMessage(
    message: String
) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    )
}