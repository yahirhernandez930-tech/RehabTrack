package com.rehabtrack.app.data.security

import org.junit.Assert
import org.junit.Test

class PasswordUtilsTest {

    private val password = "Rehab2026!"

    @Test
    fun generarHash_contrasenaValida_retornaHash() {
        val hash = PasswordUtils.hashPassword(password)

        Assert.assertNotNull(hash)
        Assert.assertTrue(hash.isNotBlank())
        Assert.assertTrue(hash.startsWith("pbkdf2_sha256$"))
    }

    @Test
    fun verificarPassword_contrasenaCorrecta_retornaTrue() {
        val hash = PasswordUtils.hashPassword(password)

        val resultado = PasswordUtils.verifyPassword(
            password,
            hash
        )

        Assert.assertTrue(resultado)
    }

    @Test
    fun verificarPassword_contrasenaIncorrecta_retornaFalse() {
        val hash = PasswordUtils.hashPassword(password)

        val resultado = PasswordUtils.verifyPassword(
            "Otra2026!",
            hash
        )

        Assert.assertFalse(resultado)
    }

    @Test
    fun generarHash_mismaContrasena_generaHashesDiferentes() {
        val hash1 = PasswordUtils.hashPassword(password)
        val hash2 = PasswordUtils.hashPassword(password)

        Assert.assertNotEquals(hash1, hash2)
    }

    @Test
    fun verificarPassword_hashMalformado_retornaFalse() {
        val resultado = PasswordUtils.verifyPassword(
            password,
            "hash_invalido"
        )

        Assert.assertFalse(resultado)
    }

    @Test
    fun verificarPassword_versionDesconocida_retornaFalse() {
        val resultado = PasswordUtils.verifyPassword(
            password,
            "sha256\$600000\$sal\$hash"
        )

        Assert.assertFalse(resultado)
    }
}