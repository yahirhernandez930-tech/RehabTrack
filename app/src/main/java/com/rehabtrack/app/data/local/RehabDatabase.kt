package com.rehabtrack.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rehabtrack.app.data.local.dao.AsignacionDao
import com.rehabtrack.app.data.local.dao.FisioterapeutaDao
import com.rehabtrack.app.data.local.dao.PacienteDao
import com.rehabtrack.app.data.local.dao.UsuarioDao
import com.rehabtrack.app.data.local.entity.AsignacionEntity
import com.rehabtrack.app.data.local.entity.FisioterapeutaEntity
import com.rehabtrack.app.data.local.entity.PacienteEntity
import com.rehabtrack.app.data.local.entity.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        PacienteEntity::class,
        FisioterapeutaEntity::class,
        AsignacionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RehabDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun pacienteDao(): PacienteDao

    abstract fun fisioterapeutaDao(): FisioterapeutaDao

    abstract fun asignacionDao(): AsignacionDao

    companion object {

        @Volatile
        private var INSTANCE: RehabDatabase? = null

        fun getDatabase(context: Context): RehabDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RehabDatabase::class.java,
                    "rehabtrack_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}