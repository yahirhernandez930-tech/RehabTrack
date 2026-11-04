package com.rehabtrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "asignaciones",
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
data class AsignacionEntity(

    @PrimaryKey(autoGenerate = true)
    val idAsignacion: Int = 0,

    val idPaciente: Int,

    val idFisioterapeuta: Int,

    val fechaInicio: String,

    val fechaFin: String? = null,

    val activa: Boolean = true
)