import type { UIBoxProps } from './types';

export function UIBox({ children, className = '', ...boxProps }: UIBoxProps) {
  const buildClass = () => {
    const classes: string[] = [];

    classes.push('box');

    // Add custom className if provided
    if (className) {
      classes.push(className);
    }

    return classes.filter(Boolean).join(' ');
  };
  return (
    <div className={buildClass()} {...boxProps}>
      {children}
    </div>
  );
}
