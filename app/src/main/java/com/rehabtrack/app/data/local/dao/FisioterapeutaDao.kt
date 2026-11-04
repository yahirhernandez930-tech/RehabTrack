package com.rehabtrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rehabtrack.app.data.local.entity.FisioterapeutaEntity

@Dao
interface FisioterapeutaDao {

    @Insert
    suspend fun insertar(fisioterapeuta: FisioterapeutaEntity): Long

    @Update
    suspend fun actualizar(fisioterapeuta: FisioterapeutaEntity)

    @Query("""SELECT * FROM fisioterapeutas WHERE idFisioterapeuta = :id""")
    suspend fun obtenerPorId(id: Int): FisioterapeutaEntity?

    @Query("""SELECT * FROM fisioterapeutas WHERE idUsuario = :idUsuario LIMIT 1""")
    suspend fun obtenerPorUsuario(idUsuario: Int): FisioterapeutaEntity?
}