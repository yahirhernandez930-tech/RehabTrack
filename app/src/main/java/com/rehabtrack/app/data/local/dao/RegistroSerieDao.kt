package com.rehabtrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rehabtrack.app.data.local.entity.RegistroSerieEntity

@Dao
interface RegistroSerieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(registro: RegistroSerieEntity): Long

    @Query("""
        SELECT * FROM registro_series
        WHERE idSesion = :idSesion
        AND idEjercicio = :idEjercicio
        ORDER BY numeroSerie ASC
    """)
    suspend fun obtenerRegistrosEjercicio(
        idSesion: Int,
        idEjercicio: Int
    ): List<RegistroSerieEntity>

    @Query("""
        SELECT * FROM registro_series
        WHERE idSesion = :idSesion
        ORDER BY idEjercicio ASC, numeroSerie ASC
    """)
    suspend fun obtenerRegistrosSesion(
        idSesion: Int
    ): List<RegistroSerieEntity>

    @Query("""
        DELETE FROM registro_series
        WHERE idSesion = :idSesion
    """)
    suspend fun eliminarRegistrosSesion(idSesion: Int)
}