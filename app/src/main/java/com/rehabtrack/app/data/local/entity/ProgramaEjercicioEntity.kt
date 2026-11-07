package com.rehabtrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "programa_ejercicios",
    foreignKeys = [
        ForeignKey(
            entity = ProgramaEntity::class,
            parentColumns = ["idPrograma"],
            childColumns = ["idPrograma"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EjercicioEntity::class,
            parentColumns = ["idEjercicio"],
            childColumns = ["idEjercicio"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["idPrograma"]),
        Index(value = ["idEjercicio"])
    ]
)
data class ProgramaEjercicioEntity(
    @PrimaryKey(autoGenerate = true)
    val idProgramaEjercicio: Int = 0,
    val idPrograma: Int,
    val idEjercicio: Int,
    val series: Int,
    val repeticiones: Int
)