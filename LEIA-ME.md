# Gastos — iOS (Expo SDK 57, Expo Go)

## Criar o projeto do zero (PowerShell)
```
npx create-expo-app@latest gastos --template blank-typescript@sdk-57
cd gastos
npx expo install expo-router react-native-safe-area-context react-native-screens expo-linking expo-constants expo-status-bar expo-sqlite expo-glass-effect @react-native-async-storage/async-storage @react-native-community/datetimepicker @expo/vector-icons
```
Depois copia os ficheiros deste zip para a raiz do projeto (substitui `app.json`, `tsconfig.json`, `.npmrc`;
adiciona as pastas `app/` e `src/`) e apaga o `App.tsx` do template.

No `package.json`, muda a linha `"main"` para:
```
"main": "expo-router/entry",
```

Arrancar:
```
npx expo start -c
```
e lê o QR com o Expo Go (versão do SDK 57).

## O que é o quê
- `app/_layout.tsx` — Stack nativa (animação de voltar do iOS + gesto na margem esquerda)
- `app/(tabs)/` — Início e Categorias, com a cápsula em Liquid Glass (`src/components/GlassTabBar.tsx`)
- `app/definicoes.tsx` — engrenagem no canto superior direito: Idioma PT/EN, Aspeto Auto/Claro/Escuro
- `app/categoria/[id].tsx` — detalhe de categoria (ecrã empurrado)
- `app/nova.tsx` — folha modal "Nova despesa" (com campo label obrigatório)
- `src/store.tsx` — mês partilhado, despesas, idioma, tema e cores
- `src/i18n.ts`, `src/db.ts` (SQLite), `src/categories.ts`
