import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import React from 'react';
import { Alert, Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { categoryById } from '../categories';
import type { Expense } from '../db';
import { useStore, ym } from '../store';

export const TAB_BAR_SPACE = 100; // folga no fundo dos ecrãs para a cápsula não tapar conteúdo

export function Card({ children, style, color }: { children: React.ReactNode; style?: any; color?: string }) {
  const { colors } = useStore();
  return <View style={[styles.card, { backgroundColor: color ?? colors.card }, style]}>{children}</View>;
}

export function CategoryIcon({ id, size = 40 }: { id: string; size?: number }) {
  const c = categoryById(id);
  return (
    <View style={{ width: size, height: size, borderRadius: size / 2, backgroundColor: c.color + '26', alignItems: 'center', justifyContent: 'center' }}>
      <Ionicons name={c.icon as any} size={size * 0.5} color={c.color} />
    </View>
  );
}

export function ProportionBar({ fraction, color }: { fraction: number; color: string }) {
  const { colors } = useStore();
  return (
    <View style={{ height: 8, borderRadius: 99, backgroundColor: colors.track, overflow: 'hidden' }}>
      <View style={{ width: `${Math.max(0, Math.min(1, fraction)) * 100}%`, height: 8, borderRadius: 99, backgroundColor: color }} />
    </View>
  );
}

// Cabeçalho dos separadores: título à esquerda, "+" e engrenagem à direita (como na app das fobias).
export function TopBar({ title }: { title: string }) {
  const { colors } = useStore();
  const insets = useSafeAreaInsets();
  const router = useRouter();
  return (
    <View style={{ paddingTop: insets.top, backgroundColor: colors.bg }}>
      <View style={{ flexDirection: 'row', alignItems: 'center', paddingHorizontal: 16, paddingVertical: 10 }}>
        <Text style={{ flex: 1, color: colors.ink, fontSize: 30, fontWeight: '700', letterSpacing: -0.5 }}>{title}</Text>
        <Pressable onPress={() => router.push('/nova')} hitSlop={10} style={{ padding: 6 }}>
          <Ionicons name="add-circle" size={30} color={colors.primary} />
        </Pressable>
        <Pressable onPress={() => router.push('/definicoes')} hitSlop={10} style={{ padding: 6 }}>
          <Ionicons name="settings-outline" size={26} color={colors.ink} />
        </Pressable>
      </View>
    </View>
  );
}

// Cabeçalho com "‹ Voltar" dos ecrãs empurrados (definições, categoria).
export function TopBackBar({ title }: { title: string }) {
  const { colors, t } = useStore();
  const insets = useSafeAreaInsets();
  const router = useRouter();
  return (
    <View style={{ paddingTop: insets.top, backgroundColor: colors.bg }}>
      <View style={{ flexDirection: 'row', alignItems: 'center', paddingHorizontal: 8, paddingVertical: 8 }}>
        <Pressable onPress={() => router.back()} hitSlop={12} style={{ flexDirection: 'row', alignItems: 'center', padding: 6 }}>
          <Ionicons name="chevron-back" size={26} color={colors.primary} />
          <Text style={{ color: colors.primary, fontSize: 17 }}>{t('common.back')}</Text>
        </Pressable>
        <Text style={{ flex: 1, color: colors.ink, fontSize: 18, fontWeight: '700', textAlign: 'center', marginRight: 90 }} numberOfLines={1}>{title}</Text>
      </View>
    </View>
  );
}

export function MonthSelector() {
  const { colors, t, month, monthLabel, prevMonth, nextMonth, goToCurrent } = useStore();
  const isCurrent = month === ym(new Date());
  return (
    <View style={[styles.month, { backgroundColor: colors.card, borderColor: colors.line }]}>
      <Pressable onPress={prevMonth} hitSlop={12} style={styles.arrow}><Ionicons name="chevron-back" size={22} color={colors.ink} /></Pressable>
      <View style={{ flex: 1, alignItems: 'center' }}>
        <Text style={{ color: colors.ink, fontSize: 16, fontWeight: '600' }}>{monthLabel(month)}</Text>
        {!isCurrent && (
          <Pressable onPress={goToCurrent} hitSlop={8}>
            <Text style={{ color: colors.primary, fontSize: 12, fontWeight: '600', marginTop: 2 }}>{t('month.backToCurrent')}</Text>
          </Pressable>
        )}
      </View>
      <Pressable onPress={nextMonth} hitSlop={12} style={styles.arrow}><Ionicons name="chevron-forward" size={22} color={colors.ink} /></Pressable>
    </View>
  );
}

export function ExpenseRow({ e, showCategory = true }: { e: Expense; showCategory?: boolean }) {
  const s = useStore();
  const { colors, t, euros, dateLabel } = s;
  const confirmDelete = () =>
    Alert.alert(e.label, `${euros(e.amountCents)} · ${t('cat.' + e.category)}\n\n${t('delete.question')}`, [
      { text: t('delete.cancel'), style: 'cancel' },
      { text: t('delete.confirm'), style: 'destructive', onPress: () => s.remove(e.id) },
    ]);
  return (
    <Pressable onPress={confirmDelete} style={({ pressed }) => [styles.row, pressed && { opacity: 0.6 }]}>
      <CategoryIcon id={e.category} />
      <View style={{ flex: 1 }}>
        <Text numberOfLines={1} style={{ color: colors.ink, fontSize: 16 }}>{e.label}</Text>
        <Text style={{ color: colors.muted, fontSize: 13, marginTop: 2 }}>
          {showCategory ? `${t('cat.' + e.category)} · ${dateLabel(e.date)}` : dateLabel(e.date)}
        </Text>
      </View>
      <Text style={{ color: colors.ink, fontSize: 16, fontWeight: '600' }}>{euros(e.amountCents)}</Text>
    </Pressable>
  );
}

export function SectionTitle({ children }: { children: string }) {
  const { colors } = useStore();
  return <Text style={{ color: colors.ink, fontSize: 20, fontWeight: '600', marginTop: 8, marginHorizontal: 4 }}>{children}</Text>;
}

export function StatCard({ label, value }: { label: string; value: string }) {
  const { colors } = useStore();
  return (
    <Card style={{ flex: 1, padding: 14, borderRadius: 16 }}>
      <Text style={{ color: colors.muted, fontSize: 12 }}>{label}</Text>
      <Text numberOfLines={1} style={{ color: colors.ink, fontSize: 16, fontWeight: '600', marginTop: 2 }}>{value}</Text>
    </Card>
  );
}

const styles = StyleSheet.create({
  card: { borderRadius: 20, padding: 16 },
  month: { flexDirection: 'row', alignItems: 'center', borderRadius: 16, borderWidth: StyleSheet.hairlineWidth, paddingVertical: 8, paddingHorizontal: 4 },
  arrow: { width: 44, height: 36, alignItems: 'center', justifyContent: 'center' },
  row: { flexDirection: 'row', alignItems: 'center', gap: 12, paddingHorizontal: 12, paddingVertical: 10, borderRadius: 14 },
});
