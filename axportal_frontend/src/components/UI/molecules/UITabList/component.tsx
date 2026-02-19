import { useLayoutEffect, useRef, useState } from 'react';

import { UITabButton } from '../../atoms/UITabButton';

import type { UITabListProps } from './types';

/**
 * TabList 컴포넌트 (Atomic Design: molecule)
 * - 여러 TabButton들을 정렬하는 레이아웃 조합
 * - 슬라이딩 인디케이터 표시
 * - 상태 관리 없음 (부모에서 전달받은 props만 사용)
 */
export function UITabList({ items, activeId, size = 'large', variant = 'default', onTabClick, className = '' }: UITabListProps) {
  const tabsRef = useRef<HTMLDivElement>(null);
  const [tabWidths, setTabWidths] = useState<number[]>([]);
  const [textWidths, setTextWidths] = useState<number[]>([]);

  // 활성 탭의 인덱스 찾기
  const activeIndex = items.findIndex(item => item.id === activeId);

  // items 배열의 실제 내용 변화만 감지 (참조 변화 무시)
  const itemKeys = items.map(item => item.id).join(',');

  // 탭 너비 측정 - useLayoutEffect로 변경하여 깜빡임 방지
  useLayoutEffect(() => {
    if (tabsRef.current) {
      const tabElements = tabsRef.current.querySelectorAll('[role="tab"]');
      const widths = Array.from(tabElements).map(tab => (tab as HTMLElement).offsetWidth);

      // 너비가 실제로 변경된 경우에만 업데이트
      setTabWidths(prevWidths => {
        const hasChanged = prevWidths.length !== widths.length || prevWidths.some((w, i) => w !== widths[i]);
        return hasChanged ? widths : prevWidths;
      });

      // 텍스트 너비 측정
      // variant='body-2' 또는 기본 span 모두 포함
      const textElements = tabsRef.current.querySelectorAll('[role="tab"] > *');
      const tWidths = Array.from(textElements).map(text => (text as HTMLElement).offsetWidth);

      setTextWidths(prevTextWidths => {
        const hasChanged = prevTextWidths.length !== tWidths.length || prevTextWidths.some((w, i) => w !== tWidths[i]);
        return hasChanged ? tWidths : prevTextWidths;
      });
    }
  }, [itemKeys, activeId, size, variant]);

  // 슬라이딩 인디케이터 위치 및 크기 계산
  const getIndicatorStyle = () => {
    if (tabWidths.length === 0 || textWidths.length === 0 || activeIndex === -1) {
      // 초기 로딩 시 기본값 사용
      return {
        transform: `translateX(0px)`,
        transition: 'transform 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        width: '0px',
      };
    }

    // 활성 탭까지의 누적 너비 계산
    const baseTranslateX = tabWidths.slice(0, activeIndex).reduce((sum, width) => sum + width, 0);

    // mx-2 (좌우 8px): 탭 간 간격 16px, 활성 탭 좌측 마진 8px 보정
    const interTabGap = 16;
    const activeLeftMargin = 8;
    const translateX = baseTranslateX + activeIndex * interTabGap;

    // 활성 탭의 전체 너비 / 텍스트 너비
    const activeTabWidth = tabWidths[activeIndex] || 0;
    const activeTextWidth = textWidths[activeIndex] || 0;

    // 사이즈별 인디케이터 너비 및 위치 계산
    let indicatorWidth = 0;
    let indicatorTranslateX = 0;

    if (size === 'medium') {
      // medium: 텍스트 폭 + 좌우 패딩(px-3 = 12px * 2) 포함
      const paddingX = 12;
      indicatorWidth = activeTextWidth + paddingX * 2;
      indicatorTranslateX = translateX + activeLeftMargin + (activeTabWidth - indicatorWidth) / 2;
    } else {
      // large: padding 영역 포함한 전체 너비 사용, 위치는 좌측 마진 8px 제외
      indicatorWidth = activeTabWidth;
      indicatorTranslateX = translateX + activeLeftMargin;
    }

    return {
      transform: `translateX(${indicatorTranslateX}px)`,
      transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
      width: `${indicatorWidth}px`,
    };
  };

  const handleTabClick = (tabId: string) => {
    if (onTabClick) {
      onTabClick(tabId);
    }
  };

  return (
    <div className={`relative border-b border-gray-300 ${size === 'large' ? 'h-[48px]' : 'h-[40px]'} ${className}`}>
      <div ref={tabsRef} className='flex' role='tablist'>
        {items.map(item => {
          const isActive = item.id === activeId;

          return (
            <div key={item.id} className='relative flex-shrink-0'>
              <UITabButton label={item.label} isActive={isActive} disabled={item.disabled} size={size} variant={variant} onClick={() => handleTabClick(item.id)} />
            </div>
          );
        })}
      </div>

      {/* 슬라이딩 인디케이터 */}
      <div className={`absolute ${size === 'large' ? '-bottom-[0.5px]' : 'bottom-0'} left-0 h-[2px] bg-blue-800 z-20`} style={getIndicatorStyle()} />
    </div>
  );
}
