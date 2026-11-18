package pt.gastos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import pt.gastos.data.Category
import pt.gastos.data.Expense
import java.time.YearMonth

// Página das categorias. O seletor de mês fica no topo, por cima da grelha,
// e é o mesmo estado que o Início — por isso nunca há dúvida sobre "que mês estou a ver".
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    month: YearMonth,
    expenses: List<Expense>,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    onExpenseClick: (Expense) -> Unit,
    contentPadding: PaddingValues,
) {
    val total = expenses.sumOf { it.amountCents }
    val totals = expenses.groupBy { it.category }.mapValues { (_, l) -> l.sumOf { it.amountCents } }

    // Categorias com gastos primeiro (por valor), depois as vazias por ordem fixa.
    val ordered = Category.entries.sortedWith(
        compareByDescending<Category> { totals[it] ?: 0L }.thenBy { it.ordinal },
    )

    var open by remember { mutableStateOf<Category?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(span = { GridItemSpan(2) }) {
            Text("Categorias", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(horizontal = 4.dp))
        }
        item(span = { GridItemSpan(2) }) { MonthSelector(month, onPrevious, onNext, onToday) }
        item(span = { GridItemSpan(2) }) {
            Text(
                if (total == 0L) "Sem despesas em ${month.label()}." else "Total do mês: ${total.euros()}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }
        items(ordered, key = { it.name }) { cat ->
            val amount = totals[cat] ?: 0L
            CategoryCard(
                category = cat,
                amount = amount,
                fraction = if (total == 0L) 0f else amount.toFloat() / total,
                count = expenses.count { it.category == cat },
                onClick = { open = cat },
            )
        }
    }

    open?.let { cat ->
        val list = expenses.filter { it.category == cat }
        ModalBottomSheet(
            onDismissRequest = { open = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(Modifier.padding(horizontal = 16.dp).padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryIcon(cat, size = 44)
                    Column(Modifier.weight(1f)) {
                        Text(cat.label, style = MaterialTheme.typography.titleLarge)
                        Text(month.label(), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text((totals[cat] ?: 0L).euros(), style = MaterialTheme.typography.titleLarge)
                }
                Gap(4)
                if (list.isEmpty()) {
                    Text("Nada registado nesta categoria este mês.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    list.forEach { e -> ExpenseRow(e, onClick = { open = null; onExpenseClick(e) }, showCategory = false) }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(category: Category, amount: Long, fraction: Float, count: Int, onClick: () -> Unit) {
    val empty = amount == 0L
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CategoryIcon(category, size = 40)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(category.label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    amount.euros(),
                    style = MaterialTheme.typography.titleLarge,
                    color = if (empty) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    when (count) { 0 -> "—"; 1 -> "1 despesa"; else -> "$count despesas" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            ProportionBar(fraction, category.color)
        }
    }
}
