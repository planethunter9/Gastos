import type { BottomTabBarProps } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import { GlassView, isLiquidGlassAvailable } from 'expo-glass-effect';
import { Platform, Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { useStore } from '../store';

const ICONS: Record<string, { on: string; off: string }> = {
  index: { on: 'home', off: 'home-outline' },
  categorias: { on: 'grid', off: 'grid-outline' },
};

const glassSupported = Platform.OS === 'ios' && isLiquidGlassAvailable();

export function GlassTabBar({ state, descriptors, navigation }: BottomTabBarProps) {
  const { colors, isDark } = useStore();
  const insets = useSafeAreaInsets();

  return (
    <View
      style={{
        position: 'absolute', left: 20, right: 20, bottom: (insets.bottom || 10) + 6, height: 64, borderRadius: 32,
        overflow: 'hidden', borderWidth: StyleSheet.hairlineWidth, borderColor: colors.line,
        shadowColor: '#000', shadowOpacity: 0.08, shadowRadius: 12, shadowOffset: { width: 0, height: 6 },
      }}>
      {glassSupported ? (
        // Vidro claro no modo claro, escuro no modo escuro (senão o iOS escolhe a variante errada).
        <GlassView
          glassEffectStyle="regular"
          tintColor={isDark ? 'rgba(20,28,24,0.35)' : 'rgba(255,255,255,0.45)'}
          style={StyleSheet.absoluteFill}
        />
      ) : (
        <View style={[StyleSheet.absoluteFill, { backgroundColor: isDark ? 'rgba(26,34,30,0.9)' : 'rgba(255,255,255,0.9)' }]} />
      )}
      <View style={{ flexDirection: 'row', height: 64, alignItems: 'center', paddingHorizontal: 8 }}>
        {state.routes.map((route, i) => {
          const active = state.index === i;
          const label = String(descriptors[route.key].options.title ?? route.name);
          const icon = ICONS[route.name] ?? ICONS.index;
          return (
            <Pressable
              key={route.key}
              onPress={() => {
                const ev = navigation.emit({ type: 'tabPress', target: route.key, canPreventDefault: true });
                if (!active && !ev.defaultPrevented) navigation.navigate(route.name);
              }}
              style={{ flex: 1, alignItems: 'center', justifyContent: 'center', gap: 2, paddingVertical: 8 }}>
              <Ionicons name={(active ? icon.on : icon.off) as any} size={24} color={active ? colors.primary : colors.muted} />
              <Text style={{ fontSize: 11, fontWeight: active ? '600' : '400', color: active ? colors.primary : colors.muted }}>{label}</Text>
            </Pressable>
          );
        })}
      </View>
    </View>
  );
}
