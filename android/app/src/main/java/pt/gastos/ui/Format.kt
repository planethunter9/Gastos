package pt.gastos.ui

import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

val PT: Locale = Locale("pt", "PT")
private val currency: NumberFormat = NumberFormat.getCurrencyInstance(PT)

fun Long.euros(): String = currency.format(this / 100.0)

// "setembro 2026" -> "Setembro 2026"
fun YearMonth.label(): String =
    format(DateTimeFormatter.ofPattern("MMMM yyyy", PT)).replaceFirstChar { it.uppercase(PT) }

fun LocalDate.short(): String = format(DateTimeFormatter.ofPattern("d MMM", PT))

// "12,50" ou "12.50" -> 1250. null se inválido.
fun parseCents(text: String): Long? {
    val clean = text.trim().replace(',', '.')
    if (clean.isEmpty()) return null
    val value = clean.toDoubleOrNull() ?: return null
    if (value <= 0) return null
    return Math.round(value * 100)
}
