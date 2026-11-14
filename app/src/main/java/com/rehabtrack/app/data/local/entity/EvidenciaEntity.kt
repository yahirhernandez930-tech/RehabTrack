package com.rehabtrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "evidencias",
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
        Index(value = ["idSesion"]),
        Index(value = ["idEjercicio"])
    ]
)
data class EvidenciaEntity(

    @PrimaryKey(autoGenerate = true)
    val idEvidencia: Int = 0,

    val idSesion: Int,

    val idEjercicio: Int,

    val rutaArchivo: String,

    val fechaCaptura: Long = System.currentTimeMillis()
)