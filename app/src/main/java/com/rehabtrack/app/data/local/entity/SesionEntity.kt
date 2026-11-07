package com.rehabtrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sesiones",
    foreignKeys = [
        ForeignKey(
            entity = ProgramaEntity::class,
            parentColumns = ["idPrograma"],
            childColumns = ["idPrograma"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["idPrograma"])
    ]
)
data class SesionEntity(

    @PrimaryKey(autoGenerate = true)
    val idSesion: Int = 0,

    val idPrograma: Int,

    val fecha: String,

    val hora: String,

    val estado: String = "Pendiente",

    val nivelDolor: Int? = null,

    val observaciones: String? = null
)