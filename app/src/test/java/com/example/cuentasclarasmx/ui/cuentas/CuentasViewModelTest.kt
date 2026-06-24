package com.example.cuentasclarasmx.ui.cuentas

import com.example.cuentasclarasmx.data.DataRepository
import com.example.cuentasclarasmx.data.local.entity.CategoriaEntity
import com.example.cuentasclarasmx.data.local.entity.CuentaEntity
import com.example.cuentasclarasmx.data.local.entity.PresupuestoMensualEntity
import com.example.cuentasclarasmx.data.local.entity.TransaccionEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class CuentasViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initiallyEmpty() = runTest {
        val repository = FakeCuentasRepository(emptyList())
        val viewModel = CuentasViewModel(repository)
        
        val job = launch(testDispatcher) {
            viewModel.uiState.collect {}
        }
        
        val state = viewModel.uiState.value

        assertEquals(0.0, state.patrimonioNeto)
        assertEquals(0.0, state.totalActivos)
        assertEquals(0.0, state.totalPasivos)
        assertEquals(true, state.cuentas.isEmpty())
        
        job.cancel()
    }

    @Test
    fun uiState_calculatesNetWorthCorrectly() = runTest {
        val testCuentas = listOf(
            CuentaEntity(id = 1, nombre = "Efectivo", tipo = "efectivo", esPasivo = false, saldoInicial = 1500.0),
            CuentaEntity(id = 2, nombre = "Nómina", tipo = "banco", esPasivo = false, saldoInicial = 8500.0),
            CuentaEntity(id = 3, nombre = "Tarjeta Crédito", tipo = "crédito", esPasivo = true, saldoInicial = 3000.0)
        )
        val repository = FakeCuentasRepository(testCuentas)
        val viewModel = CuentasViewModel(repository)
        
        val job = launch(testDispatcher) {
            viewModel.uiState.collect {}
        }
        
        val state = viewModel.uiState.value

        assertEquals(7000.0, state.patrimonioNeto) // 10000 activos - 3000 pasivos
        assertEquals(10000.0, state.totalActivos)
        assertEquals(3000.0, state.totalPasivos)
        assertEquals(3, state.cuentas.size)
        
        job.cancel()
    }
}

private class FakeCuentasRepository(
    private val initialCuentas: List<CuentaEntity>,
    private val initialTransacciones: List<TransaccionEntity> = emptyList()
) : DataRepository {
    override val data: Flow<List<String>> = flow { emit(emptyList()) }
    override val categorias: Flow<List<CategoriaEntity>> = flow { emit(emptyList()) }
    override val cuentas: Flow<List<CuentaEntity>> = flow { emit(initialCuentas) }
    override val transacciones: Flow<List<TransaccionEntity>> = flow { emit(initialTransacciones) }
    override val presupuestos: Flow<List<PresupuestoMensualEntity>> = flow { emit(emptyList()) }
    
    override suspend fun saveCuenta(cuenta: CuentaEntity) {}
    override suspend fun deleteCuenta(cuenta: CuentaEntity) {}
    override suspend fun saveTransaccion(transaccion: TransaccionEntity) {}
    override suspend fun deleteTransaccion(transaccion: TransaccionEntity) {}
    override suspend fun savePresupuesto(presupuesto: PresupuestoMensualEntity) {}
}
