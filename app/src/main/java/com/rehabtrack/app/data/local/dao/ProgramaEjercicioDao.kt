package com.rehabtrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rehabtrack.app.data.local.entity.ProgramaEjercicioEntity

@Dao
interface ProgramaEjercicioDao {

    @Insert
    suspend fun insertar(
        programaEjercicio: ProgramaEjercicioEntity
    ): Long

    @Insert
    suspend fun insertarTodos(
        ejercicios: List<ProgramaEjercicioEntity>
    )

    @Update
    suspend fun actualizar(
        programaEjercicio: ProgramaEjercicioEntity
    )

    @Delete
    suspend fun eliminar(
        programaEjercicio: ProgramaEjercicioEntity
    )

    @Query("""
        SELECT * FROM programa_ejercicios
        WHERE idPrograma = :idPrograma
        ORDER BY idProgramaEjercicio ASC
    """)
    suspend fun obtenerEjerciciosPrograma(
        idPrograma: Int
    ): List<ProgramaEjercicioEntity>

    @Query("""
        DELETE FROM programa_ejercicios
        WHERE idPrograma = :idPrograma
    """)
    suspend fun eliminarEjerciciosPrograma(idPrograma: Int)
}