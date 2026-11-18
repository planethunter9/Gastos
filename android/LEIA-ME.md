# Gastos — app Android (Kotlin + Jetpack Compose, Android 13+)

## Como abrir
1. Android Studio → New Project → **Empty Activity** (Compose)
   - Name: Gastos · Package: `pt.gastos` · Minimum SDK: API 33 · Language: Kotlin
2. Fecha o projeto e, na pasta criada, substitui pelos ficheiros deste zip:
   - `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`
   - `app/build.gradle.kts`
   - toda a pasta `app/src/main` (apaga a antiga primeiro)
3. Abre o projeto outra vez → Sync Now → Run no telemóvel.

## Estrutura
- `data/` — Room (Expense, DAO, base de dados) e o enum das categorias
- `ExpenseViewModel.kt` — mês selecionado (partilhado pelas duas páginas) + despesas desse mês
- `ui/HomeScreen.kt` — dashboard mensal
- `ui/CategoriesScreen.kt` — grelha de categorias com o seletor de mês no topo
- `ui/AddExpenseSheet.kt` — folha "Nova despesa"
- `MainActivity.kt` — barra inferior flutuante, FAB, diálogos
