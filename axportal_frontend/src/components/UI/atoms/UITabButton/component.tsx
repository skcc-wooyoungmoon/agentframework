import { UITypography } from '../UITypography';

import type { UITabButtonProps } from './types';

/**
 * TabButton 컴포넌트 (Atomic Design: atom)
 * - 단일 탭 버튼 요소
 * - 스타일 정의, 텍스트 표시, 클릭 이벤트 처리
 * - 상태 관리 없음 (부모에서 전달받은 props만 사용)
 */
export function UITabButton({ label, isActive, disabled = false, size = 'large', variant = 'default', onClick, className = '' }: UITabButtonProps) {
  const handleClick = () => {
    if (disabled || !onClick) return;
    onClick();
  };

  const getButtonStyles = () => {
    const baseStyles = `
      relative inline-flex items-center justify-center
      text-center transition-colors duration-200
    `;

    // 크기별 스타일 - 너비 제한 제거
    const sizeStyles = size === 'large' ? 'h-[48px] px-4' : 'h-[40px] px-3';

    // 양옆 마진 8px
    const marginStyles = 'mx-2';

    // 상태별 스타일
    let stateStyles = '';
    if (disabled) {
      stateStyles = 'text-gray-400 cursor-not-allowed';
    } else if (isActive) {
      stateStyles = `
        text-blue-800 ${variant === 'body-2' ? '' : 'font-bold'}
        cursor-pointer
        relative z-10
      `;
    } else {
      stateStyles = `
        text-gray-500 ${variant === 'body-2' ? '' : 'font-semibold'}
        cursor-pointer
        hover:text-gray-700
        relative
      `;
    }

    return `${baseStyles} ${sizeStyles} ${marginStyles} ${stateStyles}`.replace(/\s+/g, ' ').trim();
  };

  const getTextStyles = () => {
    const textStyles = size === 'large' ? 'text-[18px] leading-[26px]' : 'text-[16px] leading-[24px]';

    return `${textStyles} whitespace-nowrap block`.replace(/\s+/g, ' ').trim();
  };

  return (
    <button
      type='button'
      role='tab'
      aria-selected={isActive}
      aria-disabled={disabled}
      tabIndex={disabled ? -1 : 0}
      className={`${getButtonStyles()} ${className}`}
      onClick={handleClick}
    >
      {variant === 'body-2' ? (
        <UITypography variant='body-2' className='whitespace-nowrap' style={{ fontWeight: 600 }}>
          {label}
        </UITypography>
      ) : (
        <span className={getTextStyles()}>{label}</span>
      )}
    </button>
  );
}
