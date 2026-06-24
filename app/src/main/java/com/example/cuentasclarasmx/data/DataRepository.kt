package com.example.cuentasclarasmx.data

import com.example.cuentasclarasmx.data.local.AppDatabase
import com.example.cuentasclarasmx.data.local.entity.CategoriaEntity
import com.example.cuentasclarasmx.data.local.entity.CuentaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface DataRepository {
    val data: Flow<List<String>>
    val categorias: Flow<List<CategoriaEntity>>
    val cuentas: Flow<List<CuentaEntity>>
    suspend fun saveCuenta(cuenta: CuentaEntity)
    suspend fun deleteCuenta(cuenta: CuentaEntity)
}

class DefaultDataRepository(private val database: AppDatabase) : DataRepository {
    override val data: Flow<List<String>> = database.categoriaDao().getCategoriasFlow().map { list ->
        list.map { it.nombre }
    }
    
    override val categorias: Flow<List<CategoriaEntity>> = database.categoriaDao().getCategoriasFlow()
    
    override val cuentas: Flow<List<CuentaEntity>> = database.cuentaDao().getCuentasFlow()
    
    override suspend fun saveCuenta(cuenta: CuentaEntity) {
        database.cuentaDao().insertCuenta(cuenta)
    }
    
    override suspend fun deleteCuenta(cuenta: CuentaEntity) {
        database.cuentaDao().deleteCuenta(cuenta)
    }
}
