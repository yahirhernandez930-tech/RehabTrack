package com.rehabtrack.app.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.rehabtrack.app.navigation.Routes
import com.rehabtrack.app.ui.components.RehabTextField
import com.rehabtrack.app.ui.viewmodel.AuthState
import com.rehabtrack.app.ui.viewmodel.AuthViewModel
import android.util.Patterns

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()
    val emailValid = Patterns.EMAIL_ADDRESS
        .matcher(email.trim())
        .matches()

    // Navegación según el rol autenticado.
    LaunchedEffect(authState) {
        if (authState is AuthState.LoginExitoso) {

            val resultado = authState as AuthState.LoginExitoso

            val destino = when (resultado.rol) {
                "PACIENTE" ->
                    "${Routes.Home.route}/${resultado.idUsuario}"

                "FISIOTERAPEUTA" ->
                    "${Routes.PhysiotherapistHome.route}/${resultado.idUsuario}"

                "ADMINISTRADOR" ->
                    "${Routes.AdminHome.route}/${resultado.idUsuario}"

                else -> null
            }

            destino?.let {
                navController.navigate(it) {
                    popUpTo(Routes.Login.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "RehabTrack",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Accede a tu cuenta",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        RehabTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
                authViewModel.limpiarEstado()
            },
            label = "Correo electrónico",
            modifier = Modifier.fillMaxWidth()
        )

        if (emailError) {
            Text(
                text = when {
                    email.isBlank() ->
                        "El correo electrónico es obligatorio."

                    !emailValid ->
                        "Introduce un correo electrónico válido."

                    else -> ""
                },
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        RehabTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = false
                authViewModel.limpiarEstado()
            },
            label = "Contraseña",
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (passwordError) {
            Text(
                text = "La contraseña es obligatoria",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }

        // Mensajes del proceso de autenticación.
        when (val state = authState) {
            is AuthState.Error -> {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = state.mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            AuthState.Loading -> {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Verificando credenciales...",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            else -> Unit
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                emailError = email.isBlank() || !emailValid
                passwordError = password.isBlank()

                if (!emailError && !passwordError) {
                    authViewModel.iniciarSesion(
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
                text = "Iniciar sesión",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes una cuenta?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Regístrate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    authViewModel.limpiarEstado()
                    navController.navigate(Routes.Register.route)
                }
            )
        }
    }
}
