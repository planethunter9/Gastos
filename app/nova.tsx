// Folha modal "Nova despesa": valor, label (em que foi gasto), categoria e data.
import DateTimePicker from '@react-native-community/datetimepicker';
import { Ionicons } from '@expo/vector-icons';
import { useRouter } from 'expo-router';
import { useState } from 'react';
import { KeyboardAvoidingView, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from 'react-native';
import { CATEGORIES, type CategoryId } from '@/categories';
import { useStore, ym, ymd } from '@/store';

// "12,50" ou "12.50" -> 1250. null se inválido.
function parseCents(text: string): number | null {
  const v = Number(text.trim().replace(',', '.'));
  if (!text.trim() || !Number.isFinite(v) || v <= 0) return null;
  return Math.round(v * 100);
}

export default function NewExpense() {
  const { colors, t, month, add, dateLabel } = useStore();
  const router = useRouter();

  const today = new Date();
  const initial = month === ym(today) ? today : new Date(Number(month.slice(0, 4)), Number(month.slice(5, 7)) - 1, 1);
  const [amount, setAmount] = useState('');
  const [label, setLabel] = useState('');
  const [category, setCategory] = useState<CategoryId | null>(null);
  const [date, setDate] = useState(initial);

  const cents = parseCents(amount);
  const canSave = cents !== null && label.trim().length > 0 && category !== null;

  const save = () => {
    if (!canSave) return;
    add(cents!, label, category!, ymd(date));
    router.back();
  };

  return (
    <KeyboardAvoidingView behavior="padding" style={{ flex: 1, backgroundColor: colors.bg }}>
      {/* Cabeçalho da folha: Cancelar · título · Guardar */}
      <View style={{ flexDirection: 'row', alignItems: 'center', paddingHorizontal: 16, paddingTop: 18, paddingBottom: 8 }}>
        <Pressable onPress={() => router.back()} hitSlop={10}><Text style={{ color: colors.primary, fontSize: 17 }}>{t('add.cancel')}</Text></Pressable>
        <Text style={{ flex: 1, textAlign: 'center', color: colors.ink, fontSize: 17, fontWeight: '600' }}>{t('add.title')}</Text>
        <Pressable onPress={save} disabled={!canSave} hitSlop={10}>
          <Text style={{ color: canSave ? colors.primary : colors.muted, fontSize: 17, fontWeight: '600' }}>{t('add.save')}</Text>
        </Pressable>
      </View>

      <ScrollView contentContainerStyle={{ padding: 16, gap: 18 }} keyboardShouldPersistTaps="handled">
        <View style={{ gap: 6 }}>
          <Text style={[styles.fieldLabel, { color: colors.muted }]}>{t('add.amount')}</Text>
          <View style={[styles.input, { backgroundColor: colors.card, borderColor: colors.line, flexDirection: 'row', alignItems: 'center' }]}>
            <TextInput
              value={amount}
              onChangeText={(v) => setAmount(v.replace(/[^0-9.,]/g, ''))}
              keyboardType="decimal-pad"
              placeholder="0,00"
              placeholderTextColor={colors.muted}
              autoFocus
              style={{ flex: 1, color: colors.ink, fontSize: 28, fontWeight: '600' }}
            />
            <Text style={{ color: colors.muted, fontSize: 22 }}>€</Text>
          </View>
        </View>

        <View style={{ gap: 6 }}>
          <Text style={[styles.fieldLabel, { color: colors.muted }]}>{t('add.label')}</Text>
          <TextInput
            value={label}
            onChangeText={setLabel}
            placeholder={t('add.labelPlaceholder')}
            placeholderTextColor={colors.muted}
            returnKeyType="done"
            style={[styles.input, { backgroundColor: colors.card, borderColor: colors.line, color: colors.ink, fontSize: 17 }]}
          />
        </View>

        <View style={{ gap: 8 }}>
          <Text style={[styles.fieldLabel, { color: colors.muted }]}>{t('add.category')}</Text>
          <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 8 }}>
            {CATEGORIES.map((c) => {
              const sel = category === c.id;
              return (
                <Pressable key={c.id} onPress={() => setCategory(c.id)}
                  style={[styles.chip, { backgroundColor: sel ? c.color : colors.card, borderColor: sel ? c.color : colors.line }]}>
                  <Ionicons name={c.icon as any} size={15} color={sel ? '#fff' : c.color} />
                  <Text style={{ color: sel ? '#fff' : colors.ink, fontSize: 15, fontWeight: sel ? '600' : '400' }}>{t('cat.' + c.id)}</Text>
                </Pressable>
              );
            })}
          </View>
        </View>

        <View style={{ flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }}>
          <Text style={[styles.fieldLabel, { color: colors.muted }]}>{t('add.date')}</Text>
          <DateTimePicker value={date} mode="date" display="compact" onChange={(_, d) => d && setDate(d)} accentColor={colors.primary} />
        </View>
        <Text style={{ color: colors.muted, fontSize: 13, textAlign: 'right' }}>
          {ymd(date) === ymd(today) ? t('add.today') : dateLabel(ymd(date), true)}
        </Text>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  fieldLabel: { fontSize: 13, fontWeight: '600', textTransform: 'uppercase', letterSpacing: 0.5, marginHorizontal: 4 },
  input: { borderRadius: 14, borderWidth: StyleSheet.hairlineWidth, paddingHorizontal: 16, paddingVertical: 14 },
  chip: { flexDirection: 'row', alignItems: 'center', gap: 6, paddingHorizontal: 12, paddingVertical: 9, borderRadius: 20, borderWidth: 1 },
});
