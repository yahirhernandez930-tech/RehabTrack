package com.rehabtrack.app.data.repository

import com.rehabtrack.app.data.local.dao.UsuarioDao
import com.rehabtrack.app.data.local.entity.UsuarioEntity

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {

    suspend fun registrarUsuario(
        usuario: UsuarioEntity
    ): Long {
        return usuarioDao.insertar(usuario)
    }

    suspend fun obtenerUsuarioPorCorreo(
        correo: String
    ): UsuarioEntity? {
        return usuarioDao.obtenerPorCorreo(correo)
    }

    suspend fun obtenerUsuarioPorId(
        id: Int
    ): UsuarioEntity? {
        return usuarioDao.obtenerPorId(id)
    }

    suspend fun actualizarUsuario(
        usuario: UsuarioEntity
    ) {
        usuarioDao.actualizar(usuario)
    }

    suspend fun contarAdministradores(): Int {
        return usuarioDao.contarAdministradores()
    }
}