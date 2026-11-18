package pt.gastos.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountCents: Long,          // guardado em cêntimos para evitar erros de vírgula flutuante
    val label: String,            // em que foi gasto (obrigatório)
    val category: Category,
    val epochDay: Long,             // LocalDate.toEpochDay()
)
