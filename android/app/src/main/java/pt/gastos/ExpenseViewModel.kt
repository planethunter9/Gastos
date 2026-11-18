package pt.gastos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.gastos.data.AppDatabase
import pt.gastos.data.Category
import pt.gastos.data.Expense
import java.time.LocalDate
import java.time.YearMonth

// O mês selecionado vive aqui, partilhado pelas duas páginas:
// mudar o mês no Início muda-o também nas Categorias, e vice-versa.
@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).expenseDao()

    private val _month = MutableStateFlow(YearMonth.now())
    val month: StateFlow<YearMonth> = _month.asStateFlow()

    val expenses: StateFlow<List<Expense>> = _month
        .flatMapLatest { m -> dao.between(m.atDay(1).toEpochDay(), m.atEndOfMonth().toEpochDay()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val previousMonthTotal: StateFlow<Long> = _month
        .flatMapLatest { m ->
            val p = m.minusMonths(1)
            dao.totalBetween(p.atDay(1).toEpochDay(), p.atEndOfMonth().toEpochDay())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    fun previousMonth() { _month.value = _month.value.minusMonths(1) }
    fun nextMonth() { _month.value = _month.value.plusMonths(1) }
    fun goToCurrentMonth() { _month.value = YearMonth.now() }

    fun add(amountCents: Long, label: String, category: Category, date: LocalDate) {
        viewModelScope.launch {
            dao.insert(Expense(amountCents = amountCents, label = label.trim(), category = category, epochDay = date.toEpochDay()))
        }
    }

    fun update(id: Long, amountCents: Long, label: String, category: Category, date: LocalDate) {
        viewModelScope.launch {
            dao.update(id, amountCents, label.trim(), category, date.toEpochDay())
        }
    }

    fun delete(expense: Expense) {
        viewModelScope.launch { dao.delete(expense) }
    }
}
