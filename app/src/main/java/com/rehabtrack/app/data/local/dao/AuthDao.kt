package com.rehabtrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Transaction
import com.rehabtrack.app.data.local.entity.UsuarioEntity
import com.rehabtrack.app.data.local.entity.PacienteEntity

@Dao
interface AuthDao {

    @Insert
    suspend fun insertarUsuario(
        usuario: UsuarioEntity
    ): Long

    @Insert
    suspend fun insertarPaciente(
        paciente: PacienteEntity
    ): Long

    @Transaction
    suspend fun registrarPaciente(
        usuario: UsuarioEntity,
        paciente: PacienteEntity
    ): Long {

        val idUsuario = insertarUsuario(usuario).toInt()

        insertarPaciente(
            paciente.copy(idUsuario = idUsuario)
        )

        return idUsuario.toLong()
    }
}
