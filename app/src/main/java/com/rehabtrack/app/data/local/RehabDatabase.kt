package com.rehabtrack.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.rehabtrack.app.data.local.dao.AsignacionDao
import com.rehabtrack.app.data.local.dao.FisioterapeutaDao
import com.rehabtrack.app.data.local.dao.PacienteDao
import com.rehabtrack.app.data.local.dao.UsuarioDao
import com.rehabtrack.app.data.local.dao.AuthDao
import com.rehabtrack.app.data.local.dao.ProgramaDao
import com.rehabtrack.app.data.local.dao.EjercicioDao
import com.rehabtrack.app.data.local.dao.ProgramaEjercicioDao

import com.rehabtrack.app.data.local.entity.AsignacionEntity
import com.rehabtrack.app.data.local.entity.FisioterapeutaEntity
import com.rehabtrack.app.data.local.entity.PacienteEntity
import com.rehabtrack.app.data.local.entity.UsuarioEntity
import com.rehabtrack.app.data.local.entity.ProgramaEntity
import com.rehabtrack.app.data.local.entity.EjercicioEntity
import com.rehabtrack.app.data.local.entity.ProgramaEjercicioEntity

import androidx.room.withTransaction

import com.rehabtrack.app.data.local.dao.SesionDao
import com.rehabtrack.app.data.local.entity.SesionEntity

import com.rehabtrack.app.data.local.dao.RegistroSerieDao
import com.rehabtrack.app.data.local.entity.RegistroSerieEntity
import androidx.room.Index

import com.rehabtrack.app.data.local.dao.EvidenciaDao
import com.rehabtrack.app.data.local.entity.EvidenciaEntity

@Database(
    entities = [
        UsuarioEntity::class,
        PacienteEntity::class,
        FisioterapeutaEntity::class,
        AsignacionEntity::class,
        ProgramaEntity::class,
        EjercicioEntity::class,
        ProgramaEjercicioEntity::class,
        SesionEntity::class,
        RegistroSerieEntity::class,
        EvidenciaEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class RehabDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    abstract fun pacienteDao(): PacienteDao

    abstract fun fisioterapeutaDao(): FisioterapeutaDao

    abstract fun asignacionDao(): AsignacionDao

    abstract fun authDao(): AuthDao

    abstract fun programaDao(): ProgramaDao

    abstract fun ejercicioDao(): EjercicioDao

    abstract fun programaEjercicioDao(): ProgramaEjercicioDao

    abstract fun sesionDao(): SesionDao

    abstract fun registroSerieDao(): RegistroSerieDao

    abstract fun evidenciaDao(): EvidenciaDao

    suspend fun <T> ejecutarTransaccion(
        operacion: suspend () -> T
    ): T {
        return withTransaction {
            operacion()
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: RehabDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS programas (
                        idPrograma INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        idPaciente INTEGER NOT NULL,
                        idFisioterapeuta INTEGER NOT NULL,
                        nombre TEXT NOT NULL,
                        duracionSesiones INTEGER NOT NULL,
                        estado TEXT NOT NULL,
                        fechaCreacion TEXT NOT NULL,
                        FOREIGN KEY(idPaciente)
                            REFERENCES pacientes(idPaciente)
                            ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(idFisioterapeuta)
                            REFERENCES fisioterapeutas(idFisioterapeuta)
                            ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """)

                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_programas_idPaciente
                    ON programas(idPaciente)
                """)

                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_programas_idFisioterapeuta
                    ON programas(idFisioterapeuta)
                """)

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS ejercicios (
                        idEjercicio INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        descripcion TEXT NOT NULL,
                        instrucciones TEXT NOT NULL
                    )
                """)

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS programa_ejercicios (
                        idProgramaEjercicio INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        idPrograma INTEGER NOT NULL,
                        idEjercicio INTEGER NOT NULL,
                        series INTEGER NOT NULL,
                        repeticiones INTEGER NOT NULL,
                        FOREIGN KEY(idPrograma)
                            REFERENCES programas(idPrograma)
                            ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(idEjercicio)
                            REFERENCES ejercicios(idEjercicio)
                            ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """)

                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_programa_ejercicios_idPrograma
                    ON programa_ejercicios(idPrograma)
                """)

                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_programa_ejercicios_idEjercicio
                    ON programa_ejercicios(idEjercicio)
                """)
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
            CREATE TABLE IF NOT EXISTS sesiones (
                idSesion INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                idPrograma INTEGER NOT NULL,
                fecha TEXT NOT NULL,
                hora TEXT NOT NULL,
                estado TEXT NOT NULL,
                nivelDolor INTEGER,
                observaciones TEXT,
                FOREIGN KEY(idPrograma)
                    REFERENCES programas(idPrograma)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """)

                db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_sesiones_idPrograma
            ON sesiones(idPrograma)
        """)
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
            CREATE TABLE IF NOT EXISTS registro_series (
                idRegistroSerie INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                idSesion INTEGER NOT NULL,
                idEjercicio INTEGER NOT NULL,
                numeroSerie INTEGER NOT NULL,
                repeticionesRealizadas INTEGER NOT NULL,
                FOREIGN KEY(idSesion)
                    REFERENCES sesiones(idSesion)
                    ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(idEjercicio)
                    REFERENCES ejercicios(idEjercicio)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """)

                db.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS
            index_registro_series_idSesion_idEjercicio_numeroSerie
            ON registro_series(idSesion, idEjercicio, numeroSerie)
        """)
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {

            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
            CREATE TABLE IF NOT EXISTS evidencias (
                idEvidencia INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                idSesion INTEGER NOT NULL,
                idEjercicio INTEGER NOT NULL,
                rutaArchivo TEXT NOT NULL,
                fechaCaptura INTEGER NOT NULL,
                FOREIGN KEY(idSesion)
                    REFERENCES sesiones(idSesion)
                    ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(idEjercicio)
                    REFERENCES ejercicios(idEjercicio)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """)

                db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_evidencias_idSesion
            ON evidencias(idSesion)
        """)

                db.execSQL("""
            CREATE INDEX IF NOT EXISTS index_evidencias_idEjercicio
            ON evidencias(idEjercicio)
        """)
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {

            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE ejercicios ADD COLUMN videoUrl TEXT"
                )
            }
        }

        fun getDatabase(context: Context): RehabDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RehabDatabase::class.java,
                    "rehabtrack_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6
                    )
                    .build()

                INSTANCE = instance

                instance
            }
        }

        fun cerrarDatabase() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }
    }
}