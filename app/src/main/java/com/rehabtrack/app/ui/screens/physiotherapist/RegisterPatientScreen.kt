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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat
import java.text.ParsePosition
import java.util.Date
import java.util.Locale

private val RegisterBlue = Color(0xFF1976D2)

@Composable
fun RegisterPatientScreen(
    onBackClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Completa los datos para dar de alta a un nuevo paciente.",
                color = Color(0xFF64748B),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(25.dp))

            Text(
                text = "Datos personales",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF263238)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre completo *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = nameError != null,
                supportingText = {
                    nameError?.let { Text(it) }
                }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Fecha de nacimiento *") },
                placeholder = { Text("DD/MM/AAAA") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                isError = birthDateError != null,
                supportingText = {
                    birthDateError?.let { Text(it) }
                }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Número de teléfono (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                isError = phoneError != null,
                supportingText = {
                    phoneError?.let { Text(it) }
                }
            )

            Spacer(Modifier.height(25.dp))

            Text(
                text = "Datos de acceso",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF263238)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isError = emailError != null,
                supportingText = {
                    emailError?.let { Text(it) }
                }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = {
                    passwordError?.let { Text(it) }
                }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = confirmPasswordError != null,
                supportingText = {
                    confirmPasswordError?.let { Text(it) }
                }
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    // Limpiar errores anteriores
                    nameError = null
                    birthDateError = null
                    phoneError = null
                    emailError = null
                    passwordError = null
                    confirmPasswordError = null

                    // Validar nombre
                    if (name.isBlank()) {
                        nameError = "Ingresa el nombre completo."
                    }

                    // Validar fecha de nacimiento
                    if (birthDate.isBlank()) {
                        birthDateError = "Ingresa la fecha de nacimiento."
                    } else if (!isValidBirthDate(birthDate.trim())) {
                        birthDateError = "Ingresa una fecha válida (DD/MM/AAAA)."
                    }

                    // Validar teléfono únicamente si se proporcionó
                    val digits = phone.filter { it.isDigit() }

                    if (digits.length != 10 ||
                        !phone.matches(Regex("""^[\d\s()+-]+$"""))
                    ) {
                        phoneError = "Ingresa un teléfono válido de 10 dígitos."
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
                    } else if (password.length < 8) {
                        passwordError = "Debe tener al menos 8 caracteres."
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
                        onRegisterClick()
                    }
                },
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