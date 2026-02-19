import type { UIGroupProps } from './types';

export const UIGroup = ({ gap, direction, align, vAlign, children, className = '', style, ...innerProps }: UIGroupProps) => {
  const buildClass = () => {
    const classes: string[] = [];

    classes.push('ui-group');

    // Add custom className if provided
    if (className) {
      classes.push(className);
    }

    if (direction) {
      classes.push(direction);
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

    // 사용자가 전달한 style 속성을 병합 (우선순위 높음)
    return { ...styles, ...style };
  };

  return (
    <div className={buildClass()} style={buildStyle()} {...innerProps}>
      {children}
    </div>
  );
};
