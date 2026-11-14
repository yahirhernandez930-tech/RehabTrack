package com.rehabtrack.app.data.repository

import com.rehabtrack.app.data.local.dao.EvidenciaDao
import com.rehabtrack.app.data.local.entity.EvidenciaEntity

class EvidenciaRepository(
    private val evidenciaDao: EvidenciaDao
) {

    suspend fun registrarEvidencia(
        evidencia: EvidenciaEntity
    ): Long {
        return evidenciaDao.insertar(evidencia)
    }

    suspend fun obtenerEvidenciasSesion(
        idSesion: Int
    ): List<EvidenciaEntity> {
        return evidenciaDao.obtenerEvidenciasSesion(idSesion)
    }

    suspend fun obtenerEvidenciasEjercicio(
        idSesion: Int,
        idEjercicio: Int
    ): List<EvidenciaEntity> {
        return evidenciaDao.obtenerEvidenciasEjercicio(
            idSesion,
            idEjercicio
        )
    }

    suspend fun eliminarEvidencia(
        idEvidencia: Int
    ) {
        evidenciaDao.eliminarEvidencia(idEvidencia)
    }
}