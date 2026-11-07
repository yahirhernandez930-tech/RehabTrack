package com.rehabtrack.app.data.repository

import com.rehabtrack.app.data.local.dao.ProgramaEjercicioDao
import com.rehabtrack.app.data.local.entity.ProgramaEjercicioEntity

class ProgramaEjercicioRepository(
    private val programaEjercicioDao: ProgramaEjercicioDao
) {

    suspend fun insertar(
        programaEjercicio: ProgramaEjercicioEntity
    ): Long {
        return programaEjercicioDao.insertar(programaEjercicio)
    }

    suspend fun insertarTodos(
        ejercicios: List<ProgramaEjercicioEntity>
    ) {
        programaEjercicioDao.insertarTodos(ejercicios)
    }

    suspend fun actualizar(
        programaEjercicio: ProgramaEjercicioEntity
    ) {
        programaEjercicioDao.actualizar(programaEjercicio)
    }

    suspend fun eliminar(
        programaEjercicio: ProgramaEjercicioEntity
    ) {
        programaEjercicioDao.eliminar(programaEjercicio)
    }

    suspend fun obtenerEjerciciosPrograma(
        idPrograma: Int
    ): List<ProgramaEjercicioEntity> {
        return programaEjercicioDao.obtenerEjerciciosPrograma(idPrograma)
    }

    suspend fun eliminarEjerciciosPrograma(idPrograma: Int) {
        programaEjercicioDao.eliminarEjerciciosPrograma(idPrograma)
    }
}