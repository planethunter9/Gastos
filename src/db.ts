import * as SQLite from 'expo-sqlite';
import type { CategoryId } from './categories';

export type Expense = {
  id: number;
  amountCents: number;   // em cêntimos, como na versão Android
  label: string;         // em que foi gasto (obrigatório)
  category: CategoryId;
  date: string;          // 'YYYY-MM-DD'
};

const db = SQLite.openDatabaseSync('gastos.db');

db.execSync(`
  CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    amountCents INTEGER NOT NULL,
    label TEXT NOT NULL,
    category TEXT NOT NULL,
    date TEXT NOT NULL
  );
  CREATE INDEX IF NOT EXISTS idx_expenses_date ON expenses(date);
`);

export function listByMonth(month: string): Expense[] {
  return db.getAllSync<Expense>('SELECT * FROM expenses WHERE date LIKE ? ORDER BY date DESC, id DESC', [`${month}-%`]);
}

export function totalByMonth(month: string): number {
  const row = db.getFirstSync<{ total: number }>('SELECT COALESCE(SUM(amountCents), 0) AS total FROM expenses WHERE date LIKE ?', [`${month}-%`]);
  return row?.total ?? 0;
}

export function insertExpense(e: Omit<Expense, 'id'>) {
  db.runSync('INSERT INTO expenses (amountCents, label, category, date) VALUES (?, ?, ?, ?)', [e.amountCents, e.label, e.category, e.date]);
}

export function deleteExpense(id: number) {
  db.runSync('DELETE FROM expenses WHERE id = ?', [id]);
}
