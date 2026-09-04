// As mesmas 10 categorias da versão Android. Ícones do Ionicons (@expo/vector-icons).
export type CategoryId =
  | 'ALIMENTACAO' | 'RESTAURANTES' | 'TRANSPORTES' | 'CASA' | 'COMPRAS'
  | 'SAUDE' | 'LAZER' | 'SUBSCRICOES' | 'EDUCACAO' | 'OUTROS';

export type Category = { id: CategoryId; icon: string; color: string };

export const CATEGORIES: Category[] = [
  { id: 'ALIMENTACAO', icon: 'basket', color: '#2E8B57' },
  { id: 'RESTAURANTES', icon: 'restaurant', color: '#E07A3F' },
  { id: 'TRANSPORTES', icon: 'car', color: '#3B7DD8' },
  { id: 'CASA', icon: 'home', color: '#8E6BBF' },
  { id: 'COMPRAS', icon: 'bag-handle', color: '#D8508A' },
  { id: 'SAUDE', icon: 'medkit', color: '#D64545' },
  { id: 'LAZER', icon: 'game-controller', color: '#1FA3A3' },
  { id: 'SUBSCRICOES', icon: 'repeat', color: '#5C6BC0' },
  { id: 'EDUCACAO', icon: 'school', color: '#B08D2B' },
  { id: 'OUTROS', icon: 'ellipsis-horizontal', color: '#7A8A99' },
];

export const categoryById = (id: string): Category =>
  CATEGORIES.find((c) => c.id === id) ?? CATEGORIES[CATEGORIES.length - 1];
