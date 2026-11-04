package com.rehabtrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rehabtrack.app.data.local.entity.PacienteEntity

@Dao
interface PacienteDao {

    @Insert
    suspend fun insertar(paciente: PacienteEntity): Long

    @Update
    suspend fun actualizar(paciente: PacienteEntity)

    @Query("SELECT * FROM pacientes WHERE idPaciente = :id")
    suspend fun obtenerPorId(id: Int): PacienteEntity?

    @Query("SELECT * FROM pacientes ORDER BY nombre ASC")
    suspend fun obtenerTodos(): List<PacienteEntity>

    @Query("SELECT * FROM pacientes WHERE idUsuario = :idUsuario LIMIT 1")
    suspend fun obtenerPorUsuario(idUsuario: Int): PacienteEntity?

    @Query("SELECT * FROM pacientes WHERE nombre LIKE '%' || :busqueda || '%' ORDER BY nombre ASC")
    suspend fun buscarPorNombre(busqueda: String): List<PacienteEntity>
}