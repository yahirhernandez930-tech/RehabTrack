package com.rehabtrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rehabtrack.app.data.local.entity.SesionEntity

@Dao
interface SesionDao {

    @Insert
    suspend fun insertar(sesion: SesionEntity): Long

    @Update
    suspend fun actualizar(sesion: SesionEntity)

    @Delete
    suspend fun eliminar(sesion: SesionEntity)

    @Query("""
        SELECT * FROM sesiones
        WHERE idPrograma = :idPrograma
        ORDER BY fecha ASC, hora ASC
    """)
    suspend fun obtenerSesionesPrograma(
        idPrograma: Int
    ): List<SesionEntity>

    @Query("""
        SELECT * FROM sesiones
        WHERE idSesion = :idSesion
        LIMIT 1
    """)
    suspend fun obtenerSesionPorId(
        idSesion: Int
    ): SesionEntity?

    @Query("""
        SELECT * FROM sesiones
        WHERE idPrograma = :idPrograma
        AND estado = 'Pendiente'
        ORDER BY fecha ASC, hora ASC
    """)
    suspend fun obtenerSesionesPendientes(
        idPrograma: Int
    ): List<SesionEntity>
}