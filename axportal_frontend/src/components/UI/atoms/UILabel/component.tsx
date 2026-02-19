import { UIIcon2 } from '../UIIcon2';

import type { UILabelProps } from './types';

/**
 * Label 컴포넌트 (Atomic Design: atom)
 * - 상태나 카테고리를 나타내는 라벨/배지 컴포넌트
 * - Badge: 아이콘과 텍스트가 함께 있는 채워진 스타일
 * - Line: 테두리가 있는 가벼운 스타일
 * - Solid: 테두리 없이 채워진 스타일
 * - Gray: 회색 계열 특수 스타일
 */
export function UILabel({ children, variant = 'badge', intent = 'complete', showIcon = true, className = '' }: UILabelProps) {
  // 공통 스타일
  //const baseStyles = 'inline-flex items-center justify-center transition-colors';

  // variant별 기본 스타일
  // const variantStyles = {
  //   badge: 'rounded min-w-[51px] h-6 px-2', // height: 24px, min-width: 51px, padding: 0 8px
  //   line: 'rounded h-6 px-1.5 border', // height: 24px, padding: 0 6px
  //   solid: 'rounded h-6 px-1.5', // height: 24px, padding: 0 6px
  //   gray: 'rounded h-6 px-1.5', // height: 24px, padding: 0 6px (Figma: 4px 상하, 6px 좌우)
  // };

  // 텍스트 스타일
  // const textStyles = {
  //   badge: 'text-xs font-semibold leading-5', // 12px/20px, font-weight: 600
  //   line: 'text-xs font-normal leading-5', // 12px/20px, font-weight: 400
  //   solid: 'text-xs font-normal leading-5', // 12px/20px, font-weight: 400
  //   gray: 'text-xs font-normal leading-5', // 12px/20px, font-weight: 400
  // };

  // intent별 색상 스타일
  // const getIntentStyles = () => {
  //   if (variant === 'badge') {
  //     switch (intent) {
  //       case 'complete':
  //         return 'bg-[#CBDEFF] text-[#005DF9]';
  //       case 'tag':
  //         return 'bg-[#005DF9] text-white';
  //       case 'progress':
  //         return 'bg-[#C1EAE7] text-[#008479]';
  //       case 'error':
  //         return 'bg-[#FFC3C3] text-[#D61111]';
  //       case 'stop':
  //         return 'bg-[#FFEAB5] text-[#F13800]';
  //       case 'neutral':
  //         return 'bg-[#CECECE] text-[#545454]';
  //       case 'purple':
  //         return 'bg-[#EDDDFF] text-[#7C0BFF]';
  //       case 'choice':
  //         return 'bg-[#EDDDFF] text-[#7C0BFF]';
  //       case 'warning':
  //         return 'bg-[#FFEAB5] text-[#F13800]';
  //       case 'overload':
  //         return 'bg-[#FFC3C3] text-[#D61111]';
  //       default:
  //         return 'bg-[#CBDEFF] text-[#005DF9]';
  //     }
  //   }

  //   if (variant === 'line') {
  //     switch (intent) {
  //       case 'blue':
  //         return 'bg-[#EFF5FF] text-[#005DF9] border-[#C1D8FF]';
  //       case 'black':
  //         return 'bg-gray-100 text-gray-900 border-gray-300';
  //       case 'purple':
  //         return 'bg-[#F7F5FE] text-[#6448BA] border-[#E1D7F9]';
  //       case 'gray-2-outline':
  //         return 'bg-[#F3F6FB] text-[#121315] border-[#DCE2ED]';
  //       default:
  //         return 'bg-[#EFF5FF] text-[#005DF9] border-[#C1D8FF]';
  //     }
  //   }

  //   if (variant === 'solid') {
  //     switch (intent) {
  //       case 'blue':
  //         return 'bg-[#EFF5FF] text-[#005DF9]';
  //       case 'black':
  //         return 'bg-gray-100 text-gray-900';
  //       case 'purple':
  //         return 'bg-[#F7F5FE] text-[#6448BA]';
  //       case 'red':
  //         return 'bg-[#FFF1F1] text-[#D61111]';
  //       case 'green':
  //         return 'bg-[#EBFFFE] text-[#008479]';
  //       case 'gray':
  //         return 'bg-[#EFF5FF] text-gray-550';
  //       default:
  //         return 'bg-[#EFF5FF] text-[#005DF9]';
  //     }
  //   }

  //   if (variant === 'gray') {
  //     switch (intent) {
  //       case 'gray-type1':
  //         return 'bg-[#8b95a9] text-white';
  //       case 'gray-type2':
  //         return 'bg-gray-100 text-gray-600';
  //       default:
  //         return 'bg-[#8b95a9] text-white';
  //     }
  //   }

  //   return '';
  // };

  // span 스타일
  const getElementStyles = () => {
    switch (intent) {
      case 'complete':
        return { className: 'status-label status-medium' };
      case 'tag':
        return { className: 'status-label status-special' };
      case 'progress':
        return { className: 'status-label status-low' };
      case 'error':
        return { className: 'status-label status-critical' };
      case 'stop':
        return { className: 'status-label status-high' };
      case 'neutral':
        return { className: 'status-label status-neutral' };
      case 'purple':
        return { className: 'status-label status-medium' };
      case 'choice':
        return { className: 'status-label status-special' };
      case 'warning':
        return { className: 'status-label status-high2' };
      case 'overload':
        return { className: 'status-label status-warning' };
      default:
        return null;
    }
  };

  // 아이콘 이름 가져오기
  const getIconName = () => {
    if (!showIcon || variant !== 'badge') return null;

    switch (intent) {
      case 'complete':
        return 'ic-status-medium ic-14';
      case 'tag':
        return 'ic-status-special ic-14';
      case 'progress':
        return 'ic-status-low ic-14';
      case 'error':
        return 'ic-status-critical ic-14';
      case 'stop':
        return 'ic-status-high ic-14';
      case 'neutral':
        return 'ic-status-neutral ic-14';
      case 'purple':
        return 'ic-status-medium ic-14';
      case 'choice':
        return 'ic-status-special ic-14';
      case 'warning':
        return 'ic-status-high2 ic-14';
      case 'overload':
        return 'ic-status-warning ic-14';
      default:
        return null;
    }
  };

  const iconName = getIconName();
  const baseStylesObj = getElementStyles();

  return (
    <span className={`${baseStylesObj?.className} ${className}`}>
      {iconName && (
        <>
          <UIIcon2 className={iconName} />
        </>
      )}
      {children}
    </span>
  );
}
