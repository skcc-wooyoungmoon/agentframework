import type { UIFormFieldProps } from './types';

export const UIFormField = ({ gap, direction, division, align, vAlign, children, className = '', ...innerProps }: UIFormFieldProps) => {
  const buildClass = () => {
    const classes: string[] = [];

    classes.push('form-field');

    // Add custom className if provided
    if (className) {
      classes.push(className);
    }

    if (direction) {
      classes.push(direction);
    }

    if (division) {
      classes.push(division);
    }

    return classes.filter(Boolean).join(' ');
  };

  // style 객체 정의
  const buildStyle = () => {
    const styles: React.CSSProperties = {};

    if (gap) {
      // gap이 숫자라면 px 단위 추가
      styles.gap = typeof gap === 'number' ? `${gap}px` : gap;
    }

    if (align) {
      styles.justifyContent = align;
    }

    if (vAlign) {
      styles.alignItems = vAlign;
    }

    return styles;
  };

  return (
    <div className={buildClass()} style={buildStyle()} {...innerProps}>
      {children}
    </div>
  );
};
