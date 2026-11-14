package com.rehabtrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rehabtrack.app.data.local.entity.EvidenciaEntity

@Dao
interface EvidenciaDao {

    @Insert
    suspend fun insertar(evidencia: EvidenciaEntity): Long

    @Query("""
        SELECT * FROM evidencias
        WHERE idSesion = :idSesion
        ORDER BY fechaCaptura ASC
    """)
    suspend fun obtenerEvidenciasSesion(
        idSesion: Int
    ): List<EvidenciaEntity>

    @Query("""
        SELECT * FROM evidencias
        WHERE idSesion = :idSesion
        AND idEjercicio = :idEjercicio
        ORDER BY fechaCaptura ASC
    """)
    suspend fun obtenerEvidenciasEjercicio(
        idSesion: Int,
        idEjercicio: Int
    ): List<EvidenciaEntity>

    @Query("""
        DELETE FROM evidencias
        WHERE idEvidencia = :idEvidencia
    """)
    suspend fun eliminarEvidencia(idEvidencia: Int)
}