package com.rehabtrack.app.ui.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rehabtrack.app.ui.components.RehabTextField

@Composable
fun RegisterScreen(
    navController: NavController
) {
    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showErrors by remember { mutableStateOf(false) }

    val passwordsMatch = password == confirmPassword

    val passwordValid =
        password.length in 8..12 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }

    val emailValid = android.util.Patterns.EMAIL_ADDRESS
        .matcher(email)
        .matches()

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
            onValueChange = { nombre = it },
            label = "Nombre",
            modifier = Modifier.fillMaxWidth()
        )

        if (showErrors && nombre.isBlank()) {
            ValidationMessage("El nombre es obligatorio")
        }

        Spacer(modifier = Modifier.height(12.dp))

        RehabTextField(
            value = apellidoPaterno,
            onValueChange = { apellidoPaterno = it },
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

        RehabTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo electrónico",
            modifier = Modifier.fillMaxWidth()
        )

        if (showErrors && !emailValid) {
            ValidationMessage("Introduce un correo electrónico válido")
        }

        Spacer(modifier = Modifier.height(12.dp))

        RehabTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (showErrors && !passwordValid) {
            ValidationMessage(
                "Usa 8–12 caracteres, mayúscula, minúscula, " +
                        "número y símbolo"
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

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                showErrors = true

                val formValid =
                    nombre.isNotBlank() &&
                            apellidoPaterno.isNotBlank() &&
                            emailValid &&
                            passwordValid &&
                            passwordsMatch

                if (formValid) {
                    // El registro con SQLite se implementará después.
                }
            },
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
