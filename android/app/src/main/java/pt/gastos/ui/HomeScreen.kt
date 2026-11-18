package pt.gastos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.gastos.data.Expense
import pt.gastos.ui.theme.Green
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun HomeScreen(
    month: YearMonth,
    expenses: List<Expense>,
    previousTotal: Long,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    onExpenseClick: (Expense) -> Unit,
    onSeeCategories: () -> Unit,
    contentPadding: PaddingValues,
) {
    val total = expenses.sumOf { it.amountCents }
    val byCategory = expenses.groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.amountCents } }
        .entries.sortedByDescending { it.value }
    val topCategories = byCategory.take(3)

    // Média por dia: até hoje se for o mês atual, senão o mês inteiro.
    val daysElapsed = if (month == YearMonth.now()) LocalDate.now().dayOfMonth else month.lengthOfMonth()
    val perDay = if (expenses.isEmpty()) 0L else total / daysElapsed
    val biggest = expenses.maxByOrNull { it.amountCents }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Início", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(horizontal = 4.dp))
        }
        item { MonthSelector(month, onPrevious, onNext, onToday) }

        // Cartão principal: total do mês e comparação com o mês anterior.
        item {
            Surface(shape = RoundedCornerShape(20.dp), color = Green, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Gasto em ${month.label().substringBefore(' ')}", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodyMedium)
                    Text(total.euros(), color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.displayLarge)
                    ComparisonLine(total, previousTotal)
                }
            }
        }

        // Três números rápidos.
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Despesas", expenses.size.toString(), Modifier.weight(1f))
                StatCard("Por dia", perDay.euros(), Modifier.weight(1f))
                StatCard("Maior", biggest?.amountCents?.euros() ?: "—", Modifier.weight(1f))
            }
        }

        if (expenses.isEmpty()) {
            item {
                Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Ainda sem despesas neste mês", style = MaterialTheme.typography.titleMedium)
                        Text("Toca no + para registar a primeira.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            item { SectionTitle("Onde foi o dinheiro") }
            item {
                Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        topCategories.forEach { (cat, amount) ->
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    CategoryIcon(cat, size = 32)
                                    Text(cat.label, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                                    Text(amount.euros(), style = MaterialTheme.typography.titleMedium)
                                }
                                ProportionBar(if (total == 0L) 0f else amount.toFloat() / total, cat.color)
                            }
                        }
                        if (byCategory.size > 3) {
                            androidx.compose.material3.TextButton(onClick = onSeeCategories, modifier = Modifier.align(Alignment.End)) {
                                Text("Ver todas as categorias")
                            }
                        }
                    }
                }
            }

            item { SectionTitle("Últimas despesas") }
            item {
                Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(6.dp)) {
                        expenses.take(6).forEach { e -> ExpenseRow(e, onClick = { onExpenseClick(e) }) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonLine(total: Long, previous: Long) {
    val white = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f)
    when {
        previous == 0L && total == 0L -> Text("Sem dados para comparar", color = white, style = MaterialTheme.typography.bodyMedium)
        previous == 0L -> Text("Sem despesas no mês anterior", color = white, style = MaterialTheme.typography.bodyMedium)
        else -> {
            val diff = total - previous
            val pct = (diff * 100.0 / previous).let { Math.round(it) }
            val up = diff > 0
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(if (up) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward, null, tint = white, modifier = Modifier.padding(0.dp))
                Text(
                    "${if (up) "+" else ""}$pct% face ao mês anterior (${previous.euros()})",
                    color = white,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, modifier = modifier) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, maxLines = 1)
        }
    }
}
