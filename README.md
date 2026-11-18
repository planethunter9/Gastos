# Gastos

App de controlo de despesas pessoais, simples e sem anúncios, para saber em segundos quanto gastaste no mês e em quê.

Existe em duas versões com o mesmo design:

| Pasta | Plataforma | Tecnologias |
|---|---|---|
| raiz (`/`) | iOS | React Native, Expo, Expo Router |
| `android/` | Android | Kotlin, Jetpack Compose, Room |

## Funcionalidades

- **Início:** resumo do mês com o total gasto, a comparação com o mês anterior, o número de despesas, a média por dia e a maior despesa.
- **Categorias:** grelha com o total e o número de despesas de cada categoria.
- **Seletor de mês** partilhado pelos dois separadores.
- **Nova despesa:** valor, descrição (obrigatória, para nenhuma despesa ficar por identificar), categoria e data.
- **Categorias disponíveis:** Supermercado, Restaurantes, Transportes, Casa, Compras, Saúde/Cosmética, Lazer, Subscrições, Educação e Outros.
- **Menu flutuante em cápsula**, fácil de alcançar com uma mão.
- Os dados ficam **guardados no próprio telemóvel**, sem conta nem internet.

### Só na versão iOS
- Idioma: português ou inglês.
- Tema claro ou escuro.

## Como correr

### Versão iOS (Expo)
1. Instala as dependências:
   ```bash
   npm install
   ```
2. Inicia o projeto:
   ```bash
   npx expo start
   ```
3. Lê o código QR com a app **Expo Go** no iPhone.

### Versão Android
1. Abre a pasta `android/` no **Android Studio** (a que tem o `settings.gradle.kts`).
2. Espera que o *Gradle Sync* termine.
3. Carrega em **Run** com um telemóvel ligado ou um emulador.
