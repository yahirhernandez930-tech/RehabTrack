package com.rehabtrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pacientes",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["idUsuario"],
            childColumns = ["idUsuario"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["idUsuario"], unique = true)
    ]
)
data class PacienteEntity(

    @PrimaryKey(autoGenerate = true)
    val idPaciente: Int = 0,

    val idUsuario: Int,

    val nombre: String,

    val fechaNacimiento: String,

    val telefono: String?
)