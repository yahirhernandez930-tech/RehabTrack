package com.rehabtrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Transaction
import com.rehabtrack.app.data.local.entity.UsuarioEntity
import com.rehabtrack.app.data.local.entity.PacienteEntity
import com.rehabtrack.app.data.local.entity.FisioterapeutaEntity
import com.rehabtrack.app.data.local.entity.AsignacionEntity

@Dao
interface AuthDao {

    @Insert
    suspend fun insertarUsuario(usuario: UsuarioEntity): Long

    @Insert
    suspend fun insertarPaciente(paciente: PacienteEntity): Long

    @Insert
    suspend fun insertarAsignacion(
        asignacion: AsignacionEntity
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

    @Transaction
    suspend fun registrarPacienteConAsignacion(
        usuario: UsuarioEntity,
        paciente: PacienteEntity,
        asignacion: AsignacionEntity
    ): Long {

        val idUsuario = insertarUsuario(usuario).toInt()

        val idPaciente = insertarPaciente(
            paciente.copy(idUsuario = idUsuario)
        ).toInt()

        insertarAsignacion(
            asignacion.copy(idPaciente = idPaciente)
        )

        return idPaciente.toLong()
    }

    @Insert
    suspend fun insertarFisioterapeuta(
        fisioterapeuta: FisioterapeutaEntity
    ): Long

    @Transaction
    suspend fun registrarFisioterapeuta(
        usuario: UsuarioEntity,
        fisioterapeuta: FisioterapeutaEntity
    ): Long {

        val idUsuario = insertarUsuario(usuario).toInt()

        insertarFisioterapeuta(
            fisioterapeuta.copy(idUsuario = idUsuario)
        )

        return idUsuario.toLong()
    }
}