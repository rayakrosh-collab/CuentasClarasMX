package com.example.cuentasclarasmx.ui.transacciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuentasclarasmx.data.DataRepository
import com.example.cuentasclarasmx.data.local.entity.CategoriaEntity
import com.example.cuentasclarasmx.data.local.entity.CuentaEntity
import com.example.cuentasclarasmx.data.local.entity.TransaccionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TransaccionConDetalles(
    val entity: TransaccionEntity,
    val cuentaNombre: String,
    val cuentaDestinoNombre: String?,
    val subcategoriaNombre: String?
)

data class SubcategoriaItem(
    val entity: CategoriaEntity,
    val padreNombre: String
)

data class TransaccionesUiState(
    val transacciones: List<TransaccionConDetalles> = emptyList(),
    val cuentas: List<CuentaEntity> = emptyList(),
    val subcategorias: List<SubcategoriaItem> = emptyList(),
    val searchQuery: String = ""
)

class TransaccionesViewModel(private val repository: DataRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<TransaccionesUiState> = combine(
        repository.transacciones,
        repository.cuentas,
        repository.categorias,
        _searchQuery
    ) { transacciones, cuentas, categorias, query ->
        
        val madresMap = categorias.filter { it.categoriaPadreId == null }.associateBy { it.id }
        val subcategoriasItems = categorias.filter { it.categoriaPadreId != null }.map { sub ->
            val padreName = madresMap[sub.categoriaPadreId]?.nombre ?: ""
            SubcategoriaItem(sub, padreName)
        }

        val cuentasMap = cuentas.associateBy { it.id }
        val categoriasMap = categorias.associateBy { it.id }

        val filteredTransacciones = transacciones.filter {
            query.isBlank() || it.descripcion.contains(query, ignoreCase = true)
        }.map { t ->
            val cuentaName = cuentasMap[t.cuentaId]?.nombre ?: "Desconocida"
            val cuentaDestinoName = t.cuentaDestinoId?.let { cuentasMap[it]?.nombre }
            val subName = t.subcategoriaId?.let { categoriasMap[it]?.nombre }
            TransaccionConDetalles(t, cuentaName, cuentaDestinoName, subName)
        }

        TransaccionesUiState(
            transacciones = filteredTransacciones,
            cuentas = cuentas,
            subcategorias = subcategoriasItems,
            searchQuery = query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransaccionesUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun saveTransaccion(transaccion: TransaccionEntity) {
        viewModelScope.launch {
            repository.saveTransaccion(transaccion)
        }
    }

    fun deleteTransaccion(transaccion: TransaccionEntity) {
        viewModelScope.launch {
            repository.deleteTransaccion(transaccion)
        }
    }
}
