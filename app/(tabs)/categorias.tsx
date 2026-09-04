import { useRouter } from 'expo-router';
import { Pressable, ScrollView, Text, View } from 'react-native';
import { CATEGORIES } from '@/categories';
import { Card, CategoryIcon, MonthSelector, ProportionBar, TAB_BAR_SPACE, TopBar } from '@/components/ui';
import { useStore } from '@/store';

// Grelha de categorias com o seletor de mês no topo (mesmo mês do Início).
export default function Categories() {
  const { colors, t, euros, month, monthLabel, expenses } = useStore();
  const router = useRouter();
  const total = expenses.reduce((a, e) => a + e.amountCents, 0);
  const totals: Record<string, number> = {};
  const counts: Record<string, number> = {};
  for (const e of expenses) { totals[e.category] = (totals[e.category] ?? 0) + e.amountCents; counts[e.category] = (counts[e.category] ?? 0) + 1; }
  const ordered = [...CATEGORIES].sort((a, b) => (totals[b.id] ?? 0) - (totals[a.id] ?? 0));

  return (
    <View style={{ flex: 1, backgroundColor: colors.bg }}>
      <TopBar title={t('tabs.categories')} />
      <ScrollView contentContainerStyle={{ padding: 16, gap: 10, paddingBottom: TAB_BAR_SPACE }}>
        <MonthSelector />
        <Text style={{ color: colors.muted, marginHorizontal: 4 }}>
          {total ? t('month.total', { amount: euros(total) }) : t('month.none', { month: monthLabel(month) })}
        </Text>
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10 }}>
          {ordered.map((c) => {
            const amount = totals[c.id] ?? 0;
            const n = counts[c.id] ?? 0;
            return (
              <Pressable
                key={c.id}
                onPress={() => router.push({ pathname: '/categoria/[id]', params: { id: c.id } })}
                style={({ pressed }) => [{ width: '48%', flexGrow: 1 }, pressed && { opacity: 0.7 }]}>
                <Card style={{ padding: 14, borderRadius: 18, gap: 10 }}>
                  <CategoryIcon id={c.id} />
                  <View style={{ gap: 2 }}>
                    <Text style={{ color: colors.muted, fontSize: 14 }}>{t('cat.' + c.id)}</Text>
                    <Text style={{ color: amount ? colors.ink : colors.muted, fontSize: 20, fontWeight: '600' }}>{euros(amount)}</Text>
                    <Text style={{ color: colors.muted, fontSize: 12 }}>
                      {n === 0 ? t('cat.count0') : n === 1 ? t('cat.count1') : t('cat.countN', { n })}
                    </Text>
                  </View>
                  <ProportionBar fraction={total ? amount / total : 0} color={c.color} />
                </Card>
              </Pressable>
            );
          })}
        </View>
      </ScrollView>
    </View>
  );
}
