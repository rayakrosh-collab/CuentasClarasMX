package com.example.cuentasclarasmx.ui.cuentas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuentasclarasmx.data.DataRepository
import com.example.cuentasclarasmx.data.local.entity.CuentaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CuentasUiState(
    val cuentas: List<CuentaEntity> = emptyList(),
    val patrimonioNeto: Double = 0.0,
    val totalActivos: Double = 0.0,
    val totalPasivos: Double = 0.0
)

class CuentasViewModel(private val repository: DataRepository) : ViewModel() {

    val uiState: StateFlow<CuentasUiState> = repository.cuentas
        .map { list ->
            val totalActivos = list.filter { !it.esPasivo }.sumOf { it.saldoInicial }
            val totalPasivos = list.filter { it.esPasivo }.sumOf { it.saldoInicial }
            val patrimonioNeto = totalActivos - totalPasivos
            CuentasUiState(
                cuentas = list,
                patrimonioNeto = patrimonioNeto,
                totalActivos = totalActivos,
                totalPasivos = totalPasivos
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CuentasUiState()
        )

    fun saveCuenta(cuenta: CuentaEntity) {
        viewModelScope.launch {
            repository.saveCuenta(cuenta)
        }
    }

    fun deleteCuenta(cuenta: CuentaEntity) {
        viewModelScope.launch {
            repository.deleteCuenta(cuenta)
        }
    }
}
