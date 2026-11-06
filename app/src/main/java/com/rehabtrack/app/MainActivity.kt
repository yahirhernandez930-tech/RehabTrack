package com.rehabtrack.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.rehabtrack.app.data.local.DebugAccountInitializer
import com.rehabtrack.app.navigation.AppNavigation
import com.rehabtrack.app.ui.theme.RehabTrackTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false

        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightNavigationBars = true

        lifecycleScope.launch {

            try {
                withContext(Dispatchers.IO) {
                    DebugAccountInitializer
                        .crearCuentaFisioterapeuta(applicationContext)
                }
            } catch (e: Exception) {
                Log.e(
                    "RehabTrack",
                    "Error al inicializar la cuenta de prueba",
                    e
                )
            }

            setContent {
                RehabTrackTheme {
                    Surface(
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}