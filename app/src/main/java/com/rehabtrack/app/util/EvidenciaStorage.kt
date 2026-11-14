package com.rehabtrack.app.util

import android.content.Context
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

object EvidenciaStorage {

    fun crearArchivoEvidencia(context: Context): File {
        val directorio = File(context.filesDir, "evidencias")

        if (!directorio.exists()) {
            directorio.mkdirs()
        }

        val nombreArchivo = "evidencia_${UUID.randomUUID()}.jpg"

        return File(directorio, nombreArchivo).apply {
            createNewFile()
        }
    }

    fun obtenerUri(context: Context, archivo: File) =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
}