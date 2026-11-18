package pt.gastos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pt.gastos.data.Category
import pt.gastos.data.Expense
import java.time.LocalDate
import java.time.YearMonth

// Seletor de mês: setas de um lado e do outro, nome do mês ao centro.
// Se não estivermos no mês atual aparece um atalho "Hoje".
@Composable
fun MonthSelector(
    month: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToday: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isCurrent = month == YearMonth.now()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 4.dp)) {
            IconButton(onClick = onPrevious) { Icon(Icons.Rounded.ChevronLeft, contentDescription = "Mês anterior") }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(month.label(), style = MaterialTheme.typography.titleMedium)
                if (!isCurrent) {
                    TextButton(onClick = onToday, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp), modifier = Modifier.height(24.dp)) {
                        Text("Voltar ao mês atual", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            IconButton(onClick = onNext) { Icon(Icons.Rounded.ChevronRight, contentDescription = "Mês seguinte") }
        }
    }
}

@Composable
fun CategoryIcon(category: Category, size: Int = 40) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(category.color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(category.icon, contentDescription = category.label, tint = category.color, modifier = Modifier.size((size * 0.55f).dp))
    }
}

// Linha de uma despesa. Usada no Início e no detalhe de categoria.
@Composable
fun ExpenseRow(expense: Expense, onClick: () -> Unit, showCategory: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CategoryIcon(expense.category, size = 40)
        Column(Modifier.weight(1f)) {
            Text(
                expense.label.ifBlank { expense.category.label },
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val date = LocalDate.ofEpochDay(expense.epochDay).short()
            Text(
                if (showCategory) "${expense.category.label} · $date" else date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(expense.amountCents.euros(), style = MaterialTheme.typography.titleMedium)
    }
}

// Barra de proporção simples (sem animação, para ser honesta e rápida).
@Composable
fun ProportionBar(fraction: Float, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(8.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(color),
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Spacer(Modifier.height(4.dp))
    Text(text, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 4.dp))
}

@Composable
fun Gap(h: Int) = Spacer(Modifier.height(h.dp))

@Composable
fun GapW(w: Int) = Spacer(Modifier.width(w.dp))
