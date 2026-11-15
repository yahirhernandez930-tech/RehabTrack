package com.rehabtrack.app.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun AdminSetupScreen(
    onSetupSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }

    var confirmarPasswordError by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.RegistroExitoso) {
            authViewModel.limpiarEstado()
            onSetupSuccess()
        }
    }

    Scaffold(
        contentWindowInsets =
            androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "RehabTrack",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Configuración inicial",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Crea la cuenta del administrador principal para comenzar a utilizar RehabTrack.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            RehabTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    authViewModel.limpiarEstado()
                },
                label = "Correo electrónico",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            RehabTextField(
                value = password,
                onValueChange = {
                    password = it
                    confirmarPasswordError = false
                    authViewModel.limpiarEstado()
                },
                label = "Contraseña",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                    text = "Las contraseñas no coinciden.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "La contraseña debe tener entre 8 y 12 caracteres, incluyendo mayúsculas, minúsculas, números y símbolos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            when (val state = authState) {
                is AuthState.Error -> {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = state.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                AuthState.Loading -> {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Creando cuenta administrativa...",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> Unit
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    confirmarPasswordError =
                        password != confirmarPassword ||
                                confirmarPassword.isBlank()

                    if (
                        correo.isNotBlank() &&
                        password.isNotBlank() &&
                        !confirmarPasswordError
                    ) {
                        authViewModel.inicializarAdministrador(
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
                    text = "Crear cuenta administrativa",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}