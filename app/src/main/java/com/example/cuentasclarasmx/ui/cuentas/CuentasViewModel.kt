package com.example.cuentasclarasmx.ui.cuentas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuentasclarasmx.data.DataRepository
import com.example.cuentasclarasmx.data.local.entity.CuentaEntity
import com.example.cuentasclarasmx.data.local.entity.TransaccionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CuentaConSaldo(
    val entity: CuentaEntity,
    val saldoActual: Double
)

data class CuentasUiState(
    val cuentas: List<CuentaConSaldo> = emptyList(),
    val patrimonioNeto: Double = 0.0,
    val totalActivos: Double = 0.0,
    val totalPasivos: Double = 0.0
)

class CuentasViewModel(private val repository: DataRepository) : ViewModel() {

    val uiState: StateFlow<CuentasUiState> = combine(
        repository.cuentas,
        repository.transacciones
    ) { cuentas, transacciones ->
        val cuentasConSaldo = cuentas.map { cuenta ->
            val saldoActual = calculateSaldoActual(cuenta, transacciones)
            CuentaConSaldo(cuenta, saldoActual)
        }
        val totalActivos = cuentasConSaldo.filter { !it.entity.esPasivo }.sumOf { it.saldoActual }
        val totalPasivos = cuentasConSaldo.filter { it.entity.esPasivo }.sumOf { it.saldoActual }
        val patrimonioNeto = totalActivos - totalPasivos
        CuentasUiState(
            cuentas = cuentasConSaldo,
            patrimonioNeto = patrimonioNeto,
            totalActivos = totalActivos,
            totalPasivos = totalPasivos
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CuentasUiState()
    )

    private fun calculateSaldoActual(cuenta: CuentaEntity, transacciones: List<TransaccionEntity>): Double {
        var saldo = cuenta.saldoInicial
        for (t in transacciones) {
            val isSource = t.cuentaId == cuenta.id
            val isDest = t.cuentaDestinoId == cuenta.id
            if (!isSource && !isDest) continue

            if (cuenta.esPasivo) {
                when (t.tipo) {
                    "gasto" -> {
                        if (isSource) saldo += t.monto
                    }
                    "ingreso" -> {
                        if (isSource) saldo -= t.monto
                    }
                    "transferencia" -> {
                        if (isSource) saldo += t.monto
                        if (isDest) saldo -= t.monto
                    }
                }
            } else {
                when (t.tipo) {
                    "gasto" -> {
                        if (isSource) saldo -= t.monto
                    }
                    "ingreso" -> {
                        if (isSource) saldo += t.monto
                    }
                    "transferencia" -> {
                        if (isSource) saldo -= t.monto
                        if (isDest) saldo += t.monto
                    }
                }
            }
        }
        return saldo
    }

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
