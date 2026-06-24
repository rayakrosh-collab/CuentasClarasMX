package com.example.cuentasclarasmx.ui.presupuestos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cuentasclarasmx.data.DataRepository
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresupuestoScreen(
    repository: DataRepository,
    modifier: Modifier = Modifier,
    viewModel: PresupuestosViewModel = viewModel { PresupuestosViewModel(repository) }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var editingSubcategoria by remember { mutableStateOf<SubcategoriaPresupuesto?>(null) }

    val formatInput = remember { SimpleDateFormat("yyyy-MM", Locale.US) }
    val formatOutput = remember { SimpleDateFormat("MMMM yyyy", Locale("es", "MX")) }
    val parsedDate = try { formatInput.parse(state.mesSeleccionado) } catch (e: Exception) { null }
    val monthDisplay = remember(state.mesSeleccionado) {
        parsedDate?.let { formatOutput.format(it).replaceFirstChar { c -> c.uppercase() } } ?: state.mesSeleccionado
    }

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Selector de Mes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.changeMonth(-1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Mes Anterior")
                }
                Text(
                    text = monthDisplay,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { viewModel.changeMonth(1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Mes Siguiente")
                }
            }

            // Indicador Disponible para Asignar
            DisponibleAsignarCard(
                disponible = state.disponibleParaAsignar,
                ingresos = state.ingresosDelMes,
                asignado = state.totalAsignado
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de Categorías y Subcategorías
            if (state.categoriasMadre.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.categoriasMadre, key = { it.entity.id }) { madrePres ->
                        CategoriaMadreRow(
                            madrePres = madrePres,
                            onSubcategoriaClick = { sub ->
                                editingSubcategoria = sub
                            }
                        )
                    }
                }
            }
        }
    }

    if (editingSubcategoria != null) {
        PresupuestoEditDialog(
            subcategoria = editingSubcategoria!!,
            onDismiss = { editingSubcategoria = null },
            onSave = { monto ->
                viewModel.savePresupuesto(editingSubcategoria!!.entity.id, monto)
                editingSubcategoria = null
            }
        )
    }
}

@Composable
fun DisponibleAsignarCard(
    disponible: Double,
    ingresos: Double,
    asignado: Double
) {
    val format = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }
    val disponibleColor = if (disponible >= 0) {
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
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Disponible para Asignar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = format.format(disponible),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = disponibleColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Ingresos del Mes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = format.format(ingresos),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Asignado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = format.format(asignado),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriaMadreRow(
    madrePres: CategoriaMadrePresupuesto,
    onSubcategoriaClick: (SubcategoriaPresupuesto) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    val format = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Fila de Categoría Madre
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = if (expanded) "Colapsar" else "Expandir",
                        modifier = Modifier.rotate(if (expanded) 0f else -90f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = madrePres.entity.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Asignado: ${format.format(madrePres.totalAsignado)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Disp: ${format.format(madrePres.totalDisponible)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (madrePres.totalDisponible >= 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                    )
                }
            }

            // Subcategorías Hijas
            if (expanded) {
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (madrePres.subcategorias.isEmpty()) {
                        Text(
                            text = "Sin subcategorías",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        madrePres.subcategorias.forEach { sub ->
                            SubcategoriaItemRow(
                                sub = sub,
                                onClick = { onSubcategoriaClick(sub) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubcategoriaItemRow(
    sub: SubcategoriaPresupuesto,
    onClick: () -> Unit
) {
    val format = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }
    val disponibleColor = if (sub.disponible >= 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = sub.entity.nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Gastado: ${format.format(sub.gastado)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón/Caja de Asignado
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.clickable(onClick = onClick)
            ) {
                Text(
                    text = format.format(sub.asignado),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            // Badge de Disponible
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = disponibleColor.copy(alpha = 0.12f),
                contentColor = disponibleColor
            ) {
                Text(
                    text = format.format(sub.disponible),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun PresupuestoEditDialog(
    subcategoria: SubcategoriaPresupuesto,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var montoStr by remember { mutableStateOf(subcategoria.asignado.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Asignar Presupuesto")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = subcategoria.entity.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ingresa el monto presupuestado para este sobre este mes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = montoStr,
                    onValueChange = { montoStr = it },
                    label = { Text("Monto Asignado") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val valDouble = montoStr.toDoubleOrNull() ?: 0.0
                    onSave(valDouble)
                },
                enabled = montoStr.toDoubleOrNull() != null
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
