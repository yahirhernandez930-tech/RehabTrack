package com.rehabtrack.app.data.repository

import android.content.Context
import com.rehabtrack.app.data.local.RehabDatabase

class RehabRepositoryProvider(context: Context) {

    private val database = RehabDatabase.getDatabase(context)

    val usuarioRepository =
        UsuarioRepository(database.usuarioDao())

    val pacienteRepository =
        PacienteRepository(database.pacienteDao())

    val fisioterapeutaRepository =
        FisioterapeutaRepository(database.fisioterapeutaDao())

    val asignacionRepository =
        AsignacionRepository(database.asignacionDao())

    val authRepository =
        AuthRepository(database.authDao())

    val programaRepository =
        ProgramaRepository(database.programaDao())

    val ejercicioRepository =
        EjercicioRepository(database.ejercicioDao())

    val programaEjercicioRepository =
        ProgramaEjercicioRepository(database.programaEjercicioDao())

    val sesionRepository =
        SesionRepository(database.sesionDao())

    val registroSerieRepository =
        RegistroSerieRepository(database.registroSerieDao())

    suspend fun <T> ejecutarTransaccion(
        operacion: suspend () -> T
    ): T {
        return database.ejecutarTransaccion(operacion)
    }
}