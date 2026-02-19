// UIButton2.tsx
import type { UIIconProps } from './types';

export function UIIcon2({ className = '', ...iconProps }: UIIconProps) {
  const buildIconClass = () => {
    const classes: string[] = [];

    // Add custom className if provided
    if (className) {
      classes.push(className);
    }

    return classes.filter(Boolean).join(' ');
  };

  return <i aria-hidden={'true'} className={buildIconClass()} {...iconProps}></i>;
}
