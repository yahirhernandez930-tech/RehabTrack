package com.rehabtrack.app.ui.screens.physiotherapist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.text.ParsePosition
import java.util.Date
import java.util.Locale
import com.rehabtrack.app.ui.viewmodel.AuthViewModel
import com.rehabtrack.app.ui.viewmodel.AuthState
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton

private val RegisterBlue = Color(0xFF1976D2)

@Composable
fun RegisterPatientScreen(
    idUsuario: Int,
    onBackClick: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember {
        mutableStateOf(
            TextFieldValue(text = "")
        )
    }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var birthDateError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val passwordLongitudValida = password.length in 8..12
    val passwordTieneMayuscula = password.any { it.isUpperCase() }
    val passwordTieneMinuscula = password.any { it.isLowerCase() }
    val passwordTieneNumero = password.any { it.isDigit() }
    val passwordTieneSimbolo = password.any { !it.isLetterOrDigit() }

    val passwordValida =
        passwordLongitudValida &&
                passwordTieneMayuscula &&
                passwordTieneMinuscula &&
                passwordTieneNumero &&
                passwordTieneSimbolo

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.limpiarEstado()
    }

    LaunchedEffect(authState) {
        when (val state = authState) {

            AuthState.RegistroExitoso -> {
                authViewModel.limpiarEstado()
                onRegisterSuccess()
            }

            is AuthState.Error -> {
                if (state.mensaje == "Este correo electrónico ya está registrado") {
                    emailError = state.mensaje
                    authViewModel.limpiarEstado()
                }
            }

            else -> Unit
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RegisterBlue)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(62.dp)
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color.White,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(onClick = onBackClick)
                )

                Spacer(Modifier.width(18.dp))

                Text(
                    text = "Registrar paciente",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Completa los datos para dar de alta a un nuevo paciente.",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Datos personales",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF263238)
            )

            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it.take(80)
                    nameError = null
                },
                label = { Text("Nombre completo *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = nameError != null,
                supportingText = nameError?.let { error ->
                    { Text(error) }
                }
            )

            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = birthDate,
                onValueChange = { input ->
                    val digits = input.text
                        .filter { it.isDigit() }
                        .take(8)

                    val formattedDate = buildString {
                        digits.forEachIndexed { index, char ->
                            if (index == 2 || index == 4) {
                                append('/')
                            }
                            append(char)
                        }
                    }

                    birthDate = TextFieldValue(
                        text = formattedDate,
                        selection = TextRange(formattedDate.length)
                    )
                },
                label = { Text("Fecha de nacimiento *") },
                placeholder = { Text("DD/MM/AAAA") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                isError = birthDateError != null,
                supportingText = birthDateError?.let { error ->
                    { Text(error) }
                }
            )

            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it.filter { char -> char in '0'..'9' }.take(10)
                    phoneError = null
                },
                label = { Text("Número de teléfono (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                isError = phoneError != null,
                supportingText = phoneError?.let { error ->
                    { Text(error) }
                }
            )

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Datos de acceso",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF263238)
            )

            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null

                    if (
                        (authState as? AuthState.Error)?.mensaje ==
                        "Este correo electrónico ya está registrado"
                    ) {
                        authViewModel.limpiarEstado()
                    }
                },
                label = { Text("Correo electrónico *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isError = emailError != null,
                supportingText = emailError?.let { error ->
                    { Text(error) }
                }
            )

            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                    confirmPasswordError = null
                },
                label = { Text("Contraseña *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (passwordVisible) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            },
                            tint = Color(0xFF64748B)
                        )
                    }
                },
                isError = passwordError != null,
                supportingText = {
                    Column {
                        if (password.isBlank() && passwordError != null) {
                            Text("Ingresa una contraseña.")
                        } else if (password.isNotEmpty()) {
                            Text(
                                if (passwordLongitudValida) "✓ Entre 8 y 12 caracteres"
                                else "• Entre 8 y 12 caracteres"
                            )
                            Text(
                                if (passwordTieneMayuscula) "✓ Al menos una mayúscula"
                                else "• Al menos una mayúscula"
                            )
                            Text(
                                if (passwordTieneMinuscula) "✓ Al menos una minúscula"
                                else "• Al menos una minúscula"
                            )
                            Text(
                                if (passwordTieneNumero) "✓ Al menos un número"
                                else "• Al menos un número"
                            )
                            Text(
                                if (passwordTieneSimbolo) "✓ Al menos un símbolo"
                                else "• Al menos un símbolo"
                            )
                        }
                    }
                }
            )

            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it

                    confirmPasswordError = when {
                        it.isBlank() -> null
                        it != password -> "Las contraseñas no coinciden."
                        else -> null
                    }
                },
                label = { Text("Confirmar contraseña *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            confirmPasswordVisible = !confirmPasswordVisible
                        }
                    ) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (confirmPasswordVisible) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            },
                            tint = Color(0xFF64748B)
                        )
                    }
                },
                isError = confirmPasswordError != null,
                supportingText = confirmPasswordError?.let { error ->
                    { Text(error) }
                }
            )

            Spacer(Modifier.height(20.dp))

            // Mostrar el estado del registro
            when (val state = authState) {
                AuthState.Loading -> {
                    Text(
                        text = "Registrando paciente...",
                        color = RegisterBlue
                    )
                }

                is AuthState.Error -> {
                    Text(
                        text = state.mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> Unit
            }

            Button(
                onClick = {
                    // Limpiar errores anteriores
                    nameError = null
                    birthDateError = null
                    phoneError = null
                    emailError = null
                    passwordError = null
                    confirmPasswordError = null

                    // Validar nombre completo
                    val nombreValido = Regex(
                        """^[\p{L}]+(?:[ '\-’][\p{L}]+)*$"""
                    )

                    if (name.isBlank()) {
                        nameError = "Ingresa el nombre completo."
                    } else if (!nombreValido.matches(name.trim())) {
                        nameError = "Ingresa un nombre válido. Usa únicamente letras, espacios y guiones."
                    }

                    // Validar fecha de nacimiento
                    if (birthDate.text.isBlank()) {
                        birthDateError = "Ingresa la fecha de nacimiento."
                    } else if (!isValidBirthDate(birthDate.text.trim())) {
                        birthDateError = "Ingresa una fecha válida (DD/MM/AAAA)."
                    }

                    // Validar teléfono únicamente si se proporcionó
                    if (phone.isNotBlank() && phone.length != 10) {
                        phoneError = "El teléfono debe contener exactamente 10 dígitos."
                    }

                    // Validar correo electrónico
                    if (email.isBlank()) {
                        emailError = "Ingresa el correo electrónico."
                    } else if (!android.util.Patterns.EMAIL_ADDRESS
                            .matcher(email.trim()).matches()
                    ) {
                        emailError = "Ingresa un correo electrónico válido."
                    }

                    // Validar contraseña
                    if (password.isBlank()) {
                        passwordError = "Ingresa una contraseña."
                    } else if (!passwordValida) {
                        passwordError = "La contraseña no cumple todos los requisitos."
                    }

                    // Validar confirmación
                    if (confirmPassword.isBlank()) {
                        confirmPasswordError = "Confirma la contraseña."
                    } else if (password != confirmPassword) {
                        confirmPasswordError = "Las contraseñas no coinciden."
                    }

                    // Continuar únicamente si no existen errores
                    if (
                        nameError == null &&
                        birthDateError == null &&
                        phoneError == null &&
                        emailError == null &&
                        passwordError == null &&
                        confirmPasswordError == null
                    ) {
                        authViewModel.registrarPacienteConAsignacion(
                            nombreCompleto = name,
                            fechaNacimiento = birthDate.text,
                            telefono = phone,
                            correo = email,
                            password = password,
                            idUsuarioFisioterapeuta = idUsuario
                        )
                    }
                },
                enabled = authState != AuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RegisterBlue
                )
            ) {
                Text("Registrar paciente")
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "* Campos obligatorios",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

private fun isValidBirthDate(value: String): Boolean {
    val formatter = SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.US
    )

    formatter.isLenient = false

    val position = ParsePosition(0)
    val date = formatter.parse(value, position)

    return date != null &&
            position.index == value.length &&
            !date.after(Date())
}