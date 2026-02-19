import { UIIcon2 } from '../UIIcon2';

import type { UINoticeProps } from './types';

/**
 * UINotice 컴포넌트 (Atomic Design: atom)
 * - 정보 전달을 위한 알림 메시지 컴포넌트
 * - info(회색), warning(빨간색) 타입 지원
 * - 아이콘, 대시, 원형, 넘버링 불릿 타입 지원
 * - 다양한 텍스트 스타일과 깊이 지원
 */
export function UINotice({
  variant = 'info',
  message,
  bulletType = 'icon',
  number,
  fontWeight = 'regular',
  depth = '1depth',
  gapSize = 'normal',
  className = ''
}: UINoticeProps) {
  // 타입별 스타일 클래스
  const getVariantClasses = () => {
    switch (variant) {
      case 'warning':
        return {
          textColor: 'text-negative-red',
          iconName: 'ic-system-16-info-red',
        };
      case 'info':
      default:
        return {
          textColor: 'text-gray-600',
          iconName: 'ic-system-16-info-gray',
        };
    }
  };

  // 폰트 굵기 클래스
  const getFontWeightClass = () => {
    return fontWeight === 'bold' ? 'font-semibold' : 'font-normal';
  };

  // 깊이별 들여쓰기 클래스
  const getDepthClass = () => {
    return depth === '2depth' ? 'ml-4' : '';
  };

  // 불릿 렌더링
  const renderBullet = () => {
    const { textColor, iconName } = getVariantClasses();

    switch (bulletType) {
      case 'icon':
        return (
          <div className='flex-shrink-0 flex items-center h-5'>
            <UIIcon2 className={`${iconName} ${textColor}`} />
          </div>
        );
      case 'dash':
        return (
          <div className='flex-shrink-0 flex items-center h-5'>
            <div className='w-1 h-px bg-gray-600'></div>
          </div>
        );
      case 'circle':
        return (
          <div className='flex-shrink-0 flex items-center h-5'>
            <div className='w-1 h-1 bg-gray-600 rounded-full'></div>
          </div>
        );
      case 'number':
        return (
          <div className='flex-shrink-0 flex items-start h-5'>
            <span className={`text-sm ${textColor}`} style={{ fontSize: '14px', lineHeight: '20px' }}>
              {number || 1}.
            </span>
          </div>
        );
      default:
        return null;
    }
  };

  const { textColor } = getVariantClasses();
  const fontWeightClass = getFontWeightClass();
  const depthClass = getDepthClass();

  // bulletType에 따른 간격 설정
  const getGapClass = () => {
    if (gapSize === 'large') {
      switch (bulletType) {
        case 'icon':
          return 'gap-3'; // 12px
        case 'number':
          return 'gap-3'; // 12px
        default:
          return 'gap-3'; // 12px (dash, circle)
      }
    }

    // gapSize === 'normal'
    switch (bulletType) {
      case 'icon':
        return 'gap-1.5'; // 6px
      case 'number':
        return 'gap-1'; // 4px
      default:
        return 'gap-2'; // 8px (dash, circle)
    }
  };

  const gapClass = getGapClass();

  return (
    <div className={`flex items-start ${gapClass} ${depthClass} ${className}`}>
      {renderBullet()}
      <span className={`text-sm ${textColor} ${fontWeightClass}`} style={{ fontSize: '14px', lineHeight: '20px' }}>
        {message}
      </span>
    </div>
  );
}