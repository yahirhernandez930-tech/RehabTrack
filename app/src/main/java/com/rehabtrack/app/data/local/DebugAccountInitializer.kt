package com.rehabtrack.app.data.local

import android.content.Context
import com.rehabtrack.app.data.local.entity.UsuarioEntity
import com.rehabtrack.app.data.local.entity.FisioterapeutaEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import com.rehabtrack.app.data.security.PasswordUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DebugAccountInitializer {

    suspend fun crearCuentaFisioterapeuta(context: Context) {

        // No crear cuentas de prueba en versiones de producción.
        val esDepuracion = context.applicationContext.applicationInfo.flags and
                android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0

        if (!esDepuracion) return

        val provider = RehabRepositoryProvider(context.applicationContext)

        val correo = "daniel.torres@test.com"

        // Evitar duplicar la cuenta.
        val usuarioExistente = provider.usuarioRepository
            .obtenerUsuarioPorCorreo(correo)

        if (usuarioExistente != null) return

        // Generar el hash fuera del hilo principal.
        val passwordHash = withContext(Dispatchers.Default) {
            PasswordUtils.hashPassword("Fisio2026*")
        }

        val usuario = UsuarioEntity(
            correo = correo,
            passwordHash = passwordHash,
            rol = "FISIOTERAPEUTA"
        )

        val fisioterapeuta = FisioterapeutaEntity(
            idUsuario = 0,
            nombreCompleto = "Daniel Torres Ruiz",
            especialidad = "Fisioterapia deportiva",
            telefono = null
        )

        // Registrar usuario y perfil en una sola transacción.
        provider.authRepository.registrarFisioterapeuta(
            usuario,
            fisioterapeuta
        )
    }
}
