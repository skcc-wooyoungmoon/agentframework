import type { ButtonHTMLAttributes, ReactNode } from 'react';

import type { UIIconProps } from './../UIIcon2/types';

export interface UIButtonProps extends Omit<ButtonHTMLAttributes<HTMLButtonElement>, 'className'> {
  children?: ReactNode;
  className?: string;
  leftIcon?: UIIconProps; // New prop for left-side icons
  rightIcon?: UIIconProps; // New prop for right-side icons
  preventDoubleClick?: boolean; // 이중클릭 방지 옵션
  debounceDelay?: number; // debounce 지연 시간 (ms, 기본값: 300)
}
