package com.rehabtrack.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rehabtrack.app.navigation.Routes

@Composable
fun PatientBottomBar(
    navController: NavController,
    currentRoute: String?,
    idUsuario: Int
) {
    val colors = MaterialTheme.colorScheme

    val homeRoute = "${Routes.Home.route}/$idUsuario"
    val progressRoute = "${Routes.Progress.route}/$idUsuario"
    val historyRoute = "${Routes.History.route}/$idUsuario"
    val profileRoute = "${Routes.Profile.route}/$idUsuario"

    NavigationBar(
        containerColor = colors.background,
        contentColor = colors.primary,
        tonalElevation = 0.dp
    ) {

        NavigationBarItem(
            selected = currentRoute == "${Routes.Home.route}/{idUsuario}",
            onClick = {
                navigateTo(navController, homeRoute, idUsuario)
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Inicio",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Inicio") },
            colors = navigationItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == "${Routes.Progress.route}/{idUsuario}",
            onClick = {
                navigateTo(navController, progressRoute, idUsuario)
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.PieChart,
                    contentDescription = "Progreso",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Progreso") },
            colors = navigationItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == "${Routes.History.route}/{idUsuario}",
            onClick = {
                navigateTo(navController, historyRoute, idUsuario)
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = "Historial",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Historial") },
            colors = navigationItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == "${Routes.Profile.route}/{idUsuario}",
            onClick = {
                navigateTo(navController, profileRoute, idUsuario)
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Perfil") },
            colors = navigationItemColors()
        )
    }
}

@Composable
private fun navigationItemColors() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.background,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

private fun navigateTo(
    navController: NavController,
    route: String,
    idUsuario: Int
) {
    navController.navigate(route) {
        popUpTo("${Routes.Home.route}/{idUsuario}") {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}