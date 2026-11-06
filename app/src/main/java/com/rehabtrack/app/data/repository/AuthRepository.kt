package com.rehabtrack.app.data.repository

import com.rehabtrack.app.data.local.dao.AuthDao
import com.rehabtrack.app.data.local.entity.UsuarioEntity
import com.rehabtrack.app.data.local.entity.PacienteEntity
import com.rehabtrack.app.data.local.entity.FisioterapeutaEntity
import com.rehabtrack.app.data.local.entity.AsignacionEntity

class AuthRepository(
    private val authDao: AuthDao
) {

    suspend fun registrarPaciente(
        usuario: UsuarioEntity,
        paciente: PacienteEntity
    ): Long {
        return authDao.registrarPaciente(usuario, paciente)
    }

    suspend fun registrarPacienteConAsignacion(
        usuario: UsuarioEntity,
        paciente: PacienteEntity,
        asignacion: AsignacionEntity
    ): Long {
        return authDao.registrarPacienteConAsignacion(
            usuario,
            paciente,
            asignacion
        )
    }

    suspend fun registrarFisioterapeuta(
        usuario: UsuarioEntity,
        fisioterapeuta: FisioterapeutaEntity
    ): Long {
        return authDao.registrarFisioterapeuta(
            usuario,
            fisioterapeuta
        )
    }
}