package pt.gastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.gastos.data.Expense
import pt.gastos.ui.AddExpenseSheet
import pt.gastos.ui.CategoriesScreen
import pt.gastos.ui.HomeScreen
import pt.gastos.ui.euros
import pt.gastos.ui.theme.GastosTheme
import java.time.LocalDate
import java.time.YearMonth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Barras do sistema transparentes com ícones escuros (modo claro fixo).
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
        )
        setContent { GastosTheme { App() } }
    }
}

private enum class Tab(val label: String, val icon: ImageVector, val iconActive: ImageVector) {
    HOME("Início", Icons.Outlined.Home, Icons.Rounded.Home),
    CATEGORIES("Categorias", Icons.Outlined.GridView, Icons.Rounded.GridView),
}

@Composable
private fun App(vm: ExpenseViewModel = viewModel()) {
    val month by vm.month.collectAsStateWithLifecycle()
    val expenses by vm.expenses.collectAsStateWithLifecycle()
    val previousTotal by vm.previousMonthTotal.collectAsStateWithLifecycle()

    var tab by remember { mutableStateOf(Tab.HOME) }
    var adding by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<Expense?>(null) }
    var editing by remember { mutableStateOf<Expense?>(null) }

    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // Espaço para o conteúdo não ficar escondido atrás da barra flutuante.
    val contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = topInset + 12.dp, bottom = bottomInset + 110.dp)

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (tab) {
            Tab.HOME -> HomeScreen(
                month = month, expenses = expenses, previousTotal = previousTotal,
                onPrevious = vm::previousMonth, onNext = vm::nextMonth, onToday = vm::goToCurrentMonth,
                onExpenseClick = { editing = it },
                onSeeCategories = { tab = Tab.CATEGORIES },
                contentPadding = contentPadding,
            )
            Tab.CATEGORIES -> CategoriesScreen(
                month = month, expenses = expenses,
                onPrevious = vm::previousMonth, onNext = vm::nextMonth, onToday = vm::goToCurrentMonth,
                onExpenseClick = { editing = it },
                contentPadding = contentPadding,
            )
        }

        // Botão + por cima da barra, à direita.
        FloatingActionButton(
            onClick = { adding = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = bottomInset + 92.dp),
        ) { Icon(Icons.Rounded.Add, contentDescription = "Nova despesa") }

        // Barra inferior flutuante em cápsula, como na app das fobias.
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 10.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 40.dp)
                .padding(bottom = bottomInset + 16.dp),
        ) {
            Row(Modifier.height(64.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Tab.entries.forEach { t ->
                    val active = t == tab
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { tab = t }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Icon(
                            if (active) t.iconActive else t.icon, contentDescription = t.label,
                            tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            t.label, style = MaterialTheme.typography.labelMedium,
                            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }

    if (adding) {
        val initial = if (month == YearMonth.now()) LocalDate.now() else month.atDay(1)
        AddExpenseSheet(
            initialDate = initial,
            onDismiss = { adding = false },
            onSave = { cents, label, cat, date ->
                vm.add(cents, label, cat, date)
                adding = false
                // Se a despesa for de outro mês, salta para lá para a veres logo.
                if (YearMonth.from(date) != month) {
                    while (YearMonth.from(date) < vm.month.value) vm.previousMonth()
                    while (YearMonth.from(date) > vm.month.value) vm.nextMonth()
                }
            },
        )
    }

    editing?.let { e ->
        AddExpenseSheet(
            initialDate = LocalDate.ofEpochDay(e.epochDay),
            existing = e,
            onDismiss = { editing = null },
            onSave = { cents, label, cat, date ->
                vm.update(e.id, cents, label, cat, date)
                editing = null
                // Se a data passou para outro mês, salta para lá para a veres logo.
                if (YearMonth.from(date) != month) {
                    while (YearMonth.from(date) < vm.month.value) vm.previousMonth()
                    while (YearMonth.from(date) > vm.month.value) vm.nextMonth()
                }
            },
            onDelete = {
                // fecha o editor e pede confirmação com o diálogo já existente
                editing = null
                toDelete = e
            },
        )
    }

    toDelete?.let { e ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text(e.label.ifBlank { e.category.label }) },
            text = { Text("${e.amountCents.euros()} · ${e.category.label}\n\nApagar esta despesa?") },
            confirmButton = { TextButton(onClick = { vm.delete(e); toDelete = null }) { Text("Apagar", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Cancelar") } },
        )
    }
}
