import { useState } from 'react';

import type { UIDropdownOptionProps } from './types';

/**
 * UIDropdownOption 컴포넌트
 *
 * @description 드랍다운 옵션 아이템 컴포넌트
 * - 높이: 44px
 * - 텍스트: 16px/24px, 최대 40자
 * - 호버 시 파란색 텍스트와 배경
 */
export function UIDropdownOption({ value, label, isSelected = false, state = 'default', onClick, fontSize = 16 }: UIDropdownOptionProps) {
  const [isHovered, setIsHovered] = useState(false);

  // UIListContentBox.Header 내부에 있는지 자동 감지 (높이 조절용)
  const getHeight = () => {
    // fontSize가 14px면 UIListContentBox.Header 내부로 판단
    return fontSize === 14 ? 'h-9' : 'h-10'; // 36px : 40px
  };

  // 상태에 따른 스타일 (우선순위: 호버 > 선택 > 에러 > 포커스 > 기본)
  const getBackgroundColor = () => {
    if (isHovered) return 'bg-blue-100';
    // if (isSelected) return 'bg-blue-100';
    if (state === 'error') return 'bg-red-50';
    if (state === 'focused') return 'bg-gray-50';
    return 'bg-transparent';
  };

  const getTextColor = () => {
    if (isHovered) return 'text-gray-600';
    if (isSelected) return '#005DF9';
    if (state === 'error') return 'text-red-600';
    if (state === 'focused') return 'text-gray-700';
    return 'text-gray-500';
  };

  const textColor = getTextColor();
  const isCustomColor = textColor.startsWith('#') || textColor.startsWith('rgb');
  const textColorStyle = isCustomColor ? { color: textColor } : {};

  return (
    <div
      className={`
        ${getHeight()} px-4 cursor-pointer rounded-md
        text-caption-2 font-normal
        transition-all duration-200
        flex items-center
        whitespace-nowrap overflow-hidden text-ellipsis
        ${getBackgroundColor()}
        ${!isCustomColor ? textColor : ''}
      `}
      style={{ fontSize: `${fontSize}px`, ...textColorStyle }}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      onClick={() => onClick?.(value)}
    >
      {label}
    </div>
  );
}
