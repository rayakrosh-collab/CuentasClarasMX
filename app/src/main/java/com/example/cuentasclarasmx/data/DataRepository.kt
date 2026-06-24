package com.example.cuentasclarasmx.data

import com.example.cuentasclarasmx.data.local.AppDatabase
import com.example.cuentasclarasmx.data.local.entity.CategoriaEntity
import com.example.cuentasclarasmx.data.local.entity.CuentaEntity
import com.example.cuentasclarasmx.data.local.entity.PresupuestoMensualEntity
import com.example.cuentasclarasmx.data.local.entity.TransaccionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface DataRepository {
    val data: Flow<List<String>>
    val categorias: Flow<List<CategoriaEntity>>
    val cuentas: Flow<List<CuentaEntity>>
    val transacciones: Flow<List<TransaccionEntity>>
    val presupuestos: Flow<List<PresupuestoMensualEntity>>
    suspend fun saveCuenta(cuenta: CuentaEntity)
    suspend fun deleteCuenta(cuenta: CuentaEntity)
    suspend fun saveTransaccion(transaccion: TransaccionEntity)
    suspend fun deleteTransaccion(transaccion: TransaccionEntity)
    suspend fun savePresupuesto(presupuesto: PresupuestoMensualEntity)
}

class DefaultDataRepository(private val database: AppDatabase) : DataRepository {
    override val data: Flow<List<String>> = database.categoriaDao().getCategoriasFlow().map { list ->
        list.map { it.nombre }
    }
    
    override val categorias: Flow<List<CategoriaEntity>> = database.categoriaDao().getCategoriasFlow()
    
    override val cuentas: Flow<List<CuentaEntity>> = database.cuentaDao().getCuentasFlow()
    
    override val transacciones: Flow<List<TransaccionEntity>> = database.transaccionDao().getTransaccionesFlow()
    
    override val presupuestos: Flow<List<PresupuestoMensualEntity>> = database.presupuestoMensualDao().getPresupuestosFlow()
    
    override suspend fun saveCuenta(cuenta: CuentaEntity) {
        database.cuentaDao().insertCuenta(cuenta)
    }
    
    override suspend fun deleteCuenta(cuenta: CuentaEntity) {
        database.cuentaDao().deleteCuenta(cuenta)
    }

    override suspend fun saveTransaccion(transaccion: TransaccionEntity) {
        database.transaccionDao().insertTransaccion(transaccion)
    }

    override suspend fun deleteTransaccion(transaccion: TransaccionEntity) {
        database.transaccionDao().deleteTransaccion(transaccion)
    }

    override suspend fun savePresupuesto(presupuesto: PresupuestoMensualEntity) {
        database.presupuestoMensualDao().insertPresupuesto(presupuesto)
    }
}
