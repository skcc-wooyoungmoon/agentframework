import { useCallback, useEffect, useRef, useState } from 'react';

import { UITypography } from '../../../atoms';
import { UIDropdownList } from '../UIDropdownList/component';

import type { UIDropdownProps } from './types';

const DropdownIcon = ({ isOpen = false, disabled = false }: { isOpen?: boolean; disabled?: boolean }) => {
  if (disabled) {
  }

  return (
    <svg width='24' height='24' viewBox='0 0 24 24' fill='none' className={`transition-transform duration-200 ${isOpen ? 'rotate-180' : ''}`}>
      <path d='M7 10l5 5 5-5' stroke='#121315' strokeWidth='2' strokeLinecap='round' strokeLinejoin='round' />
    </svg>
  );
};

/**
 * UIDropdown 컴포넌트
 *
 * @description 다양한 상태를 지원하는 드랍다운 컴포넌트
 * - 기본 크기: width 100%, height 48px
 * - 테두리 반경: 8px
 * - 5가지 상태 지원: inactive, focused, error, disabled, read
 * - readonly prop 지원: true일 시 read 상태로 변경
 */
export function UIDropdown({
  state = 'inactive',
  variant = 'default',
  placeholder = '옵션을 선택하세요',
  value,
  options,
  refetchOnOpen,
  errorMessage = '오류 발생',
  isOpen = false,
  height = 48,
  width,
  className = '',
  onClick = () => { },
  onSelect,
  showErrorMessage = true,
  label,
  required = false,
  disabled = false,
  readonly = false,
  fontSize,
  color,
}: UIDropdownProps) {
  const dropdownRef = useRef<HTMLDivElement>(null);

  // refetchOnOpen 호출 중 로딩 (options 비어 있을 때만 refetch)
  const [refetching, setRefetching] = useState(false);

  // UIListContentBox.Header 내부에 있는지 자동 감지
  const isInListContainer = useCallback(() => {
    if (!dropdownRef.current) return false;
    return dropdownRef.current.closest('.ui-data-grp-hdr') !== null;
  }, []);

  // 드롭다운 내부 처리
  const [internalIsOpen, setInternalIsOpen] = useState(isOpen);
  const handleClick = useCallback(() => {
    if (refetching) return;
    if (internalIsOpen) {
      setInternalIsOpen(false);
      onClick();
      return;
    }
    // 열 때: refetchOnOpen 있고 options 비어 있으면 먼저 refetch 후 열기
    if (refetchOnOpen && options.length === 0) {
      setRefetching(true);
      Promise.resolve(refetchOnOpen())
        .then(() => {
          setRefetching(false);
          setInternalIsOpen(true);
          onClick();
        })
        .catch(() => {
          setRefetching(false);
        });
    } else {
      setInternalIsOpen(true);
      onClick();
    }
  }, [internalIsOpen, refetching, refetchOnOpen, options.length, onClick]);
  const handleSelect = (value: string) => {
    setInternalIsOpen(false);
    onSelect(value);
  };

  // 외부 클릭 감지하여 드롭다운 닫기
  useEffect(() => {
    if (!internalIsOpen) return;

    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        // onClick 이벤트를 호출하여 드롭다운을 닫습니다
        handleClick();
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [internalIsOpen, onClick, handleClick]);

  const getStyles = () => {
    const heightClass = height === 40 ? 'h-10' : 'h-12';
    const widthClass = width || 'w-full';

    // dataGroup variant일 때는 특별한 스타일 적용
    const textStyle = variant === 'dataGroup' ? 'text-sm font-normal leading-5' : 'font-normal';
    const baseStyles = `${widthClass} ${heightClass} px-4 rounded-lg bg-white border-solid border flex items-center justify-between ${textStyle}`;

    // 열린 상태일 때는 focused로, 닫힌 상태일 때는 원래 상태로
    const stateForStyle = internalIsOpen ? 'focused' : currentState;

    switch (stateForStyle) {
      case 'focused':
        return `${baseStyles} border-blue-800`;
      case 'error':
        return `${baseStyles} border-negative-red`;
      case 'disabled':
        return `${baseStyles} border-gray-300 !bg-[#f3f6fb] cursor-not-allowed`;
      case 'read':
        return `${baseStyles} border-gray-300 !bg-[#f3f6fb] text-gray-700 cursor-not-allowed`;
      default:
        return `${baseStyles} border-gray-300`;
    }
  };

  const getTextColor = () => {
    // color prop이 제공된 경우 우선 사용
    if (color) {
      // 색상이 Tailwind 클래스 형태인지 CSS 색상 값인지 판단
      if (color.startsWith('text-') || color.startsWith('#') || color.startsWith('rgb') || color.startsWith('hsl')) {
        return color;
      }
      // 일반 색상 이름인 경우 CSS 색상으로 반환
      return color;
    }

    // dataGroup variant일 때는 특별한 색상 적용
    if (variant === 'dataGroup') {
      if (currentState === 'disabled') return 'text-[#7E889B]';
      if (currentState === 'read') return 'secondary-neutral-800';
      return 'secondary-neutral-800';
    }

    if (value) {
      if (currentState === 'disabled') return 'text-[#7E889B]';
      if (currentState === 'read') return 'text-gray-700';
      return 'text-gray-800';
    }
    return 'text-gray-550';
  };

  const getDisplayText = () => {
    if (!value) return placeholder;
    const selectedOption = options.find(option => option.value === value);
    return selectedOption?.label || "전체";
  };

  // disabled나 readonly prop이 true면 해당 state로 설정
  const currentState = disabled ? 'disabled' : readonly ? 'read' : state;
  const isInteractive = currentState !== 'disabled' && currentState !== 'read';
  const dropdownState = currentState === 'error' ? 'error' : internalIsOpen ? 'focused' : 'default';

  return (
    <div ref={dropdownRef} className={`w-full ${className}`}>
      {label && (
        <UITypography variant='title-4' className='secondary-neutral-800 text-sb mb-2' required={required}>
          {label}
        </UITypography>
      )}
      <div className='relative'>
        <div className={`${getStyles()} ${isInteractive ? 'cursor-pointer' : ''}`} onClick={isInteractive ? handleClick : undefined}>
          <span
            className={`${getTextColor()} text-base font-normal ui-dropdown-text`}
            style={{
              fontSize: fontSize || (isInListContainer() ? '14px' : '16px'),
              color: color && (color.startsWith('#') || color.startsWith('rgb') || color.startsWith('hsl')) ? color : undefined,
            }}
          >
            {getDisplayText()}
          </span>
          {refetching ? (
            <span className='inline-block h-4 w-4 animate-spin rounded-full border-2 border-gray-300 border-t-blue-800' aria-hidden />
          ) : (
            <DropdownIcon isOpen={internalIsOpen} disabled={currentState === 'disabled'} />
          )}
        </div>
        {internalIsOpen && options.length > 0 && (
          <UIDropdownList
            options={options}
            selectedValue={value}
            state={dropdownState}
            onSelect={handleSelect}
            position='bottom'
            fontSize={(fontSize ? parseInt(fontSize.replace('px', '')) : isInListContainer() ? 14 : 16) as 14 | 16}
          />
        )}
      </div>
      {currentState === 'error' && showErrorMessage && <div className='mt-1 text-sm leading-5 text-negative-red'>{errorMessage}</div>}
    </div>
  );
}
