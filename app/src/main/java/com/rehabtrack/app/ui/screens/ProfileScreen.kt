package com.rehabtrack.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rehabtrack.app.navigation.Routes
import com.rehabtrack.app.ui.components.PatientBottomBar

@Composable
fun ProfileScreen(navController: NavController) {
    val colors = MaterialTheme.colorScheme
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            PatientBottomBar(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(colors.background)
        ) {
            // Encabezado azul
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.primary)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RehabTrack",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Perfil",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileData(
                    label = "Nombre",
                    value = "Mario"
                )

                ProfileData(
                    label = "Apellido Paterno",
                    value = "López"
                )

                ProfileData(
                    label = "Apellido Materno",
                    value = "García"
                )

                ProfileData(
                    label = "Correo electrónico",
                    value = "mario@gmail.com"
                )

                Spacer(modifier = Modifier.height(28.dp))

                ProfileActionButton(
                    text = "Cambiar correo",
                    onClick = {
                        navController.navigate(Routes.ChangeEmail.route)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                ProfileActionButton(
                    text = "Cambiar contraseña",
                    onClick = {
                        navController.navigate(Routes.ChangePassword.route)
                    }
                )

                Spacer(modifier = Modifier.height(48.dp))

                ProfileActionButton(
                    text = "Cerrar sesión",
                    backgroundColor = Color(0xFFD32F2F),
                    onClick = {
                        // Pendiente de implementar
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileData(
    label: String,
    value: String
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = colors.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onBackground,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
private fun ProfileActionButton(
    text: String,
    backgroundColor: Color = Color(0xFF1976D2),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(0.75f),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = Color.White
            )
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center
            )
        }
    }
}
