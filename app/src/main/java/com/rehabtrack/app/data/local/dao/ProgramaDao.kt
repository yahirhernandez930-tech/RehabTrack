package com.rehabtrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rehabtrack.app.data.local.entity.ProgramaEntity

@Dao
interface ProgramaDao {

    @Insert
    suspend fun insertar(programa: ProgramaEntity): Long

    @Update
    suspend fun actualizar(programa: ProgramaEntity)

    @Delete
    suspend fun eliminar(programa: ProgramaEntity)

    @Query("""
        SELECT * FROM programas
        WHERE idPaciente = :idPaciente
        ORDER BY idPrograma DESC
    """)
    suspend fun obtenerProgramasPaciente(idPaciente: Int): List<ProgramaEntity>

    @Query("""
        SELECT * FROM programas
        WHERE idPaciente = :idPaciente
        AND estado = 'Activo'
        LIMIT 1
    """)
    suspend fun obtenerProgramaActivo(idPaciente: Int): ProgramaEntity?

    @Query("""
        SELECT * FROM programas
        WHERE idFisioterapeuta = :idFisioterapeuta
        ORDER BY idPrograma DESC
    """)
    suspend fun obtenerProgramasFisioterapeuta(
        idFisioterapeuta: Int
    ): List<ProgramaEntity>
}