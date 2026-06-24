package com.example.cuentasclarasmx.ui.transacciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cuentasclarasmx.data.DataRepository
import com.example.cuentasclarasmx.data.local.entity.CuentaEntity
import com.example.cuentasclarasmx.data.local.entity.TransaccionEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransaccionesScreen(
    repository: DataRepository,
    modifier: Modifier = Modifier,
    viewModel: TransaccionesViewModel = viewModel { TransaccionesViewModel(repository) }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var editingTransaccion by remember { mutableStateOf<TransaccionEntity?>(null) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingTransaccion = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar Movimiento")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Buscar movimientos") },
                placeholder = { Text("ej. Súper, Renta, Nómina") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Lista de transacciones (Ledger)
            if (state.transacciones.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.searchQuery.isBlank()) {
                            "No tienes movimientos registrados aún.\n¡Crea uno con el botón +!"
                        } else {
                            "No se encontraron movimientos para \"${state.searchQuery}\"."
                        },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.transacciones, key = { it.entity.id }) { item ->
                        TransaccionItem(
                            item = item,
                            onClick = {
                                editingTransaccion = item.entity
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        TransaccionFormDialog(
            transaccion = editingTransaccion,
            cuentas = state.cuentas,
            subcategorias = state.subcategorias,
            onDismiss = { showDialog = false },
            onSave = { t ->
                viewModel.saveTransaccion(t)
                showDialog = false
            },
            onDelete = { t ->
                viewModel.deleteTransaccion(t)
                showDialog = false
            }
        )
    }
}

@Composable
fun TransaccionItem(
    item: TransaccionConDetalles,
    onClick: () -> Unit
) {
    val t = item.entity
    val formatMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }
    val formatFecha = remember { SimpleDateFormat("dd 'de' MMMM, yyyy", Locale("es", "MX")) }
    val fechaStr = remember(t.fecha) { formatFecha.format(Date(t.fecha)) }

    val isIngreso = t.tipo == "ingreso"
    val montoColor = if (isIngreso) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
    val montoPrefix = if (isIngreso) "+" else "-"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = t.descripcion.ifBlank { if (isIngreso) "Ingreso" else "Gasto" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(item.cuentaNombre, fontSize = 11.sp) },
                        enabled = false,
                        modifier = Modifier.height(24.dp)
                    )
                    item.subcategoriaNombre?.let { subName ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(subName, fontSize = 11.sp) },
                            enabled = false,
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = fechaStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Text(
                text = "$montoPrefix ${formatMoneda.format(t.monto).replace("$", "").trim()}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = montoColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransaccionFormDialog(
    transaccion: TransaccionEntity?,
    cuentas: List<CuentaEntity>,
    subcategorias: List<SubcategoriaItem>,
    onDismiss: () -> Unit,
    onSave: (TransaccionEntity) -> Unit,
    onDelete: (TransaccionEntity) -> Unit
) {
    var tipo by remember { mutableStateOf(transaccion?.tipo ?: "gasto") }
    var montoStr by remember { mutableStateOf(transaccion?.monto?.toString() ?: "") }
    var descripcion by remember { mutableStateOf(transaccion?.descripcion ?: "") }
    var cuentaSeleccionada by remember {
        mutableStateOf(
            cuentas.firstOrNull { it.id == transaccion?.cuentaId } ?: cuentas.firstOrNull()
        )
    }
    var subcategoriaSeleccionada by remember {
        mutableStateOf(
            subcategorias.firstOrNull { it.entity.id == transaccion?.subcategoriaId }?.entity
        )
    }
    var fechaMillis by remember { mutableStateOf(transaccion?.fecha ?: System.currentTimeMillis()) }

    var accountDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val formatFecha = remember { SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX")) }
    val isEdit = transaccion != null

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = fechaMillis)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (isEdit) "Editar Movimiento" else "Nuevo Movimiento")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selector de Tipo: Gasto / Ingreso (Segmented Control manual con Row)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { tipo = "gasto" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tipo == "gasto") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (tipo == "gasto") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Gasto")
                    }
                    Button(
                        onClick = { tipo = "ingreso" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (tipo == "ingreso") Color(0xFF2E7D32) else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (tipo == "ingreso") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ingreso")
                    }
                }

                // Monto
                OutlinedTextField(
                    value = montoStr,
                    onValueChange = { montoStr = it },
                    label = { Text("Monto") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    placeholder = { Text("ej. Supermercado, Pago quincena") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Cuenta Afectada
                if (cuentas.isEmpty()) {
                    Text(
                        text = "Primero debes crear una cuenta en la pestaña Patrimonio.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    ExposedDropdownMenuBox(
                        expanded = accountDropdownExpanded,
                        onExpandedChange = { accountDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = cuentaSeleccionada?.nombre ?: "Seleccionar Cuenta",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Cuenta") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = accountDropdownExpanded,
                            onDismissRequest = { accountDropdownExpanded = false }
                        ) {
                            cuentas.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c.nombre) },
                                    onClick = {
                                        cuentaSeleccionada = c
                                        accountDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Subcategoría (Opcional, pero recomendada)
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = subcategoriaSeleccionada?.nombre ?: "Seleccionar Categoría (Opcional)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        var lastHeader = ""
                        subcategorias.forEach { item ->
                            if (item.padreNombre != lastHeader) {
                                lastHeader = item.padreNombre
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = lastHeader,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 12.sp
                                        )
                                    },
                                    onClick = {},
                                    enabled = false
                                )
                            }
                            DropdownMenuItem(
                                text = { Text(text = "   ${item.entity.nombre}") },
                                onClick = {
                                    subcategoriaSeleccionada = item.entity
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Selector de Fecha
                OutlinedTextField(
                    value = formatFecha.format(Date(fechaMillis)),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montoVal = montoStr.toDoubleOrNull() ?: 0.0
                    val cuentaId = cuentaSeleccionada?.id ?: 0L

                    if (montoVal > 0 && cuentaId != 0L) {
                        val finalTransaccion = TransaccionEntity(
                            id = transaccion?.id ?: 0,
                            tipo = tipo,
                            fecha = fechaMillis,
                            monto = montoVal,
                            descripcion = descripcion,
                            cuentaId = cuentaId,
                            subcategoriaId = subcategoriaSeleccionada?.id,
                            cuentaDestinoId = transaccion?.cuentaDestinoId,
                            fotoReciboUri = transaccion?.fotoReciboUri,
                            compraMsiId = transaccion?.compraMsiId,
                            recurrenteId = transaccion?.recurrenteId
                        )
                        onSave(finalTransaccion)
                    }
                },
                enabled = montoStr.toDoubleOrNull() != null && (montoStr.toDoubleOrNull() ?: 0.0) > 0 && cuentaSeleccionada != null
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Row {
                if (isEdit && transaccion != null) {
                    IconButton(
                        onClick = { onDelete(transaccion) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Movimiento")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        fechaMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
