package com.rehabtrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fisioterapeutas",
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
data class FisioterapeutaEntity(

    @PrimaryKey(autoGenerate = true)
    val idFisioterapeuta: Int = 0,

    val idUsuario: Int,

    val nombreCompleto: String,

    val especialidad: String,

    val telefono: String?
)