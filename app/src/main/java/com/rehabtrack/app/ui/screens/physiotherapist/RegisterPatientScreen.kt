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
    var errorMessage by remember { mutableStateOf("") }

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
                singleLine = true
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
                )
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
                )
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
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(20.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    errorMessage = when {
                        name.isBlank() ->
                            "Ingresa el nombre completo."

                        birthDate.isBlank() ->
                            "Ingresa la fecha de nacimiento."

                        !birthDate.matches(
                            Regex("""\d{2}/\d{2}/\d{4}""")
                        ) ->
                            "La fecha debe tener el formato DD/MM/AAAA."

                        email.isBlank() ->
                            "Ingresa el correo electrónico."

                        !android.util.Patterns.EMAIL_ADDRESS
                            .matcher(email).matches() ->
                            "Ingresa un correo electrónico válido."

                        password.isBlank() ->
                            "Ingresa una contraseña."

                        password.length < 8 ->
                            "La contraseña debe tener al menos 8 caracteres."

                        confirmPassword.isBlank() ->
                            "Confirma la contraseña."

                        password != confirmPassword ->
                            "Las contraseñas no coinciden."

                        else -> ""
                    }

                    if (errorMessage.isEmpty()) {
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
