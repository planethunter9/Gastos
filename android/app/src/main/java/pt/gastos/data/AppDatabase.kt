package pt.gastos.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Converters {
    @TypeConverter fun fromCategory(c: Category): String = c.name
    @TypeConverter fun toCategory(s: String): Category = Category.valueOf(s)
}

// v1 -> v2: a coluna "description" passou a chamar-se "label".
// RENAME COLUMN é suportado pelo SQLite do Android 13, e mantém todos os dados.
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE expenses RENAME COLUMN description TO label")
    }
}

@Database(entities = [Expense::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "gastos.db")
                    .addMigrations(MIGRATION_1_2)
                    .build().also { instance = it }
            }
    }
}
