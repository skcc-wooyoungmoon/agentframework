import type { UIListProps } from './types';

export const UIList = ({ gap, direction, align, className = '', data, ...innerProps }: UIListProps) => {
  const buildClass = () => {
    const classes: string[] = [];

    classes.push('ui-list');

    // Add custom className if provided
    if (className) {
      classes.push(className);
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

    if (direction) {
      styles.flexDirection = direction;
    }

    // 높이 자동 조정을 위한 설정
    styles.height = 'auto';
    styles.minHeight = 'fit-content';
    styles.overflow = 'visible';
    styles.wordBreak = 'break-word';

    return styles;
  };

  return (
    <>
      {data && data.length > 0 ? (
        <ul className={buildClass()} style={buildStyle()} {...innerProps}>
          {data.map((item, index) => (
            <li key={index}>{item.dataItem}</li>
          ))}
        </ul>
      ) : (
        <></>
      )}
    </>
  );
};
