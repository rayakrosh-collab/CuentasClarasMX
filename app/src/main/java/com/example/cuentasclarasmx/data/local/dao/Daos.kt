package com.example.cuentasclarasmx.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cuentasclarasmx.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CuentaDao {
    @Query("SELECT * FROM cuentas WHERE archivada = 0")
    fun getCuentasFlow(): Flow<List<CuentaEntity>>

    @Query("SELECT * FROM cuentas WHERE id = :id")
    suspend fun getCuentaById(id: Long): CuentaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCuenta(cuenta: CuentaEntity): Long

    @Update
    suspend fun updateCuenta(cuenta: CuentaEntity)

    @Delete
    suspend fun deleteCuenta(cuenta: CuentaEntity)
}

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias")
    fun getCategoriasFlow(): Flow<List<CategoriaEntity>>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun getCategoriaById(id: Long): CategoriaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoria(categoria: CategoriaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategorias(categorias: List<CategoriaEntity>)

    @Update
    suspend fun updateCategoria(categoria: CategoriaEntity)

    @Delete
    suspend fun deleteCategoria(categoria: CategoriaEntity)
}

@Dao
interface PresupuestoMensualDao {
    @Query("SELECT * FROM presupuestos_mensuales")
    fun getPresupuestosFlow(): Flow<List<PresupuestoMensualEntity>>

    @Query("SELECT * FROM presupuestos_mensuales WHERE mes = :mes")
    fun getPresupuestosByMesFlow(mes: String): Flow<List<PresupuestoMensualEntity>>

    @Query("SELECT * FROM presupuestos_mensuales WHERE subcategoriaId = :subcategoriaId AND mes = :mes")
    suspend fun getPresupuestoBySubcategoriaAndMes(subcategoriaId: Long, mes: String): PresupuestoMensualEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresupuesto(presupuesto: PresupuestoMensualEntity): Long

    @Update
    suspend fun updatePresupuesto(presupuesto: PresupuestoMensualEntity)
}

@Dao
interface TransaccionDao {
    @Query("SELECT * FROM transacciones ORDER BY fecha DESC")
    fun getTransaccionesFlow(): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE cuentaId = :cuentaId OR cuentaDestinoId = :cuentaId ORDER BY fecha DESC")
    fun getTransaccionesByCuentaFlow(cuentaId: Long): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE subcategoriaId = :subcategoriaId ORDER BY fecha DESC")
    fun getTransaccionesBySubcategoriaFlow(subcategoriaId: Long): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE id = :id")
    suspend fun getTransaccionById(id: Long): TransaccionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaccion(transaccion: TransaccionEntity): Long

    @Update
    suspend fun updateTransaccion(transaccion: TransaccionEntity)

    @Delete
    suspend fun deleteTransaccion(transaccion: TransaccionEntity)
}

@Dao
interface CompraMSIDao {
    @Query("SELECT * FROM compras_msi")
    fun getComprasMsiFlow(): Flow<List<CompraMSIEntity>>

    @Query("SELECT * FROM compras_msi WHERE id = :id")
    suspend fun getCompraMsiById(id: Long): CompraMSIEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompraMsi(compra: CompraMSIEntity): Long

    @Update
    suspend fun updateCompraMsi(compra: CompraMSIEntity)

    @Delete
    suspend fun deleteCompraMsi(compra: CompraMSIEntity)
}

@Dao
interface TransaccionRecurrenteDao {
    @Query("SELECT * FROM transacciones_recurrentes")
    fun getRecurrentesFlow(): Flow<List<TransaccionRecurrenteEntity>>

    @Query("SELECT * FROM transacciones_recurrentes WHERE id = :id")
    suspend fun getRecurrenteById(id: Long): TransaccionRecurrenteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurrente(recurrente: TransaccionRecurrenteEntity): Long

    @Update
    suspend fun updateRecurrente(recurrente: TransaccionRecurrenteEntity)

    @Delete
    suspend fun deleteRecurrente(recurrente: TransaccionRecurrenteEntity)
}

@Dao
interface AjustesDao {
    @Query("SELECT * FROM ajustes WHERE id = 1")
    fun getAjustesFlow(): Flow<AjustesEntity?>

    @Query("SELECT * FROM ajustes WHERE id = 1")
    suspend fun getAjustes(): AjustesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAjustes(ajustes: AjustesEntity)

    @Update
    suspend fun updateAjustes(ajustes: AjustesEntity)
}
