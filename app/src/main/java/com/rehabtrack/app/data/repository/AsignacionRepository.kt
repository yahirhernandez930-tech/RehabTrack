package com.rehabtrack.app.data.repository

import com.rehabtrack.app.data.local.dao.AsignacionDao
import com.rehabtrack.app.data.local.entity.AsignacionEntity
import com.rehabtrack.app.data.local.entity.PacienteEntity

class AsignacionRepository(
    private val asignacionDao: AsignacionDao
) {

    suspend fun insertar(asignacion: AsignacionEntity): Long {
        return asignacionDao.insertar(asignacion)
    }

    suspend fun actualizar(asignacion: AsignacionEntity) {
        asignacionDao.actualizar(asignacion)
    }

    suspend fun obtenerAsignacionActiva(
        idPaciente: Int
    ): AsignacionEntity? {
        return asignacionDao.obtenerAsignacionActiva(idPaciente)
    }

    suspend fun obtenerHistorialPaciente(
        idPaciente: Int
    ): List<AsignacionEntity> {
        return asignacionDao.obtenerHistorialPaciente(idPaciente)
    }

    suspend fun obtenerPacientesAsignados(
        idFisioterapeuta: Int
    ): List<AsignacionEntity> {
        return asignacionDao.obtenerPacientesAsignados(idFisioterapeuta)
    }

    suspend fun obtenerPacientesDisponibles(): List<PacienteEntity> {
        return asignacionDao.obtenerPacientesDisponibles()
    }
}