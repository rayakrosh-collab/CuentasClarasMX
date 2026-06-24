package com.example.cuentasclarasmx.ui.cuentas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentasScreen(
    repository: DataRepository,
    modifier: Modifier = Modifier,
    viewModel: CuentasViewModel = viewModel { CuentasViewModel(repository) }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var editingCuenta by remember { mutableStateOf<CuentaEntity?>(null) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingCuenta = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Cuenta")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Patrimonio Neto y Resumen
            PatrimonioNetoCard(
                patrimonio = state.patrimonioNeto,
                activos = state.totalActivos,
                pasivos = state.totalPasivos
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de Cuentas
            if (state.cuentas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes cuentas registradas aún.\n¡Crea una con el botón +!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val activos = state.cuentas.filter { !it.entity.esPasivo }
                val pasivos = state.cuentas.filter { it.entity.esPasivo }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (activos.isNotEmpty()) {
                        item {
                            Text(
                                text = "ACTIVOS (Tus recursos)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(activos, key = { it.entity.id }) { cuentaConSaldo ->
                            CuentaItem(
                                cuentaConSaldo = cuentaConSaldo,
                                onClick = {
                                    editingCuenta = cuentaConSaldo.entity
                                    showDialog = true
                                }
                            )
                        }
                    }

                    if (pasivos.isNotEmpty()) {
                        item {
                            Text(
                                text = "PASIVOS (Tus deudas)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        items(pasivos, key = { it.entity.id }) { cuentaConSaldo ->
                            CuentaItem(
                                cuentaConSaldo = cuentaConSaldo,
                                onClick = {
                                    editingCuenta = cuentaConSaldo.entity
                                    showDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        CuentaFormDialog(
            cuenta = editingCuenta,
            onDismiss = { showDialog = false },
            onSave = { cuenta ->
                viewModel.saveCuenta(cuenta)
                showDialog = false
            },
            onDelete = { cuenta ->
                viewModel.deleteCuenta(cuenta)
                showDialog = false
            }
        )
    }
}

@Composable
fun PatrimonioNetoCard(
    patrimonio: Double,
    activos: Double,
    pasivos: Double
) {
    val format = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }
    val patrimonioColor = if (patrimonio >= 0) {
        Color(0xFF2E7D32) // Verde oscuro
    } else {
        MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Patrimonio Neto",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = format.format(patrimonio),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = patrimonioColor
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Activos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = format.format(activos),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Pasivos / Deudas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = format.format(pasivos),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun CuentaItem(
    cuentaConSaldo: CuentaConSaldo,
    onClick: () -> Unit
) {
    val cuenta = cuentaConSaldo.entity
    val format = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }
    val balanceColor = if (cuenta.esPasivo) {
        MaterialTheme.colorScheme.error
    } else {
        Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = cuenta.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = cuenta.tipo.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (cuenta.esPasivo && cuenta.limiteCredito != null) {
                    Text(
                        text = "Límite: ${format.format(cuenta.limiteCredito)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
            Text(
                text = format.format(cuentaConSaldo.saldoActual),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = balanceColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentaFormDialog(
    cuenta: CuentaEntity?,
    onDismiss: () -> Unit,
    onSave: (CuentaEntity) -> Unit,
    onDelete: (CuentaEntity) -> Unit
) {
    var nombre by remember { mutableStateOf(cuenta?.nombre ?: "") }
    var tipo by remember { mutableStateOf(cuenta?.tipo ?: "efectivo") }
    var saldoInicialStr by remember { mutableStateOf(cuenta?.saldoInicial?.toString() ?: "") }
    var limiteCreditoStr by remember { mutableStateOf(cuenta?.limiteCredito?.toString() ?: "") }

    val tiposCuentas = listOf("efectivo", "débito", "banco", "crédito", "inversión", "préstamo", "otro")
    var dropdownExpanded by remember { mutableStateOf(false) }

    val isEdit = cuenta != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (isEdit) "Editar Cuenta" else "Nueva Cuenta")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la cuenta") },
                    placeholder = { Text("ej. BBVA Nómina, Efectivo Cartera") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = tipo.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Cuenta") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        tiposCuentas.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    tipo = item
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                val labelSaldo = if (tipo == "crédito" || tipo == "préstamo") "Deuda inicial" else "Saldo inicial"
                val placeholderSaldo = if (tipo == "crédito" || tipo == "préstamo") "Cuánto debes hoy" else "Cuánto tienes hoy"

                OutlinedTextField(
                    value = saldoInicialStr,
                    onValueChange = { saldoInicialStr = it },
                    label = { Text(labelSaldo) },
                    placeholder = { Text(placeholderSaldo) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (tipo == "crédito") {
                    OutlinedTextField(
                        value = limiteCreditoStr,
                        onValueChange = { limiteCreditoStr = it },
                        label = { Text("Límite de crédito (opcional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val saldoVal = saldoInicialStr.toDoubleOrNull() ?: 0.0
                    val limiteVal = limiteCreditoStr.toDoubleOrNull()
                    val esPasivo = (tipo == "crédito" || tipo == "préstamo")

                    if (nombre.isNotBlank()) {
                        val finalCuenta = CuentaEntity(
                            id = cuenta?.id ?: 0,
                            nombre = nombre,
                            tipo = tipo,
                            esPasivo = esPasivo,
                            saldoInicial = saldoVal,
                            limiteCredito = if (tipo == "crédito") limiteVal else null,
                            archivada = cuenta?.archivada ?: false
                        )
                        onSave(finalCuenta)
                    }
                },
                enabled = nombre.isNotBlank() && saldoInicialStr.toDoubleOrNull() != null
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Row {
                if (isEdit && cuenta != null) {
                    IconButton(
                        onClick = { onDelete(cuenta) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Cuenta")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        }
    )
}
