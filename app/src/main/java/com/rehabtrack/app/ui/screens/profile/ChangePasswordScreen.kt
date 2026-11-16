package com.rehabtrack.app.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rehabtrack.app.ui.viewmodel.ChangePasswordUiState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    idUsuario: Int,
    uiState: ChangePasswordUiState,
    onChangePassword: (String, String, String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var mostrarPasswordActual by remember { mutableStateOf(false) }
    var mostrarNuevaPassword by remember { mutableStateOf(false) }
    var mostrarConfirmarPassword by remember { mutableStateOf(false) }

    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val passwordLongitudValida = newPassword.length in 8..12
    val passwordTieneMayuscula = newPassword.any { it.isUpperCase() }
    val passwordTieneMinuscula = newPassword.any { it.isLowerCase() }
    val passwordTieneNumero = newPassword.any { it.isDigit() }
    val passwordTieneSimbolo = newPassword.any {
        !it.isLetterOrDigit() && !it.isWhitespace()
    }

    val passwordValida =
        passwordLongitudValida &&
                passwordTieneMayuscula &&
                passwordTieneMinuscula &&
                passwordTieneNumero &&
                passwordTieneSimbolo

    Scaffold(
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
        ) {
            // Encabezado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primary)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(54.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = colors.onPrimary
                    )
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cambiar contraseña",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.width(48.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp)
            ) {
                Spacer(modifier = Modifier.height(56.dp))

                // Contraseña actual
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        currentPasswordError = null
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("Contraseña actual") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    isError = currentPasswordError != null,
                    supportingText = currentPasswordError?.let { error ->
                        { Text(error) }
                    },
                    visualTransformation = if (mostrarPasswordActual) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                mostrarPasswordActual = !mostrarPasswordActual
                            }
                        ) {
                            Icon(
                                imageVector = if (mostrarPasswordActual) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (mostrarPasswordActual) {
                                    "Ocultar contraseña"
                                } else {
                                    "Mostrar contraseña"
                                }
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Nueva contraseña
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        newPasswordError = null

                        confirmPasswordError = when {
                            confirmPassword.isBlank() -> null
                            confirmPassword != it -> "Las contraseñas no coinciden."
                            else -> null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("Nueva contraseña") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    isError = newPasswordError != null,
                    supportingText = {
                        Column {
                            if (newPassword.isBlank() && newPasswordError != null) {
                                Text("Ingresa una nueva contraseña.")
                            } else if (newPassword.isNotEmpty()) {
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
                    },
                    visualTransformation = if (mostrarNuevaPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                mostrarNuevaPassword = !mostrarNuevaPassword
                            }
                        ) {
                            Icon(
                                imageVector = if (mostrarNuevaPassword) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (mostrarNuevaPassword) {
                                    "Ocultar contraseña"
                                } else {
                                    "Mostrar contraseña"
                                }
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirmar contraseña
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it

                        confirmPasswordError = when {
                            it.isBlank() -> null
                            it != newPassword -> "Las contraseñas no coinciden."
                            else -> null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    placeholder = { Text("Confirmar contraseña") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    isError = confirmPasswordError != null,
                    supportingText = confirmPasswordError?.let { error ->
                        { Text(error) }
                    },
                    visualTransformation = if (mostrarConfirmarPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                mostrarConfirmarPassword = !mostrarConfirmarPassword
                            }
                        ) {
                            Icon(
                                imageVector = if (mostrarConfirmarPassword) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (mostrarConfirmarPassword) {
                                    "Ocultar contraseña"
                                } else {
                                    "Mostrar contraseña"
                                }
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    )
                )

                uiState.mensajeError?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                uiState.mensajeExito?.let { mensaje ->
                    Text(
                        text = mensaje,
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(44.dp))

                // Guardar cambios
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            currentPasswordError = null
                            newPasswordError = null
                            confirmPasswordError = null

                            if (currentPassword.isBlank()) {
                                currentPasswordError = "Ingresa tu contraseña actual."
                            }

                            if (newPassword.isBlank()) {
                                newPasswordError = "Ingresa una nueva contraseña."
                            } else if (!passwordValida) {
                                newPasswordError =
                                    "La contraseña no cumple todos los requisitos."
                            }

                            if (confirmPassword.isBlank()) {
                                confirmPasswordError = "Confirma tu nueva contraseña."
                            } else if (newPassword != confirmPassword) {
                                confirmPasswordError = "Las contraseñas no coinciden."
                            }

                            if (
                                currentPasswordError == null &&
                                newPasswordError == null &&
                                confirmPasswordError == null
                            ) {
                                onChangePassword(
                                    currentPassword,
                                    newPassword,
                                    confirmPassword
                                )
                            }
                        },
                        enabled = !uiState.guardando,
                        modifier = Modifier
                            .fillMaxWidth(0.68f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary
                        )
                    ) {
                        if (uiState.guardando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Guardar cambios")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Cancelar
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth(0.68f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.primary
                        ),
                        border = BorderStroke(1.dp, colors.primary)
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}
