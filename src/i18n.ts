export type Lang = 'pt' | 'en';

const pt = {
  tabs: { home: 'Início', categories: 'Categorias' },
  common: { back: 'Voltar' },
  home: {
    spentIn: 'Gasto em {month}',
    noPrev: 'Sem despesas no mês anterior',
    noCompare: 'Sem dados para comparar',
    vsPrev: '{pct}% face ao mês anterior ({amount})',
    expenses: 'Despesas', perDay: 'Por dia', biggest: 'Maior',
    empty: 'Ainda sem despesas neste mês', emptyHint: 'Toca no + para registar a primeira.',
    whereMoney: 'Onde foi o dinheiro', seeAll: 'Ver todas as categorias', latest: 'Últimas despesas',
  },
  month: { backToCurrent: 'Voltar ao mês atual', total: 'Total do mês: {amount}', none: 'Sem despesas em {month}.' },
  cat: {
    ALIMENTACAO: 'Supermercado', RESTAURANTES: 'Restaurantes', TRANSPORTES: 'Transportes', CASA: 'Casa',
    COMPRAS: 'Compras', SAUDE: 'Saúde/Cosmética', LAZER: 'Lazer', SUBSCRICOES: 'Subscrições',
    EDUCACAO: 'Educação', OUTROS: 'Outros',
    count0: '—', count1: '1 despesa', countN: '{n} despesas', emptyDetail: 'Nada registado nesta categoria este mês.',
  },
  add: {
    title: 'Nova despesa', amount: 'Valor', label: 'Em que gastaste?', labelPlaceholder: 'Ex.: Continente, gasolina, Netflix, farmácia',
    category: 'Categoria', date: 'Data', today: 'Hoje', save: 'Guardar', cancel: 'Cancelar',
  },
  delete: { question: 'Apagar esta despesa?', confirm: 'Apagar', cancel: 'Cancelar' },
  settings: {
    title: 'Definições', language: 'Idioma', appearance: 'Aspeto',
    system: 'Automático', light: 'Claro', dark: 'Escuro', pt: 'Português (PT)', en: 'English',
    about: 'Os dados ficam só neste iPhone.',
  },
};

const en: typeof pt = {
  tabs: { home: 'Home', categories: 'Categories' },
  common: { back: 'Back' },
  home: {
    spentIn: 'Spent in {month}',
    noPrev: 'No expenses last month',
    noCompare: 'Nothing to compare yet',
    vsPrev: '{pct}% vs. last month ({amount})',
    expenses: 'Expenses', perDay: 'Per day', biggest: 'Largest',
    empty: 'No expenses this month yet', emptyHint: 'Tap + to add the first one.',
    whereMoney: 'Where the money went', seeAll: 'See all categories', latest: 'Recent expenses',
  },
  month: { backToCurrent: 'Back to current month', total: 'Month total: {amount}', none: 'No expenses in {month}.' },
  cat: {
    ALIMENTACAO: 'Groceries', RESTAURANTES: 'Restaurants', TRANSPORTES: 'Transport', CASA: 'Home',
    COMPRAS: 'Shopping', SAUDE: 'Health/Cosmetics', LAZER: 'Leisure', SUBSCRICOES: 'Subscriptions',
    EDUCACAO: 'Education', OUTROS: 'Other',
    count0: '—', count1: '1 expense', countN: '{n} expenses', emptyDetail: 'Nothing in this category this month.',
  },
  add: {
    title: 'New expense', amount: 'Amount', label: 'What was it for?', labelPlaceholder: 'E.g.: groceries, fuel, Netflix, pharmacy',
    category: 'Category', date: 'Date', today: 'Today', save: 'Save', cancel: 'Cancel',
  },
  delete: { question: 'Delete this expense?', confirm: 'Delete', cancel: 'Cancel' },
  settings: {
    title: 'Settings', language: 'Language', appearance: 'Appearance',
    system: 'Automatic', light: 'Light', dark: 'Dark', pt: 'Português (PT)', en: 'English',
    about: 'Your data stays on this iPhone only.',
  },
};

export const STRINGS: Record<Lang, typeof pt> = { pt, en };
export const LOCALE: Record<Lang, string> = { pt: 'pt-PT', en: 'en-GB' };

export function translate(lang: Lang, key: string, vars?: Record<string, string | number>): string {
  const value = key.split('.').reduce<any>((o, k) => (o ? o[k] : undefined), STRINGS[lang]);
  let s = typeof value === 'string' ? value : key;
  if (vars) for (const [k, v] of Object.entries(vars)) s = s.replace(`{${k}}`, String(v));
  return s;
}
