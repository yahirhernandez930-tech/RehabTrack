package com.rehabtrack.app.data.repository

import com.rehabtrack.app.data.local.dao.FisioterapeutaDao
import com.rehabtrack.app.data.local.entity.FisioterapeutaEntity

class FisioterapeutaRepository(
    private val fisioterapeutaDao: FisioterapeutaDao
) {

    suspend fun registrarFisioterapeuta(
        fisioterapeuta: FisioterapeutaEntity
    ): Long {
        return fisioterapeutaDao.insertar(fisioterapeuta)
    }

    suspend fun actualizarFisioterapeuta(
        fisioterapeuta: FisioterapeutaEntity
    ) {
        fisioterapeutaDao.actualizar(fisioterapeuta)
    }

    suspend fun obtenerFisioterapeutaPorId(
        id: Int
    ): FisioterapeutaEntity? {
        return fisioterapeutaDao.obtenerPorId(id)
    }

    suspend fun obtenerFisioterapeutaPorUsuario(
        idUsuario: Int
    ): FisioterapeutaEntity? {
        return fisioterapeutaDao.obtenerPorUsuario(idUsuario)
    }
}