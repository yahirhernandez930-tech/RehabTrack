package com.rehabtrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rehabtrack.app.data.local.entity.EjercicioEntity

@Dao
interface EjercicioDao {

    @Insert
    suspend fun insertar(ejercicio: EjercicioEntity): Long

    @Insert
    suspend fun insertarTodos(ejercicios: List<EjercicioEntity>)

    @Update
    suspend fun actualizar(ejercicio: EjercicioEntity)

    @Delete
    suspend fun eliminar(ejercicio: EjercicioEntity)

    @Query("""
        SELECT * FROM ejercicios
        ORDER BY nombre ASC
    """)
    suspend fun obtenerTodos(): List<EjercicioEntity>

    @Query("""
        SELECT * FROM ejercicios
        WHERE idEjercicio = :idEjercicio
        LIMIT 1
    """)
    suspend fun obtenerPorId(idEjercicio: Int): EjercicioEntity?

    @Query("""
        SELECT * FROM ejercicios
        WHERE nombre LIKE '%' || :busqueda || '%'
        ORDER BY nombre ASC
    """)
    suspend fun buscarPorNombre(busqueda: String): List<EjercicioEntity>
}