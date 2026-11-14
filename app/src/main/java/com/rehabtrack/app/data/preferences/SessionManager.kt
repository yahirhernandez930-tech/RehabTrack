package com.rehabtrack.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "rehabtrack_session"
)

data class UserSession(
    val idUsuario: Int,
    val rol: String
)

class SessionManager(private val context: Context) {

    companion object {
        private val ID_USUARIO = intPreferencesKey("id_usuario")
        private val ROL_USUARIO = stringPreferencesKey("rol_usuario")
    }

    // Recuperar la sesión almacenada.
    val sessionFlow: Flow<UserSession?> = context.dataStore.data.map { preferences ->

        val idUsuario = preferences[ID_USUARIO]
        val rol = preferences[ROL_USUARIO]

        if (idUsuario != null && rol != null) {
            UserSession(
                idUsuario = idUsuario,
                rol = rol
            )
        } else {
            null
        }
    }

    // Guardar la sesión del usuario autenticado.
    suspend fun guardarSesion(idUsuario: Int, rol: String) {
        context.dataStore.edit { preferences ->
            preferences[ID_USUARIO] = idUsuario
            preferences[ROL_USUARIO] = rol
        }
    }

    // Eliminar la sesión al cerrar sesión.
    suspend fun cerrarSesion() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}