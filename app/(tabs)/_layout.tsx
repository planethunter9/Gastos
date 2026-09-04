import { Tabs } from 'expo-router';
import { GlassTabBar } from '@/components/GlassTabBar';
import { useStore } from '@/store';

export default function TabsLayout() {
  const { t, colors } = useStore();
  return (
    <Tabs
      tabBar={(props) => <GlassTabBar {...props} />}
      screenOptions={{ headerShown: false, sceneStyle: { backgroundColor: colors.bg } }}>
      <Tabs.Screen name="index" options={{ title: t('tabs.home') }} />
      <Tabs.Screen name="categorias" options={{ title: t('tabs.categories') }} />
    </Tabs>
  );
}
