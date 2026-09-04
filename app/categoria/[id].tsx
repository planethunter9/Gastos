import { useLocalSearchParams } from 'expo-router';
import { ScrollView, Text, View } from 'react-native';
import { Card, CategoryIcon, ExpenseRow, TopBackBar } from '@/components/ui';
import { useStore } from '@/store';

export default function CategoryDetail() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const { colors, t, euros, month, monthLabel, expenses } = useStore();
  const list = expenses.filter((e) => e.category === id);
  const total = list.reduce((a, e) => a + e.amountCents, 0);

  return (
    <View style={{ flex: 1, backgroundColor: colors.bg }}>
      <TopBackBar title={t('cat.' + id)} />
      <ScrollView contentContainerStyle={{ padding: 16, gap: 12, paddingBottom: 40 }}>
        <Card style={{ flexDirection: 'row', alignItems: 'center', gap: 12 }}>
          <CategoryIcon id={id} size={48} />
          <View style={{ flex: 1 }}>
            <Text style={{ color: colors.ink, fontSize: 18, fontWeight: '600' }}>{t('cat.' + id)}</Text>
            <Text style={{ color: colors.muted }}>{monthLabel(month)}</Text>
          </View>
          <Text style={{ color: colors.ink, fontSize: 22, fontWeight: '700' }}>{euros(total)}</Text>
        </Card>
        {list.length === 0 ? (
          <Text style={{ color: colors.muted, marginHorizontal: 4 }}>{t('cat.emptyDetail')}</Text>
        ) : (
          <Card style={{ padding: 6 }}>{list.map((e) => <ExpenseRow key={e.id} e={e} showCategory={false} />)}</Card>
        )}
      </ScrollView>
    </View>
  );
}
