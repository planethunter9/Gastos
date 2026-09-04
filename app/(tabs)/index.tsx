import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import { Pressable, ScrollView, Text, View } from 'react-native';
import { categoryById } from '@/categories';
import { Card, CategoryIcon, ExpenseRow, MonthSelector, ProportionBar, SectionTitle, StatCard, TAB_BAR_SPACE, TopBar } from '@/components/ui';
import { daysInMonth, useStore, ym } from '@/store';

export default function Home() {
  const { colors, t, euros, month, monthLabel, expenses, previousTotal } = useStore();
  const router = useRouter();

  const total = expenses.reduce((a, e) => a + e.amountCents, 0);
  const byCat = Object.entries(
    expenses.reduce<Record<string, number>>((acc, e) => ({ ...acc, [e.category]: (acc[e.category] ?? 0) + e.amountCents }), {}),
  ).sort((a, b) => b[1] - a[1]);
  const daysElapsed = month === ym(new Date()) ? new Date().getDate() : daysInMonth(month);
  const perDay = expenses.length ? Math.round(total / daysElapsed) : 0;
  const biggest = expenses.reduce((m, e) => Math.max(m, e.amountCents), 0);

  let comparison = t('home.noCompare');
  let up = false;
  if (previousTotal > 0) {
    const pct = Math.round(((total - previousTotal) * 100) / previousTotal);
    up = pct > 0;
    comparison = t('home.vsPrev', { pct: (up ? '+' : '') + pct, amount: euros(previousTotal) });
  } else if (total > 0) comparison = t('home.noPrev');

  return (
    <View style={{ flex: 1, backgroundColor: colors.bg }}>
      <TopBar title={t('tabs.home')} />
      <ScrollView contentContainerStyle={{ padding: 16, gap: 12, paddingBottom: TAB_BAR_SPACE }}>
        <MonthSelector />

        <Card color={colors.primary}>
          <Text style={{ color: '#FFFFFFD9', fontSize: 14 }}>{t('home.spentIn', { month: monthLabel(month).split(' ')[0] })}</Text>
          <Text style={{ color: '#fff', fontSize: 40, fontWeight: '700', letterSpacing: -1, marginVertical: 4 }}>{euros(total)}</Text>
          <View style={{ flexDirection: 'row', alignItems: 'center', gap: 4 }}>
            {previousTotal > 0 && <Ionicons name={up ? 'arrow-up' : 'arrow-down'} size={14} color="#fff" />}
            <Text style={{ color: '#FFFFFFE6', fontSize: 14 }}>{comparison}</Text>
          </View>
        </Card>

        <View style={{ flexDirection: 'row', gap: 10 }}>
          <StatCard label={t('home.expenses')} value={String(expenses.length)} />
          <StatCard label={t('home.perDay')} value={euros(perDay)} />
          <StatCard label={t('home.biggest')} value={biggest ? euros(biggest) : '—'} />
        </View>

        {expenses.length === 0 ? (
          <Card style={{ padding: 20 }}>
            <Text style={{ color: colors.ink, fontSize: 16, fontWeight: '600' }}>{t('home.empty')}</Text>
            <Text style={{ color: colors.muted, marginTop: 4 }}>{t('home.emptyHint')}</Text>
          </Card>
        ) : (
          <>
            <SectionTitle>{t('home.whereMoney')}</SectionTitle>
            <Card style={{ gap: 14 }}>
              {byCat.slice(0, 3).map(([id, amount]) => (
                <View key={id} style={{ gap: 6 }}>
                  <View style={{ flexDirection: 'row', alignItems: 'center', gap: 10 }}>
                    <CategoryIcon id={id} size={32} />
                    <Text style={{ flex: 1, color: colors.ink, fontSize: 16 }}>{t('cat.' + id)}</Text>
                    <Text style={{ color: colors.ink, fontSize: 16, fontWeight: '600' }}>{euros(amount)}</Text>
                  </View>
                  <ProportionBar fraction={total ? amount / total : 0} color={categoryById(id).color} />
                </View>
              ))}
              {byCat.length > 3 && (
                <Pressable onPress={() => router.navigate('/categorias')} style={{ alignSelf: 'flex-end' }}>
                  <Text style={{ color: colors.primary, fontWeight: '600' }}>{t('home.seeAll')}</Text>
                </Pressable>
              )}
            </Card>

            <SectionTitle>{t('home.latest')}</SectionTitle>
            <Card style={{ padding: 6 }}>
              {expenses.slice(0, 6).map((e) => <ExpenseRow key={e.id} e={e} />)}
            </Card>
          </>
        )}
      </ScrollView>
    </View>
  );
}
