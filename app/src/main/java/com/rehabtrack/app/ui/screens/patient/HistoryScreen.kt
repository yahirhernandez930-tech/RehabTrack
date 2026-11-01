package com.rehabtrack.app.ui.screens.patient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rehabtrack.app.navigation.Routes
import com.rehabtrack.app.ui.components.PatientBottomBar
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding

private data class SessionHistoryItem(
    val date: String,
    val title: String,
    val exercises: Int,
    val duration: Int,
    val painLevel: Int
)

private val sampleHistory = listOf(
    SessionHistoryItem(
        date = "30 septiembre 2026",
        title = "Fortalecimiento de rodilla",
        exercises = 3,
        duration = 20,
        painLevel = 3
    ),
    SessionHistoryItem(
        date = "28 septiembre 2026",
        title = "Movilidad de rodilla",
        exercises = 4,
        duration = 25,
        painLevel = 4
    ),
    SessionHistoryItem(
        date = "26 septiembre 2026",
        title = "Fortalecimiento de rodilla",
        exercises = 3,
        duration = 20,
        painLevel = 5
    )
)

@Composable
fun HistoryScreen(
    navController: NavController,
    patientName: String? = null
) {
    val colors = MaterialTheme.colorScheme
    val isPhysioterapist = patientName != null
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if(!isPhysioterapist) {
                PatientBottomBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(colors.background)
        ) {
            // Encabezado
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
                    text = if (isPhysioterapist) {
                        "Historial de sesiones"
                    } else {
                        "Historial"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                if (isPhysioterapist) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = patientName.orEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onBackground,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Sesiones recientes",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onBackground,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(40.dp))

                sampleHistory.forEachIndexed { index, session ->
                    HistorySessionItem(
                        session = session,
                        onViewMore = {
                            if (patientName == null) {
                                navController.navigate(Routes.SessionDetail.route)
                            } else {
                                navController.navigate(
                                    "${Routes.SessionDetail.route}/${
                                        java.net.URLEncoder.encode(patientName, "UTF-8")
                                    }"
                                )
                            }
                        }
                    )

                    if (index < sampleHistory.lastIndex) {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HistorySessionItem(
    session: SessionHistoryItem,
    onViewMore: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = session.date,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = session.title,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onBackground,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${session.exercises} ejercicios · ${session.duration} min",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dolor: ${session.painLevel}/10",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onBackground
            )

            Text(
                text = "Ver más",
                style = MaterialTheme.typography.labelLarge,
                color = colors.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    onViewMore()
                }
            )
        }
    }
}
