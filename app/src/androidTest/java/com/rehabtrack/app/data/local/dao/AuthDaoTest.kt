package com.rehabtrack.app.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rehabtrack.app.data.local.RehabDatabase
import com.rehabtrack.app.data.local.entity.UsuarioEntity
import com.rehabtrack.app.data.local.entity.PacienteEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthDaoTest {

    private lateinit var database: RehabDatabase
    private lateinit var usuarioDao: UsuarioDao
    private lateinit var authDao: AuthDao

    @Before
    fun configurarBaseDatos() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            RehabDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        usuarioDao = database.usuarioDao()
        authDao = database.authDao()
    }

    @After
    fun cerrarBaseDatos() {
        database.close()
    }

    @Test
    fun insertarUsuario_consultarPorCorreo_retornaUsuario() = runBlocking {
        val usuario = UsuarioEntity(
            correo = "paciente@test.com",
            passwordHash = "hash_prueba",
            rol = "PACIENTE"
        )

        usuarioDao.insertar(usuario)

        val resultado = usuarioDao.obtenerPorCorreo(
            "paciente@test.com"
        )

        assertNotNull(resultado)
        assertEquals("paciente@test.com", resultado?.correo)
        assertEquals("PACIENTE", resultado?.rol)
    }

    @Test
    fun insertarUsuario_correoDuplicado_lanzaExcepcion() = runBlocking {
        val usuario = UsuarioEntity(
            correo = "duplicado@test.com",
            passwordHash = "hash_prueba",
            rol = "PACIENTE"
        )

        usuarioDao.insertar(usuario)

        try {
            usuarioDao.insertar(usuario.copy(idUsuario = 0))
            fail("Room debería rechazar el correo duplicado")
        } catch (e: Exception) {
            assertTrue(e is android.database.sqlite.SQLiteConstraintException)
        }
    }

    @Test
    fun registrarPaciente_usuarioYPerfilQuedanRelacionados() = runBlocking {
        val usuario = UsuarioEntity(
            correo = "nuevo@test.com",
            passwordHash = "hash_prueba",
            rol = "PACIENTE"
        )

        val paciente = PacienteEntity(
            idUsuario = 0,
            nombre = "Paciente de prueba",
            fechaNacimiento = "2000-01-01",
            telefono = null
        )

        val idUsuario = authDao.registrarPaciente(
            usuario,
            paciente
        ).toInt()

        val usuarioGuardado = usuarioDao.obtenerPorId(idUsuario)
        val pacienteGuardado = database.pacienteDao()
            .obtenerPorUsuario(idUsuario)

        assertNotNull(usuarioGuardado)
        assertNotNull(pacienteGuardado)
        assertEquals(idUsuario, pacienteGuardado?.idUsuario)
    }

    @Test
    fun registrarPaciente_dosPerfilesParaMismoUsuario_lanzaExcepcion() = runBlocking {
        val usuario = UsuarioEntity(
            correo = "unico@test.com",
            passwordHash = "hash_prueba",
            rol = "PACIENTE"
        )

        val paciente = PacienteEntity(
            idUsuario = 0,
            nombre = "Primer perfil",
            fechaNacimiento = "2000-01-01",
            telefono = null
        )

        val idUsuario = authDao.registrarPaciente(
            usuario,
            paciente
        ).toInt()

        val segundoPaciente = PacienteEntity(
            idUsuario = idUsuario,
            nombre = "Segundo perfil",
            fechaNacimiento = "2001-01-01",
            telefono = null
        )

        try {
            database.pacienteDao().insertar(segundoPaciente)
            fail("No debería permitir dos perfiles para un usuario")
        } catch (e: Exception) {
            assertTrue(e is android.database.sqlite.SQLiteConstraintException)
        }
    }

    @Test
    fun eliminarUsuario_eliminaPacienteRelacionado() = runBlocking {
        val usuario = UsuarioEntity(
            correo = "eliminar@test.com",
            passwordHash = "hash_prueba",
            rol = "PACIENTE"
        )

        val paciente = PacienteEntity(
            idUsuario = 0,
            nombre = "Paciente de prueba",
            fechaNacimiento = "2000-01-01",
            telefono = null
        )

        val idUsuario = authDao.registrarPaciente(
            usuario,
            paciente
        ).toInt()

        val pacienteAntes = database.pacienteDao()
            .obtenerPorUsuario(idUsuario)

        assertNotNull(pacienteAntes)

        usuarioDao.eliminarPorId(idUsuario)

        val pacienteDespues = database.pacienteDao()
            .obtenerPorUsuario(idUsuario)

        assertNull(pacienteDespues)
    }
}