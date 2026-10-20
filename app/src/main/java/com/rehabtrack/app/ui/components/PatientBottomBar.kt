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
    currentRoute: String?
) {
    val colors = MaterialTheme.colorScheme

    NavigationBar(
        containerColor = colors.background,
        contentColor = colors.primary,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == Routes.Home.route,
            onClick = {
                navigateTo(navController, Routes.Home.route)
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
            selected = currentRoute == Routes.Progress.route,
            onClick = {
                navigateTo(navController, Routes.Progress.route)
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
            selected = currentRoute == Routes.History.route,
            onClick = {
                navigateTo(navController, Routes.History.route)
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
            selected = currentRoute == Routes.Profile.route,
            onClick = {
                navigateTo(navController, Routes.Profile.route)
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
    route: String
) {
    if (navController.currentDestination?.route != route) {
        navController.navigate(route) {
            popUpTo(Routes.Home.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}
