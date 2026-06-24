package com.example.cuentasclarasmx.data

import com.example.cuentasclarasmx.data.local.AppDatabase
import com.example.cuentasclarasmx.data.local.entity.CategoriaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface DataRepository {
    val data: Flow<List<String>>
    val categorias: Flow<List<CategoriaEntity>>
}

class DefaultDataRepository(private val database: AppDatabase) : DataRepository {
    override val data: Flow<List<String>> = database.categoriaDao().getCategoriasFlow().map { list ->
        list.map { it.nombre }
    }
    
    override val categorias: Flow<List<CategoriaEntity>> = database.categoriaDao().getCategoriasFlow()
}
