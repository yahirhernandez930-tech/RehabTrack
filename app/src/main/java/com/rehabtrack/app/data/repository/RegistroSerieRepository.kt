package com.rehabtrack.app.data.repository

import com.rehabtrack.app.data.local.dao.RegistroSerieDao
import com.rehabtrack.app.data.local.entity.RegistroSerieEntity

class RegistroSerieRepository(
    private val registroSerieDao: RegistroSerieDao
) {

    suspend fun insertar(
        registro: RegistroSerieEntity
    ): Long {
        return registroSerieDao.insertar(registro)
    }

    suspend fun obtenerRegistrosEjercicio(
        idSesion: Int,
        idEjercicio: Int
    ): List<RegistroSerieEntity> {
        return registroSerieDao.obtenerRegistrosEjercicio(
            idSesion,
            idEjercicio
        )
    }

    suspend fun obtenerRegistrosSesion(
        idSesion: Int
    ): List<RegistroSerieEntity> {
        return registroSerieDao.obtenerRegistrosSesion(idSesion)
    }

    suspend fun eliminarRegistrosSesion(idSesion: Int) {
        registroSerieDao.eliminarRegistrosSesion(idSesion)
    }
}