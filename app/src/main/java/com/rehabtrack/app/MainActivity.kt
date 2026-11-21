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
import androidx.compose.material3.Text
import com.rehabtrack.app.util.BackupManager

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false

        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightNavigationBars = true

        lifecycleScope.launch {

            var errorRestauracion: String? = null

            // 1. Resolver cualquier restauración interrumpida.
            try {
                withContext(Dispatchers.IO) {
                    BackupManager.verificarRestauracionPendiente(
                        applicationContext
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    "RehabTrack",
                    "No se pudo verificar o recuperar la restauración",
                    e
                )

                errorRestauracion =
                    "No se pudo completar la verificación de los datos. " +
                            "La aplicación no continuará para proteger tu información. " +
                            "Conserva los archivos de recuperación."
            }

            // 2. Inicializar la cuenta de prueba únicamente si no hubo errores.
            if (errorRestauracion == null) {
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
            }

            // 3. Mostrar la aplicación o bloquear el acceso si falló la recuperación.
            setContent {
                RehabTrackTheme {
                    Surface(
                        color = MaterialTheme.colorScheme.background
                    ) {
                        if (errorRestauracion != null) {
                            Text(
                                text = errorRestauracion!!
                            )
                        } else {
                            AppNavigation()
                        }
                    }
                }
            }
        }
    }
}