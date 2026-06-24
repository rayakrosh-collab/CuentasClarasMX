package com.example.cuentasclarasmx.ui.transacciones

import com.example.cuentasclarasmx.data.DataRepository
import com.example.cuentasclarasmx.data.local.entity.CategoriaEntity
import com.example.cuentasclarasmx.data.local.entity.CuentaEntity
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

class TransaccionesViewModelTest {

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
        val repository = FakeTransaccionesRepository(emptyList(), emptyList(), emptyList())
        val viewModel = TransaccionesViewModel(repository)

        val job = launch(testDispatcher) {
            viewModel.uiState.collect {}
        }

        val state = viewModel.uiState.value
        assertEquals(true, state.transacciones.isEmpty())
        assertEquals(true, state.cuentas.isEmpty())
        assertEquals(true, state.subcategorias.isEmpty())
        assertEquals("", state.searchQuery)

        job.cancel()
    }

    @Test
    fun uiState_filtersTransactionsByQuery() = runTest {
        val testCuentas = listOf(
            CuentaEntity(id = 1, nombre = "BBVA Débito", tipo = "débito", esPasivo = false, saldoInicial = 5000.0)
        )
        val testCategorias = listOf(
            CategoriaEntity(id = 1, nombre = "Alimentación", categoriaPadreId = null),
            CategoriaEntity(id = 2, nombre = "Supermercado", categoriaPadreId = 1)
        )
        val testTransacciones = listOf(
            TransaccionEntity(id = 1, tipo = "gasto", fecha = System.currentTimeMillis(), monto = 150.0, descripcion = "Walmart Super", cuentaId = 1, subcategoriaId = 2),
            TransaccionEntity(id = 2, tipo = "gasto", fecha = System.currentTimeMillis(), monto = 80.0, descripcion = "Gasolina", cuentaId = 1, subcategoriaId = null)
        )

        val repository = FakeTransaccionesRepository(testTransacciones, testCuentas, testCategorias)
        val viewModel = TransaccionesViewModel(repository)

        val job = launch(testDispatcher) {
            viewModel.uiState.collect {}
        }

        // Test filter
        viewModel.onSearchQueryChanged("Super")
        var state = viewModel.uiState.value
        assertEquals(1, state.transacciones.size)
        assertEquals("Walmart Super", state.transacciones[0].entity.descripcion)
        assertEquals("BBVA Débito", state.transacciones[0].cuentaNombre)
        assertEquals("Supermercado", state.transacciones[0].subcategoriaNombre)

        // Reset filter
        viewModel.onSearchQueryChanged("")
        state = viewModel.uiState.value
        assertEquals(2, state.transacciones.size)

        job.cancel()
    }
}

private class FakeTransaccionesRepository(
    private val transaccionesList: List<TransaccionEntity>,
    private val cuentasList: List<CuentaEntity>,
    private val categoriasList: List<CategoriaEntity>
) : DataRepository {
    override val data: Flow<List<String>> = flow { emit(emptyList()) }
    override val categorias: Flow<List<CategoriaEntity>> = flow { emit(categoriasList) }
    override val cuentas: Flow<List<CuentaEntity>> = flow { emit(cuentasList) }
    override val transacciones: Flow<List<TransaccionEntity>> = flow { emit(transaccionesList) }

    override suspend fun saveCuenta(cuenta: CuentaEntity) {}
    override suspend fun deleteCuenta(cuenta: CuentaEntity) {}
    override suspend fun saveTransaccion(transaccion: TransaccionEntity) {}
    override suspend fun deleteTransaccion(transaccion: TransaccionEntity) {}
}
