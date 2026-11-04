package com.rehabtrack.app

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry

import com.rehabtrack.app.data.local.RehabDatabase
import com.rehabtrack.app.data.local.entity.UsuarioEntity

import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.*

import java.util.UUID

@RunWith(AndroidJUnit4::class)
class PersistentRoomDatabaseTest {

    @Test
    fun verificarPersistenciaUsuario() = runBlocking {

        val context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        val nombreBD = "rehabtrack_persistence_test"

        // Eliminar una base de datos de pruebas anterior.
        context.deleteDatabase(nombreBD)

        // Crear una base de datos física.
        var database = Room.databaseBuilder(
            context,
            RehabDatabase::class.java,
            nombreBD
        ).build()

        try {

            // Crear un usuario de prueba.
            val usuario = UsuarioEntity(
                correo = "persistencia_${UUID.randomUUID()}@test.com",
                passwordHash = "hash_de_prueba",
                rol = "PACIENTE"
            )

            // Insertar el usuario.
            val id = database.usuarioDao().insertar(usuario)

            // Cerrar la conexión.
            database.close()

            // Abrir nuevamente la misma base de datos.
            database = Room.databaseBuilder(
                context,
                RehabDatabase::class.java,
                nombreBD
            ).build()

            // Recuperar el usuario.
            val usuarioRecuperado = database
                .usuarioDao()
                .obtenerPorCorreo(usuario.correo)

            // Verificar que los datos permanecen.
            assertNotNull(usuarioRecuperado)
            assertEquals(id.toInt(), usuarioRecuperado?.idUsuario)
            assertEquals(usuario.correo, usuarioRecuperado?.correo)
            assertEquals(usuario.rol, usuarioRecuperado?.rol)

        } finally {

            database.close()

            // Eliminar la base de datos temporal.
            context.deleteDatabase(nombreBD)
        }
    }
}