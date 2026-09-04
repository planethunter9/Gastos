// Estado partilhado: mês selecionado, despesas, idioma, tema e cores.
import AsyncStorage from '@react-native-async-storage/async-storage';
import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { Appearance, useColorScheme } from 'react-native';
import type { CategoryId } from './categories';
import { deleteExpense, insertExpense, listByMonth, totalByMonth, type Expense } from './db';
import { LOCALE, translate, type Lang } from './i18n';

export type ThemeMode = 'system' | 'light' | 'dark';

// ---------- Datas ----------
export const ym = (d: Date) => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
export const ymd = (d: Date) => `${ym(d)}-${String(d.getDate()).padStart(2, '0')}`;
export const shiftMonth = (month: string, delta: number) => {
  const [y, m] = month.split('-').map(Number);
  return ym(new Date(y, m - 1 + delta, 1));
};
export const daysInMonth = (month: string) => {
  const [y, m] = month.split('-').map(Number);
  return new Date(y, m, 0).getDate();
};

// ---------- Cores ----------
const light = {
  bg: '#F5F7F4', card: '#FFFFFF', line: '#E3E8E4', ink: '#17201C', muted: '#6B7772',
  primary: '#1F6F5B', primarySoft: '#DDEFE6', onPrimary: '#FFFFFF', track: '#EEF2EF', danger: '#C63D3D',
};
const dark: typeof light = {
  bg: '#0E1411', card: '#1A221E', line: '#2A3630', ink: '#EEF3F0', muted: '#96A39D',
  primary: '#2C8F75', primarySoft: '#1E3A31', onPrimary: '#FFFFFF', track: '#26312C', danger: '#E06060',
};
export type Palette = typeof light;

// ---------- Contexto ----------
type Store = {
  lang: Lang; setLang: (l: Lang) => void;
  theme: ThemeMode; setTheme: (t: ThemeMode) => void;
  isDark: boolean; colors: Palette;
  t: (key: string, vars?: Record<string, string | number>) => string;
  euros: (cents: number) => string;
  monthLabel: (month: string) => string;
  dateLabel: (date: string, withYear?: boolean) => string;
  month: string; prevMonth: () => void; nextMonth: () => void; goToCurrent: () => void;
  expenses: Expense[]; previousTotal: number;
  add: (amountCents: number, label: string, category: CategoryId, date: string) => void;
  remove: (id: number) => void;
};

const Ctx = createContext<Store | null>(null);

export function StoreProvider({ children }: { children: React.ReactNode }) {
  const [lang, setLangState] = useState<Lang>('pt');
  const [theme, setThemeState] = useState<ThemeMode>('system');
  const [month, setMonth] = useState(ym(new Date()));
  const [version, setVersion] = useState(0); // sobe após cada escrita na BD
  const scheme = useColorScheme();

  useEffect(() => {
    AsyncStorage.multiGet(['lang', 'theme']).then((pairs) => {
      const saved = Object.fromEntries(pairs);
      if (saved.lang === 'pt' || saved.lang === 'en') setLangState(saved.lang);
      if (saved.theme === 'light' || saved.theme === 'dark' || saved.theme === 'system') setThemeState(saved.theme);
    });
  }, []);

  // Aplica o tema a todas as vistas nativas (Stack, alertas, teclado, vidro).
  useEffect(() => { Appearance.setColorScheme(theme === 'system' ? null : theme); }, [theme]);

  const setLang = useCallback((l: Lang) => { setLangState(l); AsyncStorage.setItem('lang', l); }, []);
  const setTheme = useCallback((t: ThemeMode) => { setThemeState(t); AsyncStorage.setItem('theme', t); }, []);

  const expenses = useMemo(() => listByMonth(month), [month, version]);
  const previousTotal = useMemo(() => totalByMonth(shiftMonth(month, -1)), [month, version]);

  const locale = LOCALE[lang];
  const isDark = scheme === 'dark';
  const value = useMemo<Store>(() => ({
    lang, setLang, theme, setTheme, isDark, colors: isDark ? dark : light,
    t: (key, vars) => translate(lang, key, vars),
    euros: (cents) => new Intl.NumberFormat(locale, { style: 'currency', currency: 'EUR' }).format(cents / 100),
    monthLabel: (m) => {
      const [y, mm] = m.split('-').map(Number);
      const s = new Date(y, mm - 1, 1).toLocaleDateString(locale, { month: 'long', year: 'numeric' });
      return s.charAt(0).toUpperCase() + s.slice(1);
    },
    dateLabel: (date, withYear) => {
      const [y, mm, d] = date.split('-').map(Number);
      return new Date(y, mm - 1, d).toLocaleDateString(locale, { day: 'numeric', month: 'short', ...(withYear ? { year: 'numeric' } : {}) });
    },
    month,
    prevMonth: () => setMonth((m) => shiftMonth(m, -1)),
    nextMonth: () => setMonth((m) => shiftMonth(m, 1)),
    goToCurrent: () => setMonth(ym(new Date())),
    expenses, previousTotal,
    add: (amountCents, label, category, date) => {
      insertExpense({ amountCents, label: label.trim(), category, date });
      setMonth(date.slice(0, 7)); // salta para o mês da despesa
      setVersion((v) => v + 1);
    },
    remove: (id) => { deleteExpense(id); setVersion((v) => v + 1); },
  }), [lang, theme, isDark, locale, month, expenses, previousTotal, setLang, setTheme]);

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export function useStore(): Store {
  const s = useContext(Ctx);
  if (!s) throw new Error('useStore fora do StoreProvider');
  return s;
}
