package pt.gastos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE epochDay BETWEEN :from AND :to ORDER BY epochDay DESC, id DESC")
    fun between(from: Long, to: Long): Flow<List<Expense>>

    @Query("SELECT COALESCE(SUM(amountCents), 0) FROM expenses WHERE epochDay BETWEEN :from AND :to")
    fun totalBetween(from: Long, to: Long): Flow<Long>

    @Insert
    suspend fun insert(expense: Expense)

    @Query("UPDATE expenses SET amountCents = :amountCents, label = :label, category = :category, epochDay = :epochDay WHERE id = :id")
    suspend fun update(id: Long, amountCents: Long, label: String, category: Category, epochDay: Long)

    @Delete
    suspend fun delete(expense: Expense)
}
