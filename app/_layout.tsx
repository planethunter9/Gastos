// Stack raiz (nativa): os ecrãs "por cima" deslizam da direita e voltam
// com o gesto do iOS a partir da margem esquerda — sem código extra.
import { DarkTheme, DefaultTheme, Stack, ThemeProvider } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { useColorScheme } from 'react-native';
import { StoreProvider, useStore } from '@/store';

function Root() {
  const scheme = useColorScheme();
  const { colors } = useStore();
  return (
    <ThemeProvider value={scheme === 'dark' ? DarkTheme : DefaultTheme}>
      <StatusBar style="auto" />
      <Stack screenOptions={{ headerShown: false, gestureEnabled: true, contentStyle: { backgroundColor: colors.bg } }}>
        <Stack.Screen name="(tabs)" />
        <Stack.Screen name="definicoes" />
        <Stack.Screen name="categoria/[id]" />
        <Stack.Screen name="nova" options={{ presentation: 'modal' }} />
      </Stack>
    </ThemeProvider>
  );
}

export default function RootLayout() {
  return (
    <StoreProvider>
      <Root />
    </StoreProvider>
  );
}
