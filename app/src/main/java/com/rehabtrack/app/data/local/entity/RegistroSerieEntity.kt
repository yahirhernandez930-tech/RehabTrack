package com.rehabtrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "registro_series",
    foreignKeys = [
        ForeignKey(
            entity = SesionEntity::class,
            parentColumns = ["idSesion"],
            childColumns = ["idSesion"],
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
        Index(
            value = ["idSesion", "idEjercicio", "numeroSerie"],
            unique = true
        )
    ]
)
data class RegistroSerieEntity(
    @PrimaryKey(autoGenerate = true)
    val idRegistroSerie: Int = 0,

    val idSesion: Int,
    val idEjercicio: Int,
    val numeroSerie: Int,
    val repeticionesRealizadas: Int
)