package com.rehabtrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rehabtrack.app.data.local.entity.AsignacionEntity

@Dao
interface AsignacionDao{

    @Insert
    suspend fun insertar(asignacion: AsignacionEntity): Long

    @Update
    suspend fun actualizar(asignacion: AsignacionEntity)

    @Query("""SELECT * FROM asignaciones WHERE idPaciente = :idPaciente AND activa = 1 LIMIT 1""")
    suspend fun obtenerAsignacionActiva(idPaciente: Int): AsignacionEntity?

    @Query("""SELECT * FROM asignaciones WHERE idPaciente = :idPaciente ORDER BY fechaInicio DESC""")
    suspend fun obtenerHistorialPaciente(idPaciente: Int): List<AsignacionEntity>

    @Query("""SELECT * FROM asignaciones WHERE idFisioterapeuta = :idFisioterapeuta AND activa = 1""")
    suspend fun obtenerPacientesAsignados(idFisioterapeuta: Int): List<AsignacionEntity>
}