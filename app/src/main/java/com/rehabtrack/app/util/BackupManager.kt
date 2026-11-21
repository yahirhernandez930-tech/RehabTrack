package com.rehabtrack.app.util

import android.content.Context
import com.rehabtrack.app.data.local.RehabDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import android.database.sqlite.SQLiteDatabase
import java.util.zip.ZipInputStream
import java.io.InputStream
import java.util.zip.ZipFile
import org.json.JSONObject
import java.io.IOException

object BackupManager {

    suspend fun crearRespaldo(
        context: Context,
        database: RehabDatabase
    ): File = withContext(Dispatchers.IO) {

        val dbFile = context.getDatabasePath("rehabtrack_database")

        require(dbFile.exists()) {
            "No se encontró la base de datos."
        }

        val archivoZip = File(
            context.cacheDir,
            "RehabTrack_Backup_${System.currentTimeMillis()}.zip"
        )

        val archivoBDTemporal = File(
            context.cacheDir,
            "RehabTrack_Database_${System.currentTimeMillis()}.sqlite"
        )

        try {

            // 1. Crear una copia independiente de la base de datos.
            crearCopiaTemporal(database, archivoBDTemporal)

            // 2. Crear el archivo ZIP.
            ZipOutputStream(
                BufferedOutputStream(FileOutputStream(archivoZip))
            ).use { zip ->

                // Agregar la copia independiente de la base de datos.
                agregarArchivo(
                    zip,
                    archivoBDTemporal,
                    "database/rehabtrack_database"
                )

                // 3. Agregar fotografías de evidencias.
                val directorioEvidencias = File(
                    context.filesDir,
                    "evidencias"
                )

                if (directorioEvidencias.exists()) {

                    directorioEvidencias.walkTopDown()
                        .filter { it.isFile }
                        .forEach { archivo ->

                            val rutaRelativa = archivo
                                .relativeTo(directorioEvidencias)
                                .invariantSeparatorsPath

                            agregarArchivo(
                                zip,
                                archivo,
                                "evidencias/$rutaRelativa"
                            )
                        }
                }

                // 4. Agregar metadatos.
                val metadata = """
                    {
                        "app": "RehabTrack",
                        "database": "rehabtrack_database",
                        "databaseVersion": 6,
                        "backupDate": ${System.currentTimeMillis()}
                    }
                """.trimIndent()

                zip.putNextEntry(ZipEntry("metadata.json"))
                zip.write(metadata.toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }

            // 5. Validar la integridad del respaldo.
            validarRespaldo(archivoZip)

            archivoZip

        } catch (e: Exception) {

            archivoZip.delete()
            throw e

        } finally {

            archivoBDTemporal.delete()
        }
    }

    private fun agregarArchivo(
        zip: ZipOutputStream,
        archivo: File,
        nombreEntrada: String
    ) {

        zip.putNextEntry(ZipEntry(nombreEntrada))

        BufferedInputStream(FileInputStream(archivo)).use { input ->
            input.copyTo(zip)
        }

        zip.closeEntry()
    }


    private fun validarRespaldo(archivoZip: File) {

        val directorioTemporal = File(
            archivoZip.parentFile,
            "validacion_${System.currentTimeMillis()}"
        )

        if (!directorioTemporal.mkdirs()) {
            throw IllegalStateException(
                "No se pudo crear el directorio de validación."
            )
        }

        var baseDatos: SQLiteDatabase? = null

        try {

            // 1. Extraer únicamente los archivos de SQLite.
            ZipInputStream(
                BufferedInputStream(FileInputStream(archivoZip))
            ).use { zip ->

                var entrada = zip.nextEntry

                while (entrada != null) {

                    val destino = when (entrada.name) {
                        "database/rehabtrack_database" ->
                            File(directorioTemporal, "rehabtrack_database")

                        else -> null
                    }

                    if (destino != null && !entrada.isDirectory) {
                        FileOutputStream(destino).use { output ->
                            zip.copyTo(output)
                        }
                    }

                    zip.closeEntry()
                    entrada = zip.nextEntry
                }
            }

            // 2. Comprobar que exista la base de datos.
            val archivoBD = File(
                directorioTemporal,
                "rehabtrack_database"
            )

            if (!archivoBD.exists() || archivoBD.length() == 0L) {
                throw IllegalStateException(
                    "El respaldo no contiene una base de datos válida."
                )
            }

            // 3. Abrir la copia en modo de solo lectura.
            baseDatos = SQLiteDatabase.openDatabase(
                archivoBD.absolutePath,
                null,
                SQLiteDatabase.OPEN_READONLY
            )

            // 4. Ejecutar comprobación de integridad.
            baseDatos.rawQuery(
                "PRAGMA integrity_check",
                null
            ).use { cursor ->

                if (!cursor.moveToFirst() ||
                    cursor.getString(0) != "ok"
                ) {
                    throw IllegalStateException(
                        "La comprobación de integridad de SQLite falló."
                    )
                }
            }

            // 5. Comprobar la versión de la base de datos.
            baseDatos.rawQuery(
                "PRAGMA user_version",
                null
            ).use { cursor ->

                if (!cursor.moveToFirst() ||
                    cursor.getInt(0) != 6
                ) {
                    throw IllegalStateException(
                        "La versión de la base de datos no es la esperada."
                    )
                }
            }

        } finally {

            baseDatos?.close()
            directorioTemporal.deleteRecursively()
        }
    }

    private fun validarBaseDatosArchivo(archivoBD: File) {

        if (!archivoBD.exists() || archivoBD.length() == 0L) {
            throw IllegalStateException(
                "La base de datos restaurada no existe o está vacía."
            )
        }

        var baseDatos: SQLiteDatabase? = null

        try {
            // Abrir la base de datos en modo de solo lectura.
            baseDatos = SQLiteDatabase.openDatabase(
                archivoBD.absolutePath,
                null,
                SQLiteDatabase.OPEN_READONLY
            )

            // Comprobar la integridad de SQLite.
            baseDatos.rawQuery(
                "PRAGMA integrity_check",
                null
            ).use { cursor ->

                if (!cursor.moveToFirst() ||
                    cursor.getString(0) != "ok"
                ) {
                    throw IllegalStateException(
                        "La integridad de la base restaurada no es correcta."
                    )
                }
            }

            // Comprobar que corresponda a la versión 6.
            baseDatos.rawQuery(
                "PRAGMA user_version",
                null
            ).use { cursor ->

                if (!cursor.moveToFirst() ||
                    cursor.getInt(0) != 6
                ) {
                    throw IllegalStateException(
                        "La versión de la base restaurada no es compatible."
                    )
                }
            }

        } finally {
            baseDatos?.close()
        }
    }

    private fun crearCopiaTemporal(
        database: RehabDatabase,
        archivoDestino: File
    ) {
        // 1. Consultar la versión de SQLite.
        val cursor = database.openHelper.writableDatabase
            .query("SELECT sqlite_version()")

        val versionSQLite = cursor.use {
            if (!it.moveToFirst()) {
                throw IllegalStateException(
                    "No se pudo consultar la versión de SQLite."
                )
            }
            it.getString(0)
        }

        // 2. Comprobar que SQLite sea 3.27.0 o superior.
        val componentes = versionSQLite
            .split(".")
            .take(3)
            .map { it.toIntOrNull() ?: 0 }

        val versionCompatible = when {
            componentes[0] > 3 -> true
            componentes[0] < 3 -> false
            componentes[1] > 27 -> true
            componentes[1] < 27 -> false
            else -> componentes[2] >= 0
        }

        if (!versionCompatible) {
            throw IllegalStateException(
                "La versión de SQLite ($versionSQLite) " +
                        "no admite VACUUM INTO. Se requiere SQLite 3.27.0 o superior."
            )
        }

        // 3. Preparar la ruta del archivo temporal.
        if (archivoDestino.exists()) {
            archivoDestino.delete()
        }

        val rutaSegura = archivoDestino.absolutePath
            .replace("'", "''")

        // 4. Crear una copia consistente de la base de datos.
        database.openHelper.writableDatabase.execSQL(
            "VACUUM INTO '$rutaSegura'"
        )

        // 5. Comprobar que se haya creado el archivo.
        if (!archivoDestino.exists() || archivoDestino.length() == 0L) {
            throw IllegalStateException(
                "No se pudo generar la copia temporal de la base de datos."
            )
        }
    }

    private fun validarMetadatos(archivoZip: File) {
        ZipFile(archivoZip).use { zip ->

            val entrada = zip.getEntry("metadata.json")
                ?: throw IllegalStateException(
                    "El respaldo no contiene metadata.json."
                )

            if (entrada.isDirectory || entrada.size > 65536) {
                throw IllegalStateException(
                    "El archivo de metadatos no es válido."
                )
            }

            val contenido = zip.getInputStream(entrada)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }

            val metadata = JSONObject(contenido)

            if (metadata.optString("app") != "RehabTrack") {
                throw IllegalStateException(
                    "El respaldo no pertenece a RehabTrack."
                )
            }

            if (metadata.optString("database") != "rehabtrack_database") {
                throw IllegalStateException(
                    "El nombre de la base de datos no coincide."
                )
            }

            if (metadata.optInt("databaseVersion", -1) != 6) {
                throw IllegalStateException(
                    "La versión del respaldo no es compatible con RehabTrack."
                )
            }
        }
    }
    private fun validarRespaldoCompleto(archivoZip: File) {

        // 1. Validar identidad y versión del respaldo.
        validarMetadatos(archivoZip)

        // 2. Validar integridad de la base de datos.
        validarRespaldo(archivoZip)
    }

    private fun extraerRespaldoTemporal(
        archivoZip: File,
        directorioDestino: File
    ) {
        if (directorioDestino.exists()) {
            throw IllegalStateException(
                "El directorio temporal ya existe."
            )
        }

        if (!directorioDestino.mkdirs()) {
            throw IllegalStateException(
                "No se pudo crear el directorio temporal."
            )
        }

        try {
            ZipFile(archivoZip).use { zip ->

                val nombresProcesados = mutableSetOf<String>()

                val entradas = zip.entries()

                while (entradas.hasMoreElements()) {
                    val entrada = entradas.nextElement()
                    val nombre = entrada.name

                    // Evitar rutas absolutas y traversal.
                    if (
                        nombre.startsWith("/") ||
                        nombre.contains("\\") ||
                        nombre.split("/").any {
                            it == ".." || it == "."
                        }
                    ) {
                        throw IllegalStateException(
                            "El respaldo contiene una ruta no permitida."
                        )
                    }

                    if (!nombresProcesados.add(nombre)) {
                        throw IllegalStateException(
                            "El respaldo contiene archivos duplicados."
                        )
                    }

                    // Solo permitir los elementos propios del respaldo.
                    val esFotografiaValida =
                        nombre.startsWith("evidencias/evidencia_") &&
                                nombre.endsWith(".jpg") &&
                                nombre.count { it == '/' } == 1

                    val rutaPermitida =
                        nombre == "metadata.json" ||
                                nombre == "database/" ||
                                nombre == "database/rehabtrack_database" ||
                                nombre == "evidencias/" ||
                                esFotografiaValida

                    if (!rutaPermitida) {
                        throw IllegalStateException(
                            "El respaldo contiene un archivo no reconocido: $nombre"
                        )
                    }

                    if (entrada.isDirectory) continue

                    val destino = File(directorioDestino, nombre)

                    val rutaCanonica = destino.canonicalPath
                    val directorioCanonico =
                        directorioDestino.canonicalPath + File.separator

                    if (!rutaCanonica.startsWith(directorioCanonico)) {
                        throw IllegalStateException(
                            "Se detectó una ruta de extracción no segura."
                        )
                    }

                    destino.parentFile?.let {
                        if (!it.exists() && !it.mkdirs()) {
                            throw IllegalStateException(
                                "No se pudo crear un directorio del respaldo."
                            )
                        }
                    }

                    zip.getInputStream(entrada).use { input ->
                        FileOutputStream(destino).use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }

            val archivoBD = File(
                directorioDestino,
                "database/rehabtrack_database"
            )

            if (!archivoBD.exists() || archivoBD.length() == 0L) {
                throw IllegalStateException(
                    "No se encontró la base de datos en el respaldo."
                )
            }

        } catch (e: Exception) {
            directorioDestino.deleteRecursively()
            throw e
        }
    }

    private fun validarFotografias(directorioTemporal: File) {

        val directorioEvidencias = File(
            directorioTemporal,
            "evidencias"
        )

        if (!directorioEvidencias.exists()) {
            return
        }

        val archivos = directorioEvidencias.listFiles()
            ?: throw IllegalStateException(
                "No se pudieron leer las fotografías del respaldo."
            )

        archivos.forEach { archivo ->

            if (!archivo.isFile) {
                throw IllegalStateException(
                    "El respaldo contiene una carpeta no permitida en evidencias."
                )
            }

            if (!Regex(
                    """evidencia_[0-9a-fA-F-]{36}\.jpg"""
                ).matches(archivo.name)
            ) {
                throw IllegalStateException(
                    "Nombre de fotografía no válido: ${archivo.name}"
                )
            }

            if (archivo.length() == 0L) {
                throw IllegalStateException(
                    "Se encontró una fotografía vacía: ${archivo.name}"
                )
            }

            val firma = ByteArray(3)

            archivo.inputStream().use { input ->
                if (input.read(firma) != 3) {
                    throw IllegalStateException(
                        "No se pudo leer la fotografía: ${archivo.name}"
                    )
                }
            }

            if (
                firma[0] != 0xFF.toByte() ||
                firma[1] != 0xD8.toByte() ||
                firma[2] != 0xFF.toByte()
            ) {
                throw IllegalStateException(
                    "El archivo no contiene una imagen JPG válida: ${archivo.name}"
                )
            }
        }
    }

    suspend fun prepararRestauracion(
        archivoZip: File,
        context: Context
    ): File = withContext(Dispatchers.IO) {

        require(archivoZip.exists() && archivoZip.isFile) {
            "No se encontró el archivo de respaldo."
        }

        // 1. Validar metadatos e integridad de SQLite.
        validarRespaldoCompleto(archivoZip)

        // 2. Crear directorio temporal único.
        val directorioTemporal = File(
            context.cacheDir,
            "restauracion_${System.currentTimeMillis()}"
        )

        try {
            // 3. Extraer los archivos.
            extraerRespaldoTemporal(
                archivoZip,
                directorioTemporal
            )

            // 4. Validar las fotografías.
            validarFotografias(directorioTemporal)

            // 5. Devolver los archivos preparados.
            directorioTemporal

        } catch (e: Exception) {
            directorioTemporal.deleteRecursively()
            throw e
        }
    }


    private fun guardarRegistroRestauracion(
        context: Context,
        directorioSeguridad: File,
        existiaBDAnterior: Boolean,
        existiaWALAnterior: Boolean,
        existiaSHMAnterior: Boolean,
        existianEvidenciasAnteriores: Boolean
    ) {
        val archivoRegistro = File(
            context.filesDir,
            "restauracion_pendiente.json"
        )

        val archivoTemporal = File(
            context.filesDir,
            "restauracion_pendiente.tmp"
        )

        val datos = JSONObject().apply {
            put("directorioSeguridad", directorioSeguridad.name)
            put("existiaBDAnterior", existiaBDAnterior)
            put("existiaWALAnterior", existiaWALAnterior)
            put("existiaSHMAnterior", existiaSHMAnterior)
            put("existianEvidenciasAnteriores", existianEvidenciasAnteriores)
        }

        FileOutputStream(archivoTemporal).use { output ->
            output.write(datos.toString().toByteArray(Charsets.UTF_8))
            output.fd.sync()
        }

        if (archivoRegistro.exists() && !archivoRegistro.delete()) {
            archivoTemporal.delete()
            throw IOException("No se pudo actualizar el registro de restauración.")
        }

        if (!archivoTemporal.renameTo(archivoRegistro)) {
            archivoTemporal.delete()
            throw IOException("No se pudo guardar el registro de restauración.")
        }
    }

    private fun validarFotografiasRestauradas(
        archivoBD: File,
        context: Context
    ) {
        var baseDatos: SQLiteDatabase? = null

        try {
            baseDatos = SQLiteDatabase.openDatabase(
                archivoBD.absolutePath,
                null,
                SQLiteDatabase.OPEN_READONLY
            )

            val directorioEvidencias = File(
                context.filesDir,
                "evidencias"
            ).canonicalFile

            baseDatos.rawQuery(
                "SELECT rutaArchivo FROM evidencias",
                null
            ).use { cursor ->

                val prefijoPermitido =
                    directorioEvidencias.absolutePath + File.separator

                while (cursor.moveToNext()) {
                    val ruta = cursor.getString(0)
                        ?: throw IllegalStateException(
                            "Se encontró una evidencia sin ruta."
                        )

                    val fotografia = File(ruta).canonicalFile

                    if (!fotografia.path.startsWith(prefijoPermitido)) {
                        throw IllegalStateException(
                            "Una fotografía tiene una ruta fuera del directorio de evidencias."
                        )
                    }

                    if (!fotografia.isFile || fotografia.length() == 0L) {
                        throw IllegalStateException(
                            "No se encontró una fotografía restaurada: ${fotografia.name}"
                        )
                    }
                }
            }
        } finally {
            baseDatos?.close()
        }
    }

    private fun validarRestauracionCompleta(
        context: Context
    ) {
        val archivoBD = context.getDatabasePath(
            "rehabtrack_database"
        )

        validarBaseDatosArchivo(archivoBD)
        validarFotografiasRestauradas(archivoBD, context)

        // Comprobar también que Room reconozca el esquema.
        validarAperturaRoom(context)
    }


    private fun validarAperturaRoom(context: Context) {
        try {
            val database = RehabDatabase.getDatabase(context)

            // Forzar la apertura real y la validación del esquema de Room.
            database.openHelper.writableDatabase

        } finally {
            RehabDatabase.cerrarDatabase()
        }
    }

    private fun recuperarArchivosAnteriores(
        context: Context,
        directorioSeguridad: File,
        datos: JSONObject
    ) {
        val archivoBDActual = context.getDatabasePath(
            "rehabtrack_database"
        )

        val archivoWAL = File("${archivoBDActual.absolutePath}-wal")
        val archivoSHM = File("${archivoBDActual.absolutePath}-shm")

        val directorioEvidenciasActual = File(
            context.filesDir,
            "evidencias"
        )

        val archivoBDAnterior = File(
            directorioSeguridad,
            "rehabtrack_database"
        )

        val archivoWALAnterior = File(
            directorioSeguridad,
            "rehabtrack_database-wal"
        )

        val archivoSHMAnterior = File(
            directorioSeguridad,
            "rehabtrack_database-shm"
        )

        val evidenciasAnteriores = File(
            directorioSeguridad,
            "evidencias"
        )

        if (datos.optBoolean("existiaBDAnterior")) {
            require(
                archivoBDAnterior.isFile &&
                        archivoBDAnterior.length() > 0L
            ) {
                "No se encontró la base de datos original para recuperarla."
            }
        }

        RehabDatabase.cerrarDatabase()

        archivoBDActual.delete()
        archivoWAL.delete()
        archivoSHM.delete()

        if (datos.optBoolean("existiaBDAnterior")) {
            archivoBDAnterior.copyTo(
                archivoBDActual,
                overwrite = true
            )
        }

        if (datos.optBoolean("existiaWALAnterior")) {
            require(archivoWALAnterior.isFile) {
                "No se encontró el archivo WAL original."
            }

            archivoWALAnterior.copyTo(
                archivoWAL,
                overwrite = true
            )
        }

        if (datos.optBoolean("existiaSHMAnterior")) {
            require(archivoSHMAnterior.isFile) {
                "No se encontró el archivo SHM original."
            }

            archivoSHMAnterior.copyTo(
                archivoSHM,
                overwrite = true
            )
        }

        if (directorioEvidenciasActual.exists()) {
            directorioEvidenciasActual.deleteRecursively()
        }

        if (datos.optBoolean("existianEvidenciasAnteriores")) {
            require(evidenciasAnteriores.isDirectory) {
                "No se encontró el directorio original de evidencias."
            }

            evidenciasAnteriores.copyRecursively(
                directorioEvidenciasActual,
                overwrite = true
            )
        }
    }

    /**
     * Se ejecuta al iniciar la aplicación, antes de inicializar Room.
     * Devuelve true si encontró y resolvió una restauración pendiente.
     */
    suspend fun verificarRestauracionPendiente(
        context: Context
    ): Boolean = withContext(Dispatchers.IO) {

        val archivoRegistro = File(
            context.filesDir,
            "restauracion_pendiente.json"
        )

        if (!archivoRegistro.exists()) {
            return@withContext false
        }

        val datos = JSONObject(
            archivoRegistro.readText(Charsets.UTF_8)
        )

        val nombreDirectorio = datos.optString("directorioSeguridad")

        require(
            nombreDirectorio.startsWith("respaldo_anterior_") &&
                    File(nombreDirectorio).name == nombreDirectorio
        ) {
            "El registro de restauración contiene una ruta no válida."
        }

        val directorioSeguridad = File(
            context.filesDir,
            nombreDirectorio
        )

        try {
            // Si los nuevos datos están completos, se acepta la restauración.
            validarRestauracionCompleta(context)

            if (!archivoRegistro.delete()) {
                throw IOException(
                    "La restauración es válida, pero no se pudo cerrar su registro."
                )
            }
        } catch (errorValidacion: Exception) {
            // Si algo falla, se recupera la copia anterior.
            recuperarArchivosAnteriores(
                context,
                directorioSeguridad,
                datos
            )

            // Comprobar que la recuperación también quedó íntegra.
            val archivoBDAnterior = context.getDatabasePath(
                "rehabtrack_database"
            )

            if (datos.optBoolean("existiaBDAnterior")) {
                validarBaseDatosArchivo(archivoBDAnterior)
            }

            if (datos.optBoolean("existianEvidenciasAnteriores")) {
                validarFotografiasRestauradas(
                    archivoBDAnterior,
                    context
                )
            }

            // Comprobar que Room pueda abrir también los datos recuperados.
            validarAperturaRoom(context)

            if (!archivoRegistro.delete()) {
                throw IOException(
                    "Los archivos anteriores fueron recuperados, pero no se pudo cerrar el registro."
                )
            }
        }

        true
    }

    suspend fun aplicarRestauracion(
        context: Context,
        directorioPreparado: File
    ): File = withContext(Dispatchers.IO) {

        // 1. Identificar los archivos nuevos.
        val archivoBDNueva = File(
            directorioPreparado,
            "database/rehabtrack_database"
        )

        val directorioEvidenciasNuevo = File(
            directorioPreparado,
            "evidencias"
        )

        // 2. Identificar los archivos actuales.
        val archivoBDActual = context.getDatabasePath(
            "rehabtrack_database"
        )

        val directorioEvidenciasActual = File(
            context.filesDir,
            "evidencias"
        )

        val archivoWAL = File("${archivoBDActual.absolutePath}-wal")
        val archivoSHM = File("${archivoBDActual.absolutePath}-shm")

        // 3. Preparar una carpeta para conservar los datos anteriores.
        val directorioSeguridad = File(
            context.filesDir,
            "respaldo_anterior_${System.currentTimeMillis()}"
        )

        require(archivoBDNueva.isFile && archivoBDNueva.length() > 0L) {
            "No se encontró la nueva base de datos."
        }

        require(directorioPreparado.isDirectory) {
            "El directorio preparado no es válido."
        }

        require(directorioSeguridad.mkdirs()) {
            "No se pudo preparar la recuperación de seguridad."
        }

        val archivoBDAnterior = File(
            directorioSeguridad,
            "rehabtrack_database"
        )

        val archivoWALAnterior = File(
            directorioSeguridad,
            "rehabtrack_database-wal"
        )

        val archivoSHMAnterior = File(
            directorioSeguridad,
            "rehabtrack_database-shm"
        )

        val evidenciasAnteriores = File(
            directorioSeguridad,
            "evidencias"
        )

        var baseDatosCerrada = false
        var archivosModificados = false
        var existiaBDAnterior = false
        var existianEvidenciasAnteriores = false
        var existiaWALAnterior = false
        var existiaSHMAnterior = false

        try {
            // 4. Cerrar Room antes de respaldar o sustituir archivos.
            RehabDatabase.cerrarDatabase()
            baseDatosCerrada = true

            // 5. Conservar los archivos originales después del cierre.
            if (archivoBDActual.exists()) {
                archivoBDActual.copyTo(archivoBDAnterior)
                existiaBDAnterior = true
            }

            if (archivoWAL.exists()) {
                archivoWAL.copyTo(archivoWALAnterior)
                existiaWALAnterior = true
            }

            if (archivoSHM.exists()) {
                archivoSHM.copyTo(archivoSHMAnterior)
                existiaSHMAnterior = true
            }

            if (directorioEvidenciasActual.exists()) {
                directorioEvidenciasActual.copyRecursively(
                    evidenciasAnteriores,
                    overwrite = true
                )
                existianEvidenciasAnteriores = true
            }

            // 6. Registrar la recuperación antes de sustituir archivos.
            guardarRegistroRestauracion(
                context = context,
                directorioSeguridad = directorioSeguridad,
                existiaBDAnterior = existiaBDAnterior,
                existiaWALAnterior = existiaWALAnterior,
                existiaSHMAnterior = existiaSHMAnterior,
                existianEvidenciasAnteriores = existianEvidenciasAnteriores
            )

            // A partir de aquí comienza la sustitución.
            archivosModificados = true

            // Eliminar archivos auxiliares de la base anterior.
            archivoWAL.delete()
            archivoSHM.delete()

            // Sustituir la base de datos.
            archivoBDNueva.copyTo(
                archivoBDActual,
                overwrite = true
            )

            // Comprobar la base de datos ya restaurada.
            validarBaseDatosArchivo(archivoBDActual)

            // Sustituir las fotografías.
            if (directorioEvidenciasActual.exists()) {
                directorioEvidenciasActual.deleteRecursively()
            }

            if (directorioEvidenciasNuevo.exists()) {
                directorioEvidenciasNuevo.copyRecursively(
                    directorioEvidenciasActual,
                    overwrite = true
                )
            }

            // La copia anterior se conserva para verificar
            // posteriormente que la restauración funciona.

            return@withContext directorioSeguridad
        } catch (e: Exception) {

            // 7. Si hubo cambios, intentar recuperar los originales.
            if (archivosModificados) {
                try {
                    // Recuperar la base de datos.
                    archivoBDActual.delete()
                    archivoWAL.delete()
                    archivoSHM.delete()

                    if (existiaBDAnterior) {
                        archivoBDAnterior.copyTo(
                            archivoBDActual,
                            overwrite = true
                        )
                    }

                    if (existiaWALAnterior) {
                        archivoWALAnterior.copyTo(
                            archivoWAL,
                            overwrite = true
                        )
                    }

                    if (existiaSHMAnterior) {
                        archivoSHMAnterior.copyTo(
                            archivoSHM,
                            overwrite = true
                        )
                    }


                    // Recuperar las fotografías.
                    if (directorioEvidenciasActual.exists()) {
                        directorioEvidenciasActual.deleteRecursively()
                    }

                    if (existianEvidenciasAnteriores) {
                        require(evidenciasAnteriores.isDirectory) {
                            "No se encontró el directorio original de evidencias."
                        }

                        evidenciasAnteriores.copyRecursively(
                            directorioEvidenciasActual,
                            overwrite = true
                        )
                    }

                    // 1. Comprobar que la base de datos original se recuperó.
                    if (existiaBDAnterior) {
                        validarBaseDatosArchivo(archivoBDActual)

                        // 2. Comprobar que todas las fotografías registradas existen.
                        validarFotografiasRestauradas(
                            archivoBDActual,
                            context
                        )

                        // 3. Comprobar que Room puede abrir la base recuperada.
                        validarAperturaRoom(context)
                    } else {
                        throw IllegalStateException(
                            "No existía una base de datos original que pudiera recuperarse."
                        )
                    }

                    // 4. Eliminar el registro únicamente después de superar
                    // todas las comprobaciones anteriores.
                    val archivoRegistro = File(
                        context.filesDir,
                        "restauracion_pendiente.json"
                    )

                    if (archivoRegistro.exists() && !archivoRegistro.delete()) {
                        throw IllegalStateException(
                            "Los archivos originales fueron recuperados, " +
                                    "pero no se pudo eliminar el registro de restauración."
                        )
                    }


                } catch (errorRecuperacion: Exception) {
                    throw IllegalStateException(
                        "Falló la restauración y también la recuperación de los archivos originales. No cierres la aplicación ni elimines sus datos. Conserva la carpeta de recuperación.",
                        errorRecuperacion
                    )
                }
            }

            throw IllegalStateException(
                if (archivosModificados) {
                    "La restauración falló. Se intentaron recuperar los datos originales."
                } else {
                    "No se pudo preparar la restauración. Los archivos originales no fueron sustituidos."
                },
                e
            )
        }
    }
}