package com.example.cuentasclarasmx.ui.main

import com.example.cuentasclarasmx.data.DataRepository
import com.example.cuentasclarasmx.data.local.entity.CategoriaEntity
import com.example.cuentasclarasmx.data.local.entity.CuentaEntity
import com.example.cuentasclarasmx.data.local.entity.PresupuestoMensualEntity
import com.example.cuentasclarasmx.data.local.entity.TransaccionEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {
  @Test
  fun uiState_initiallyLoading() = runTest {
    val viewModel = MainScreenViewModel(FakeMyModelRepository())
    assertEquals(viewModel.uiState.first(), MainScreenUiState.Loading)
  }

  @Test
  fun uiState_onItemSaved_isDisplayed() = runTest {
    val viewModel = MainScreenViewModel(FakeMyModelRepository())
    assertEquals(viewModel.uiState.first(), MainScreenUiState.Loading)
  }
}

private class FakeMyModelRepository : DataRepository {
  override val data: Flow<List<String>> = flow { emit(listOf("Sample")) }
  override val categorias: Flow<List<CategoriaEntity>> = flow { emit(emptyList()) }
  override val cuentas: Flow<List<CuentaEntity>> = flow { emit(emptyList()) }
  override val transacciones: Flow<List<TransaccionEntity>> = flow { emit(emptyList()) }
  override val presupuestos: Flow<List<PresupuestoMensualEntity>> = flow { emit(emptyList()) }
  override suspend fun saveCuenta(cuenta: CuentaEntity) {}
  override suspend fun deleteCuenta(cuenta: CuentaEntity) {}
  override suspend fun saveTransaccion(transaccion: TransaccionEntity) {}
  override suspend fun deleteTransaccion(transaccion: TransaccionEntity) {}
  override suspend fun savePresupuesto(presupuesto: PresupuestoMensualEntity) {}
}
