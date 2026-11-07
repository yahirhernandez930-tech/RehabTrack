package com.rehabtrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "programas",
    foreignKeys = [
        ForeignKey(
            entity = PacienteEntity::class,
            parentColumns = ["idPaciente"],
            childColumns = ["idPaciente"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FisioterapeutaEntity::class,
            parentColumns = ["idFisioterapeuta"],
            childColumns = ["idFisioterapeuta"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["idPaciente"]),
        Index(value = ["idFisioterapeuta"])
    ]
)
data class ProgramaEntity(
    @PrimaryKey(autoGenerate = true)
    val idPrograma: Int = 0,
    val idPaciente: Int,
    val idFisioterapeuta: Int,
    val nombre: String,
    val duracionSesiones: Int,
    val estado: String = "Activo",
    val fechaCreacion: String
)