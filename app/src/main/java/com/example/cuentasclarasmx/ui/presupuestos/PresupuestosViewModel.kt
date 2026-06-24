package com.example.cuentasclarasmx.ui.presupuestos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuentasclarasmx.data.DataRepository
import com.example.cuentasclarasmx.data.local.entity.CategoriaEntity
import com.example.cuentasclarasmx.data.local.entity.PresupuestoMensualEntity
import com.example.cuentasclarasmx.data.local.entity.TransaccionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class SubcategoriaPresupuesto(
    val entity: CategoriaEntity,
    val asignado: Double,
    val gastado: Double,
    val disponible: Double,
    val presupuestoId: Long = 0L
)

data class CategoriaMadrePresupuesto(
    val entity: CategoriaEntity,
    val subcategorias: List<SubcategoriaPresupuesto>,
    val totalAsignado: Double,
    val totalGastado: Double,
    val totalDisponible: Double
)

data class PresupuestosUiState(
    val categoriasMadre: List<CategoriaMadrePresupuesto> = emptyList(),
    val disponibleParaAsignar: Double = 0.0,
    val ingresosDelMes: Double = 0.0,
    val totalAsignado: Double = 0.0,
    val mesSeleccionado: String = "" // Formato YYYY-MM
)

class PresupuestosViewModel(private val repository: DataRepository) : ViewModel() {

    private val _currentMonth = MutableStateFlow(getCurrentMonthString())
    val currentMonth: StateFlow<String> = _currentMonth

    private val sdf = SimpleDateFormat("yyyy-MM", Locale.US)

    val uiState: StateFlow<PresupuestosUiState> = combine(
        repository.categorias,
        repository.presupuestos,
        repository.transacciones,
        _currentMonth
    ) { categorias, presupuestos, transacciones, month ->
        
        // Filtrar transacciones del mes
        val transaccionesDelMes = transacciones.filter { t ->
            sdf.format(Date(t.fecha)) == month
        }

        // Ingresos del mes
        val ingresosDelMes = transaccionesDelMes.filter { it.tipo == "ingreso" }.sumOf { it.monto }

        // Presupuestos del mes
        val presupuestosDelMes = presupuestos.filter { it.mes == month }.associateBy { it.subcategoriaId }

        // Categorías madre y sus subcategorías
        val madres = categorias.filter { it.categoriaPadreId == null }
        val subcategorias = categorias.filter { it.categoriaPadreId != null }

        val madrePresupuestos = madres.map { madre ->
            val hijas = subcategorias.filter { it.categoriaPadreId == madre.id }
            val hijasPresupuestos = hijas.map { hija ->
                val pres = presupuestosDelMes[hija.id]
                val asignado = pres?.montoAsignado ?: 0.0
                // Gastado por esta subcategoría en este mes
                val gastado = transaccionesDelMes
                    .filter { it.tipo == "gasto" && it.subcategoriaId == hija.id }
                    .sumOf { it.monto }
                val disponible = asignado - gastado // Arrastre será fase 7
                SubcategoriaPresupuesto(
                    entity = hija,
                    asignado = asignado,
                    gastado = gastado,
                    disponible = disponible,
                    presupuestoId = pres?.id ?: 0L
                )
            }

            val totalAsignado = hijasPresupuestos.sumOf { it.asignado }
            val totalGastado = hijasPresupuestos.sumOf { it.gastado }
            val totalDisponible = hijasPresupuestos.sumOf { it.disponible }

            CategoriaMadrePresupuesto(
                entity = madre,
                subcategorias = hijasPresupuestos,
                totalAsignado = totalAsignado,
                totalGastado = totalGastado,
                totalDisponible = totalDisponible
            )
        }

        val totalAsignadoMes = madrePresupuestos.sumOf { it.totalAsignado }
        val disponibleParaAsignar = ingresosDelMes - totalAsignadoMes

        PresupuestosUiState(
            categoriasMadre = madrePresupuestos,
            disponibleParaAsignar = disponibleParaAsignar,
            ingresosDelMes = ingresosDelMes,
            totalAsignado = totalAsignadoMes,
            mesSeleccionado = month
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PresupuestosUiState(mesSeleccionado = getCurrentMonthString())
    )

    fun changeMonth(offset: Int) {
        val cal = Calendar.getInstance()
        val currentParts = _currentMonth.value.split("-")
        if (currentParts.size == 2) {
            cal.set(Calendar.YEAR, currentParts[0].toInt())
            cal.set(Calendar.MONTH, currentParts[1].toInt() - 1)
            cal.add(Calendar.MONTH, offset)
            _currentMonth.value = sdf.format(cal.time)
        }
    }

    fun savePresupuesto(subcategoriaId: Long, monto: Double) {
        viewModelScope.launch {
            // Buscar si existe un presupuesto para esta subcategoría y mes
            val month = _currentMonth.value
            val state = uiState.value
            val existingSub = state.categoriasMadre
                .flatMap { it.subcategorias }
                .firstOrNull { it.entity.id == subcategoriaId }
            
            val entity = PresupuestoMensualEntity(
                id = existingSub?.presupuestoId ?: 0L,
                subcategoriaId = subcategoriaId,
                mes = month,
                montoAsignado = monto,
                arrastre = 0.0 // Arrastre será fase 7
            )
            repository.savePresupuesto(entity)
        }
    }

    private fun getCurrentMonthString(): String {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.US)
        return sdf.format(Date())
    }
}
