package pt.gastos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.gastos.data.Category
import pt.gastos.data.Expense
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

// cêntimos -> texto editável: 1250 -> "12,50"
private fun centsToInput(cents: Long): String =
    String.format(Locale.US, "%.2f", cents / 100.0).replace('.', ',')

// Folha para registar OU editar uma despesa: valor, label (em que foi gasto), categoria e data.
// - Nova despesa: existing = null, a data começa em initialDate.
// - Editar: existing != null, os campos vêm preenchidos e aparece o botão "Apagar despesa".
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseSheet(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (amountCents: Long, label: String, category: Category, date: LocalDate) -> Unit,
    existing: Expense? = null,
    onDelete: (() -> Unit)? = null,
) {
    val editing = existing != null
    var amountText by remember { mutableStateOf(existing?.let { centsToInput(it.amountCents) } ?: "") }
    var label by remember { mutableStateOf(existing?.label ?: "") }
    var category by remember { mutableStateOf<Category?>(existing?.category) }
    var date by remember { mutableStateOf(existing?.let { LocalDate.ofEpochDay(it.epochDay) } ?: initialDate) }
    var showPicker by remember { mutableStateOf(false) }

    val cents = parseCents(amountText)
    // Ao editar permitimos guardar mesmo sem label (ex.: corrigir só a data de uma despesa antiga).
    val canSave = cents != null && category != null && (editing || label.isNotBlank())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(if (editing) "Editar despesa" else "Nova despesa", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() || c == ',' || c == '.' } },
                label = { Text("Valor") },
                suffix = { Text("€") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.headlineMedium,
                isError = amountText.isNotEmpty() && cents == null,
            )

            // Label: em que é que o dinheiro foi gasto. Obrigatório, para cada
            // despesa ter sempre um nome reconhecível nas listas.
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Em que gastaste?") },
                placeholder = { Text("Ex.: Continente, gasolina, Netflix, farmácia") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Categoria", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Category.entries.forEach { c ->
                    val selected = category == c
                    FilterChip(
                        selected = selected,
                        onClick = { category = c },
                        label = { Text(c.label) },
                        leadingIcon = { Icon(c.icon, null, tint = if (selected) MaterialTheme.colorScheme.onPrimary else c.color, modifier = Modifier.height(18.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = c.color,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { showPicker = true }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Rounded.CalendarMonth, null)
                    GapW(8)
                    Text(if (date == LocalDate.now()) "Hoje" else date.format(DateTimeFormatter.ofPattern("d MMM yyyy", PT)))
                }
                Button(onClick = { onSave(cents!!, label, category!!, date) }, enabled = canSave, modifier = Modifier.weight(1f)) {
                    Text("Guardar")
                }
            }

            // Só ao editar: apagar a despesa a partir do próprio editor.
            if (editing && onDelete != null) {
                TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Rounded.DeleteOutline, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.height(18.dp))
                    GapW(6)
                    Text("Apagar despesa", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { ms ->
                        date = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    showPicker = false
                }) { Text("Confirmar") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancelar") } },
        ) { DatePicker(state = state) }
    }
}
