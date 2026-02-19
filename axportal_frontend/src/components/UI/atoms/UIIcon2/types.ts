import type { ButtonHTMLAttributes } from 'react';

export interface UIIconProps extends Omit<ButtonHTMLAttributes<HTMLButtonElement>, 'className'> {
  className?: string;
}
