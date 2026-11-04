package com.rehabtrack.app.data.repository

import com.rehabtrack.app.data.local.dao.AuthDao
import com.rehabtrack.app.data.local.entity.UsuarioEntity
import com.rehabtrack.app.data.local.entity.PacienteEntity

class AuthRepository(
    private val authDao: AuthDao
) {

    suspend fun registrarPaciente(
        usuario: UsuarioEntity,
        paciente: PacienteEntity
    ): Long {
        return authDao.registrarPaciente(usuario, paciente)
    }
}