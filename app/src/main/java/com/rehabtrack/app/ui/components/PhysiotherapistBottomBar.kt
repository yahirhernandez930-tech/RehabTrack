package com.rehabtrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val RehabBlue = Color(0xFF1976D2)
private val RehabSecondary = Color(0xFF64748B)

@Composable
fun PhysiotherapistBottomBar(
    onHomeClick: () -> Unit,
    onPatientsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PhysiotherapistBottomBarItem(
            label = "Inicio",
            icon = Icons.Default.Home,
            selected = true,
            onClick = onHomeClick
        )

        PhysiotherapistBottomBarItem(
            label = "Pacientes",
            icon = Icons.Default.People,
            selected = false,
            onClick = onPatientsClick
        )

        PhysiotherapistBottomBarItem(
            label = "Perfil",
            icon = Icons.Default.Person,
            selected = false,
            onClick = onProfileClick
        )
    }
}

@Composable
private fun PhysiotherapistBottomBarItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) RehabBlue else RehabSecondary,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = label,
            color = if (selected) RehabBlue else RehabSecondary,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
