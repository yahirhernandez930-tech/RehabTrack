package com.rehabtrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rehabtrack.app.data.local.entity.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Update
    suspend fun actualizar(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE idUsuario = :id")
    suspend fun obtenerPorId(id: Int): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerPorCorreo(correo: String): UsuarioEntity?
}