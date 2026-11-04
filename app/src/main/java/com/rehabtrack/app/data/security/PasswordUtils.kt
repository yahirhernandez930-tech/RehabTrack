package com.rehabtrack.app.data.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordUtils {

    private const val ITERATIONS = 600_000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16

    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val VERSION = "pbkdf2_sha256"

    /**
     * Genera una sal aleatoria para cada contraseña
     */
    private fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt
    }

    /**
     * Deriva una clave a partir de la contraseña y la sal
     */
    private fun deriveKey(
        password: String,
        salt: ByteArray,
        iterations: Int
    ): ByteArray {

        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            iterations,
            KEY_LENGTH
        )

        return try {
            SecretKeyFactory
                .getInstance(ALGORITHM)
                .generateSecret(spec)
                .encoded
        } finally {
            spec.clearPassword()
        }
    }

    /**
     * Genera el valor que se almacenará en Room
     */
    fun hashPassword(password: String): String {

        val salt = generateSalt()

        val hash = deriveKey(
            password = password,
            salt = salt,
            iterations = ITERATIONS
        )

        val encodedSalt = Base64.encodeToString(
            salt,
            Base64.NO_WRAP
        )

        val encodedHash = Base64.encodeToString(
            hash,
            Base64.NO_WRAP
        )

        return "$VERSION\$$ITERATIONS\$$encodedSalt\$$encodedHash"
    }

    /**
     * Comprueba si una contraseña coincide con el valor almacenado.
     */
    fun verifyPassword(
        password: String,
        storedHash: String
    ): Boolean {

        val parts = storedHash.split('$')

        if (parts.size != 4 || parts[0] != VERSION) {
            return false
        }

        val iterations = parts[1].toIntOrNull()
            ?: return false

        if (iterations !in 100_000..1_000_000) {
            return false
        }

        return try {

            val salt = Base64.decode(
                parts[2],
                Base64.NO_WRAP
            )

            val expectedHash = Base64.decode(
                parts[3],
                Base64.NO_WRAP
            )

            if (salt.size != SALT_LENGTH ||
                expectedHash.size != KEY_LENGTH / 8
            ) {
                return false
            }

            val calculatedHash = deriveKey(
                password = password,
                salt = salt,
                iterations = iterations
            )

            MessageDigest.isEqual(
                calculatedHash,
                expectedHash
            )

        } catch (e: IllegalArgumentException) {
            false
        }
    }
}