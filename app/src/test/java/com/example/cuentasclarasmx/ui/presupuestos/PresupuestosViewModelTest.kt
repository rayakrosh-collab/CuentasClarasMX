package com.example.cuentasclarasmx.ui.presupuestos

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PresupuestosViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val sdf = SimpleDateFormat("yyyy-MM", Locale.US)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_calculatesIncomeAndAvailableCorrectly() = runTest {
        val monthStr = sdf.format(Date()) // Mes actual

        val testCategorias = listOf(
            CategoriaEntity(id = 1, nombre = "Alimentación", categoriaPadreId = null),
            CategoriaEntity(id = 2, nombre = "Supermercado", categoriaPadreId = 1)
        )

        // Ingreso en este mes: 10000, Gasto en este mes: 1500 (de los cuales 1200 son de Supermercado)
        val testTransacciones = listOf(
            TransaccionEntity(id = 1, tipo = "ingreso", fecha = System.currentTimeMillis(), monto = 10000.0, descripcion = "Sueldo", cuentaId = 1),
            TransaccionEntity(id = 2, tipo = "gasto", fecha = System.currentTimeMillis(), monto = 1200.0, descripcion = "Walmart", cuentaId = 1, subcategoriaId = 2),
            TransaccionEntity(id = 3, tipo = "gasto", fecha = System.currentTimeMillis() - 40 * 24 * 60 * 60 * 1000L, monto = 5000.0, descripcion = "Otro mes", cuentaId = 1) // Transacción de otro mes
        )

        // Presupuesto asignado a Supermercado este mes: 3000
        val testPresupuestos = listOf(
            PresupuestoMensualEntity(id = 1, subcategoriaId = 2, mes = monthStr, montoAsignado = 3000.0, arrastre = 0.0)
        )

        val repository = FakePresupuestosRepository(testCategorias, testPresupuestos, testTransacciones)
        val viewModel = PresupuestosViewModel(repository)

        val job = launch(testDispatcher) {
            viewModel.uiState.collect {}
        }

        val state = viewModel.uiState.value

        assertEquals(10000.0, state.ingresosDelMes)
        assertEquals(3000.0, state.totalAsignado)
        assertEquals(7000.0, state.disponibleParaAsignar) // 10000 - 3000

        // Verificar mapeo de subcategoría
        val madre = state.categoriasMadre.first()
        val sub = madre.subcategorias.first()
        assertEquals(3000.0, sub.asignado)
        assertEquals(1200.0, sub.gastado)
        assertEquals(1800.0, sub.disponible) // 3000 - 1200

        job.cancel()
    }
}

private class FakePresupuestosRepository(
    private val categoriasList: List<CategoriaEntity>,
    private val presupuestosList: List<PresupuestoMensualEntity>,
    private val transaccionesList: List<TransaccionEntity>
) : DataRepository {
    override val data: Flow<List<String>> = flow { emit(emptyList()) }
    override val categorias: Flow<List<CategoriaEntity>> = flow { emit(categoriasList) }
    override val cuentas: Flow<List<CuentaEntity>> = flow { emit(emptyList()) }
    override val transacciones: Flow<List<TransaccionEntity>> = flow { emit(transaccionesList) }
    override val presupuestos: Flow<List<PresupuestoMensualEntity>> = flow { emit(presupuestosList) }

    override suspend fun saveCuenta(cuenta: CuentaEntity) {}
    override suspend fun deleteCuenta(cuenta: CuentaEntity) {}
    override suspend fun saveTransaccion(transaccion: TransaccionEntity) {}
    override suspend fun deleteTransaccion(transaccion: TransaccionEntity) {}
    override suspend fun savePresupuesto(presupuesto: PresupuestoMensualEntity) {}
}
