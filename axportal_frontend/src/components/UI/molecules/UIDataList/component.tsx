import type { UIDataListProps } from './types';

export const UIDataList = ({ gap, direction, align, children, className = '', datalist, ...innerProps }: UIDataListProps) => {
  const buildClass = () => {
    const classes: string[] = [];

    classes.push('data-list-wrap');

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

    return styles;
  };

  return (
    <>
      {datalist && datalist.length > 0 ? (
        <div className={buildClass()} style={buildStyle()} {...innerProps}>
          {datalist.map((item, index) => (
            <dl className='data-list__row' key={index}>
              <dt className='data-list__name'>{item.dataName}</dt>
              <dd className='data-list__value'>{item.dataValue}</dd>
            </dl>
          ))}
        </div>
      ) : (
        children
      )}
    </>
  );
};
