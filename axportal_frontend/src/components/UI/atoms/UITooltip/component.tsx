import React, { useState, useRef, useEffect, useCallback } from 'react';

import { UIImage } from '../UIImage';
import type { UITooltipProps } from './types';

export const UITooltip: React.FC<UITooltipProps> = ({
  children,
  type = 'notice',
  title,
  items,
  content,
  bulletType = 'circle',
  position = 'auto',
  showArrow = true,
  showCloseButton = true,
  className = '',
  tooltipClassName = '',
  disabled = false,
  trigger = 'click',
  visible,
  onVisibleChange,
  onClose,
}) => {
  const [isVisible, setIsVisible] = useState(false);
  const [tooltipPosition, setTooltipPosition] = useState(position);
  const triggerRef = useRef<HTMLDivElement>(null);
  const tooltipRef = useRef<HTMLDivElement>(null);

  // 수동 모드에서 visible prop 사용
  const actualVisible = trigger === 'manual' ? visible : isVisible;

  // 툴팁 위치 자동 계산
  const calculatePosition = useCallback(() => {
    if (position !== 'auto' || !triggerRef.current || !tooltipRef.current) {
      return position;
    }

    const triggerRect = triggerRef.current.getBoundingClientRect();
    const tooltipRect = tooltipRef.current.getBoundingClientRect();
    const viewport = {
      width: window.innerWidth,
      height: window.innerHeight,
    };

    // 각 방향별 여유 공간 계산
    const spaceTop = triggerRect.top;
    const spaceBottom = viewport.height - triggerRect.bottom;
    const spaceLeft = triggerRect.left;
    const spaceRight = viewport.width - triggerRect.right;

    // 우선순위: top > bottom > right > left
    if (spaceTop >= tooltipRect.height + 10) return 'top';
    if (spaceBottom >= tooltipRect.height + 10) return 'bottom';
    if (spaceRight >= tooltipRect.width + 10) return 'right';
    if (spaceLeft >= tooltipRect.width + 10) return 'left';

    return 'top'; // 기본값
  }, [position]);

  // 툴팁 표시/숨김 처리
  const showTooltip = () => {
    if (disabled) return;

    setIsVisible(true);
    onVisibleChange?.(true);
  };

  const hideTooltip = () => {
    setIsVisible(false);
    onVisibleChange?.(false);
  };

  const toggleTooltip = () => {
    if (actualVisible) {
      hideTooltip();
    } else {
      showTooltip();
    }
  };

  const handleClose = () => {
    hideTooltip();
    onClose?.();
  };

  // 위치 업데이트
  useEffect(() => {
    if (actualVisible) {
      const newPosition = calculatePosition();
      setTooltipPosition(newPosition);
    }
  }, [actualVisible, position, calculatePosition]);

  // 이벤트 핸들러
  const handleMouseEnter = () => {
    if (trigger === 'hover') showTooltip();
  };

  const handleMouseLeave = () => {
    if (trigger === 'hover') hideTooltip();
  };

  const handleClick = () => {
    if (trigger === 'click') toggleTooltip();
  };

  const handleFocus = () => {
    if (trigger === 'focus') showTooltip();
  };

  const handleBlur = () => {
    if (trigger === 'focus') hideTooltip();
  };

  // 타입별 기본 스타일 클래스
  const getTypeClass = () => {
    if (type === 'grid') return 'bg-gray-900 text-white shadow-lg';
    if (type === 'gridHover') return 'bg-white border-gray-300 shadow-lg';
    return 'bg-white border-gray-300 shadow-lg';
  };

  // 위치별 스타일 클래스
  const positionClasses = {
    top: 'bottom-full left-1/2 transform -translate-x-1/2 mb-2',
    'top-start': 'bottom-full left-0 mb-2',
    'top-end': 'bottom-full right-0 mb-2',
    bottom: 'top-full left-1/2 transform -translate-x-1/2 mt-2',
    'bottom-start': 'top-full left-0 mt-2',
    'bottom-end': 'top-full right-0 mt-2',
    left: 'right-full top-1/2 transform -translate-y-1/2 mr-2',
    right: 'left-full top-1/2 transform -translate-y-1/2 ml-2',
  } as const;

  // 툴팁 너비 클래스
  const getWidthClass = () => {
    switch (type) {
      case 's':
        return 'w-fit max-w-[360px]' + ' min-w-[167px]';
      case 's-fixed':
        return 'w-fit max-w-none whitespace-nowrap';
      case 'info':
        return 'w-[400px] max-w-[360px]';
      case 'title-notice-l':
        return 'w-[536px] max-w-[360px]';
      case 'grid':
        return 'min-w-fit max-w-[360px] whitespace-nowrap';
      case 'gridHover':
        return 'w-[400px] max-w-[360px]';
      default:
        return 'w-[400px] max-w-[360px]';
    }
  };

  // 불릿 렌더링 함수
  const renderBullet = () => {
    switch (bulletType) {
      case 'dash':
        return (
          <div className='flex-shrink-0 flex items-center h-5'>
            <div className='w-1 h-px bg-gray-600'></div>
          </div>
        );
      case 'default':
        return null;
      case 'circle':
      default:
        return (
          <div className='flex-shrink-0 flex items-center h-5'>
            <div className='w-1 h-1 bg-gray-600 rounded-full'></div>
          </div>
        );
    }
  };

  // 화살표 SVG 컴포넌트
  const renderArrow = () => {
    if (!showArrow) return null;

    const arrowClasses = {
      top: 'top-full left-1/2 transform -translate-x-1/2 -translate-y-px rotate-180',
      'top-start': 'top-full left-3 transform -translate-y-px rotate-180',
      'top-end': 'top-full right-3 transform -translate-y-px rotate-180',
      bottom: 'bottom-full left-1/2 transform -translate-x-1/2 translate-y-px',
      'bottom-start': 'bottom-full left-3 transform translate-y-px',
      'bottom-end': 'bottom-full right-3 transform translate-y-px',
      left: 'left-full top-1/2 transform -translate-y-1/2 translate-x-px rotate-90',
      right: 'right-full top-1/2 transform -translate-y-1/2 -translate-x-px -rotate-90',
    } as const;

    const basePosition = tooltipPosition?.replace(/-start|-end/, '') as 'top' | 'bottom' | 'left' | 'right';

    return (
      <div
        className={
          'absolute z-40 ' + (arrowClasses[tooltipPosition as keyof typeof arrowClasses] || arrowClasses[basePosition as keyof typeof arrowClasses] || arrowClasses.bottom)
        }
      >
        <UIImage src='/assets/images/system/ico-system-arrow-tooltip.svg' alt='tooltip arrow' width={12} height={6} />
      </div>
    );
  };

  return (
    <div
      ref={triggerRef}
      className={'relative inline-block ' + className}
      onMouseEnter={handleMouseEnter}
      onMouseLeave={handleMouseLeave}
      onClick={handleClick}
      onFocus={handleFocus}
      onBlur={handleBlur}
    >
      {children}

      {actualVisible && (
        <div
          ref={tooltipRef}
          onClick={e => e.stopPropagation()}
          className={
            'absolute z-50 rounded-lg ' +
            (type === 'grid' ? '' : 'border ') +
            getTypeClass() +
            ' ' +
            (positionClasses[tooltipPosition as keyof typeof positionClasses] || positionClasses.top) +
            ' ' +
            getWidthClass() +
            ' ' +
            tooltipClassName +
            ' ' +
            (type === 's'
              ? 'px-4 py-3'
              : type === 's-fixed'
                ? 'h-[56px] pl-4 pr-12 py-[18px]'
                : type === 'grid'
                  ? 'p-0'
                  : type === 'gridHover'
                    ? 'px-4 py-3'
                    : type === 'title-notice-l'
                      ? 'p-4'
                      : 'p-4')
          }
        >
          {/* 닫기 버튼 */}
          {showCloseButton && type !== 'grid' && type !== 'gridHover' && (
            <button
              onClick={e => {
                e.preventDefault();
                e.stopPropagation();
                handleClose();
              }}
              className={'absolute w-6 h-6 flex items-center justify-center cursor-pointer ' + (type === 's-fixed' ? 'top-4 right-4' : 'top-3 right-2')}
              type='button'
              aria-label='닫기'
            >
              <svg width='24' height='24' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'>
                <path d='M17 7L7 17M7 7L17 17' className='stroke-gray-900' strokeWidth='1.5' strokeLinecap='round' strokeLinejoin='round' />
              </svg>
            </button>
          )}

          {/* 타입별 콘텐츠 렌더링 */}
          {(type === 's' || type === 's-fixed') && content && (
            <div className={'text-sm text-gray-600 leading-5' + (showCloseButton && type !== 's-fixed' ? ' pr-5' : '')}>{content}</div>
          )}

          {type === 'info' && (
            <div className='flex items-start'>
              <div className='mr-3 mt-1 flex-shrink-0'>
                <svg width='16' height='16' viewBox='0 0 16 16' fill='none' xmlns='http://www.w3.org/2000/svg'>
                  <circle cx='8' cy='8' r='8' className='fill-gray-400' />
                  <path d='M8 7V11M8 5H8.01' stroke='white' strokeWidth='1.5' strokeLinecap='round' strokeLinejoin='round' />
                </svg>
              </div>
              <div className={'text-sm leading-5 text-gray-550 break-words' + (showCloseButton ? ' pr-5' : '')}>{content || '안내 문구의 Icon, gray 타입입니다.'}</div>
            </div>
          )}

          {(type === 'notice' || type === 'title-notice') && (
            <div>
              {/* 제목 */}
              {title && <div className={'text-gray-900 mb-3 text-base font-semibold break-words' + (showCloseButton ? ' pr-6' : '')}>{title}</div>}

              {/* 리스트 아이템들 */}
              {items && items.length > 0 && (
                <ul className={(bulletType !== 'default' ? 'ui-list-dash' : '') + (showCloseButton && !title ? ' pr-6' : '')}>
                  {items.map((item, index) => (
                    <li key={index} className='flex items-start text-sm leading-5 font-normal gap-2' style={{ color: '#576072' }}>
                      {renderBullet()}
                      <span className='break-words'>{item}</span>
                    </li>
                  ))}
                </ul>
              )}

              {/* notice 타입에서 content만 있는 경우 */}
              {type === 'notice' && content && !items && <div className='text-sm leading-5 text-gray-550 break-words'>{content}</div>}
            </div>
          )}

          {/* title-notice-l 타입 (Large) */}
          {type === 'title-notice-l' && (
            <div>
              {/* 제목 */}
              {title && <div className={'font-bold text-gray-900 mb-4 text-[13px] leading-5 break-words' + (showCloseButton ? ' pr-6' : '')}>{title}</div>}

              {/* 리스트 아이템들 */}
              {items && items.length > 0 && (
                <ul className={(bulletType !== 'default' ? 'ui-list-dash ' : '') + 'space-y-3' + (showCloseButton && !title ? ' pr-6' : '')}>
                  {items.map((item, index) => (
                    <li key={index} className='flex items-start text-sm leading-5 text-gray-550 leading-relaxed gap-2'>
                      {renderBullet()}
                      <span className='break-words'>{item}</span>
                    </li>
                  ))}
                </ul>
              )}

              {/* content만 있는 경우 */}
              {content && !items && <div className='text-sm leading-5 text-gray-550 break-words leading-relaxed'>{content}</div>}
            </div>
          )}

          {/* grid 타입 콘텐츠 */}
          {type === 'grid' && content && <div className='text-sm leading-5 text-white px-2 py-1'>{content}</div>}

          {/* gridHover 타입 콘텐츠 */}
          {type === 'gridHover' && content && (
            <div className='flex items-start gap-3'>
              {/* 점 표시 */}
              <div className='flex-shrink-0 mt-2'>
                <div className='w-1 h-1 bg-gray-600 rounded-full'></div>
              </div>
              {/* 텍스트 내용 */}
              <div className='text-sm text-gray-600 leading-5'>{content}</div>
            </div>
          )}

          {/* 화살표 */}
          {type !== 'grid' && type !== 'gridHover' && renderArrow()}
        </div>
      )}
    </div>
  );
};
