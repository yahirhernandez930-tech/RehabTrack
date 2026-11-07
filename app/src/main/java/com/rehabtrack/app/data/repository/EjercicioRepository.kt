package com.rehabtrack.app.data.repository

import com.rehabtrack.app.data.local.dao.EjercicioDao
import com.rehabtrack.app.data.local.entity.EjercicioEntity

class EjercicioRepository(
    private val ejercicioDao: EjercicioDao
) {

    suspend fun insertar(ejercicio: EjercicioEntity): Long {
        return ejercicioDao.insertar(ejercicio)
    }

    suspend fun insertarTodos(ejercicios: List<EjercicioEntity>) {
        ejercicioDao.insertarTodos(ejercicios)
    }

    suspend fun actualizar(ejercicio: EjercicioEntity) {
        ejercicioDao.actualizar(ejercicio)
    }

    suspend fun eliminar(ejercicio: EjercicioEntity) {
        ejercicioDao.eliminar(ejercicio)
    }

    suspend fun obtenerTodos(): List<EjercicioEntity> {
        return ejercicioDao.obtenerTodos()
    }

    suspend fun obtenerPorId(idEjercicio: Int): EjercicioEntity? {
        return ejercicioDao.obtenerPorId(idEjercicio)
    }

    suspend fun buscarPorNombre(busqueda: String): List<EjercicioEntity> {
        return ejercicioDao.buscarPorNombre(busqueda)
    }
}