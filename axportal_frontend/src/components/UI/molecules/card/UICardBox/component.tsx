import React from 'react';
import type { UICardBoxProps } from './types';

export const UICardBox = ({ gap, children, flexType, className = '', ...innerProps }: UICardBoxProps) => {
  const buildClass = () => {
    const classes: string[] = [];

    classes.push('ui-grid-cardlist w-full mt-2 overflow-x-auto flex-nowrap');  //  [1005] class 추가  w-[1550px] overflow-x-auto flex-nowrap

    // Add custom className if provided
    if (className) {
      classes.push(className);
    }

    return classes.filter(Boolean).join(' ');
  };


  // li 요소들에 flexType 스타일 적용
  const buildLiStyle = () => {
    const styles: React.CSSProperties = {};

    if (flexType) {
      //styles.flexShrink = 0; // [1005] stlye 501px 고정
      styles.flex = '0 1 auto';

      switch (flexType) {
        case 'none':
          styles.flex = '0 0 auto';
          break;
        case 'shrink':
          styles.flex = '0 1 auto';
          break;
        case 'grow':
          // 가변으로 꽉차게 0 1 auto
          styles.flex = '501px 1 auto';
          break;
      }
    }

    return styles;
  };

  // children을 클론하여 각 li에 스타일 적용
  const enhancedChildren = React.Children.map(children, (child) => {
    if (React.isValidElement(child) && child.type === 'li') {
      const childProps = child.props as any;
      return React.cloneElement(child as React.ReactElement<any>, {
        style: { ...childProps.style, ...buildLiStyle() }
      });
    }
    return child;
  });

  return (
    <div className={buildClass()} style={{ gap: gap ? (typeof gap === 'number' ? `${gap}px` : gap) : undefined }} {...innerProps}>
      <ul>{enhancedChildren}</ul>
    </div>
  );
};
