package com.rehabtrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [
        Index(value = ["correo"], unique = true)
    ]
)
data class UsuarioEntity(

    @PrimaryKey(autoGenerate = true)
    val idUsuario: Int = 0,

    val correo: String,

    val passwordHash: String,

    val rol: String
)