package com.rehabtrack.app.data.repository

import com.rehabtrack.app.data.local.dao.ProgramaDao
import com.rehabtrack.app.data.local.entity.ProgramaEntity

class ProgramaRepository(
    private val programaDao: ProgramaDao
) {

    suspend fun insertar(programa: ProgramaEntity): Long {
        return programaDao.insertar(programa)
    }

    suspend fun actualizar(programa: ProgramaEntity) {
        programaDao.actualizar(programa)
    }

    suspend fun eliminar(programa: ProgramaEntity) {
        programaDao.eliminar(programa)
    }

    suspend fun obtenerProgramasPaciente(
        idPaciente: Int
    ): List<ProgramaEntity> {
        return programaDao.obtenerProgramasPaciente(idPaciente)
    }

    suspend fun obtenerProgramaActivo(
        idPaciente: Int
    ): ProgramaEntity? {
        return programaDao.obtenerProgramaActivo(idPaciente)
    }

    suspend fun obtenerProgramasFisioterapeuta(
        idFisioterapeuta: Int
    ): List<ProgramaEntity> {
        return programaDao.obtenerProgramasFisioterapeuta(idFisioterapeuta)
    }
}