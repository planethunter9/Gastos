// Definições (engrenagem no canto superior direito): Idioma PT/EN e Aspeto Automático/Claro/Escuro.
import { Ionicons } from '@expo/vector-icons';
import { Pressable, ScrollView, Text, View } from 'react-native';
import { Card, SectionTitle, TopBackBar } from '@/components/ui';
import type { Lang } from '@/i18n';
import { useStore, type ThemeMode } from '@/store';

function Option({ label, selected, onPress, last }: { label: string; selected: boolean; onPress: () => void; last?: boolean }) {
  const { colors } = useStore();
  return (
    <Pressable onPress={onPress} style={({ pressed }) => [
      { flexDirection: 'row', alignItems: 'center', paddingVertical: 14, paddingHorizontal: 16, borderBottomWidth: last ? 0 : 1, borderBottomColor: colors.line },
      pressed && { opacity: 0.6 },
    ]}>
      <Text style={{ flex: 1, color: colors.ink, fontSize: 16 }}>{label}</Text>
      {selected && <Ionicons name="checkmark" size={20} color={colors.primary} />}
    </Pressable>
  );
}

export default function Settings() {
  const { colors, t, lang, setLang, theme, setTheme } = useStore();
  const langs: Lang[] = ['pt', 'en'];
  const themes: ThemeMode[] = ['system', 'light', 'dark'];

  return (
    <View style={{ flex: 1, backgroundColor: colors.bg }}>
      <TopBackBar title={t('settings.title')} />
      <ScrollView contentContainerStyle={{ padding: 16, gap: 12, paddingBottom: 40 }}>
        <SectionTitle>{t('settings.language')}</SectionTitle>
        <Card style={{ padding: 0, overflow: 'hidden' }}>
          {langs.map((l, i) => <Option key={l} label={t('settings.' + l)} selected={lang === l} onPress={() => setLang(l)} last={i === langs.length - 1} />)}
        </Card>

        <SectionTitle>{t('settings.appearance')}</SectionTitle>
        <Card style={{ padding: 0, overflow: 'hidden' }}>
          {themes.map((m, i) => <Option key={m} label={t('settings.' + m)} selected={theme === m} onPress={() => setTheme(m)} last={i === themes.length - 1} />)}
        </Card>

        <Text style={{ color: colors.muted, fontSize: 13, marginTop: 8, marginHorizontal: 4 }}>{t('settings.about')}</Text>
      </ScrollView>
    </View>
  );
}
