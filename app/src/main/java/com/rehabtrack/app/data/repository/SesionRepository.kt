package com.rehabtrack.app.data.repository

import com.rehabtrack.app.data.local.dao.SesionDao
import com.rehabtrack.app.data.local.entity.SesionEntity

class SesionRepository(
    private val sesionDao: SesionDao
) {

    suspend fun insertar(sesion: SesionEntity): Long {
        return sesionDao.insertar(sesion)
    }

    suspend fun actualizar(sesion: SesionEntity) {
        sesionDao.actualizar(sesion)
    }

    suspend fun eliminar(sesion: SesionEntity) {
        sesionDao.eliminar(sesion)
    }

    suspend fun obtenerSesionesPrograma(
        idPrograma: Int
    ): List<SesionEntity> {
        return sesionDao.obtenerSesionesPrograma(idPrograma)
    }

    suspend fun obtenerSesionPorId(
        idSesion: Int
    ): SesionEntity? {
        return sesionDao.obtenerSesionPorId(idSesion)
    }

    suspend fun obtenerSesionesPendientes(
        idPrograma: Int
    ): List<SesionEntity> {
        return sesionDao.obtenerSesionesPendientes(idPrograma)
    }
}