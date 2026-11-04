package com.rehabtrack.app

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import com.rehabtrack.app.data.local.RehabDatabase
import com.rehabtrack.app.data.local.entity.UsuarioEntity

import kotlinx.coroutines.runBlocking

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class RoomDatabaseTest {

    private lateinit var database: RehabDatabase

    @Before
    fun crearBaseDatos() {
        val context = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        database = Room.inMemoryDatabaseBuilder(
            context,
            RehabDatabase::class.java
        ).build()
    }

    @After
    fun cerrarBaseDatos() {
        database.close()
    }

    @Test
    fun insertarYConsultarUsuario() = runBlocking {

        // Crear usuario de prueba
        val usuario = UsuarioEntity(
            correo = "prueba@rehabtrack.com",
            passwordHash = "hash_de_prueba",
            rol = "PACIENTE"
        )

        // Insertar usuario
        val idGenerado = database
            .usuarioDao()
            .insertar(usuario)

        // Consultar por identificador
        val usuarioPorId = database
            .usuarioDao()
            .obtenerPorId(idGenerado.toInt())

        // Verificar resultados
        assertNotNull(usuarioPorId)
        assertEquals(usuario.correo, usuarioPorId?.correo)
        assertEquals(usuario.rol, usuarioPorId?.rol)

        // Consultar por correo
        val usuarioPorCorreo = database
            .usuarioDao()
            .obtenerPorCorreo(usuario.correo)

        assertNotNull(usuarioPorCorreo)
        assertEquals(idGenerado.toInt(), usuarioPorCorreo?.idUsuario)
    }
}