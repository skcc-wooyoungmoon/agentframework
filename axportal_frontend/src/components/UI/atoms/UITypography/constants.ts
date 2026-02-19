import type { UITypographyProps } from './types';

// Tailwind fontSize 매핑 (Atomic atom)
export const fontSizeClassMap: Record<UITypographyProps['variant'], string> = {
  'headline-1': 'text-headline-1',
  'headline-2-product': 'text-headline-2-product',
  'headline-2': 'text-headline-2',
  'title-1': 'text-title-1',
  'title-2': 'text-title-2',
  'title-3': 'text-title-3',
  'title-4': 'text-title-4',
  'body-1': 'text-body-1',
  'body-2': 'text-body-2',
  'body-3': 'text-body-3',
  'caption-1': 'text-caption-1',
  'caption-2': 'text-caption-2',
  'appbar-1': 'text-appbar-1',
  'appbar-2': 'text-appbar-2',
  'bottom-1': 'text-bottom-1',
};
