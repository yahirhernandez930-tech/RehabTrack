package com.rehabtrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ejercicios")
data class EjercicioEntity(
    @PrimaryKey(autoGenerate = true)
    val idEjercicio: Int = 0,
    val nombre: String,
    val descripcion: String,
    val instrucciones: String,
    val videoUrl: String? = null
)