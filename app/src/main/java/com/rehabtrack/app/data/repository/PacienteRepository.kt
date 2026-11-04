package com.rehabtrack.app.data.repository

import com.rehabtrack.app.data.local.dao.PacienteDao
import com.rehabtrack.app.data.local.entity.PacienteEntity

class PacienteRepository(
    private val pacienteDao: PacienteDao
) {

    suspend fun registrarPaciente(
        paciente: PacienteEntity
    ): Long {
        return pacienteDao.insertar(paciente)
    }

    suspend fun actualizarPaciente(
        paciente: PacienteEntity
    ) {
        pacienteDao.actualizar(paciente)
    }

    suspend fun obtenerPacientePorId(
        id: Int
    ): PacienteEntity? {
        return pacienteDao.obtenerPorId(id)
    }

    suspend fun obtenerTodosLosPacientes(): List<PacienteEntity> {
        return pacienteDao.obtenerTodos()
    }

    suspend fun obtenerPacientePorUsuario(
        idUsuario: Int
    ): PacienteEntity? {
        return pacienteDao.obtenerPorUsuario(idUsuario)
    }

    suspend fun buscarPacientes(
        busqueda: String
    ): List<PacienteEntity> {
        return pacienteDao.buscarPorNombre(busqueda)
    }
}