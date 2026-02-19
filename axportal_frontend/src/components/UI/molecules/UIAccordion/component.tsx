import { useState, useCallback, useRef, useEffect } from 'react';

import { UIIcon2 } from '../../atoms/UIIcon2';

import type { UIAccordionProps, UIAccordionItemComponentProps } from './types';
import { UITypography } from '../../atoms';

/**
 * 아코디언 아이템 컴포넌트
 */
function UIAccordionItem({
  title,
  titleSub,
  content,
  index,
  isOpen,
  variant = 'default',
  disabled = false,
  icon,
  showNoticeIcon = true,
  isNoticeContent = false,
  noticeType = 'info',
  actionButton,
  arrowPosition = 'left',
  onToggle,
}: UIAccordionItemComponentProps) {
  const contentRef = useRef<HTMLDivElement>(null);
  const [hasScroll, setHasScroll] = useState(false);

  const handleToggle = useCallback(() => {
    if (!disabled) {
      onToggle(index);
    }
  }, [disabled, index, onToggle]);

  useEffect(() => {
    if (isOpen && variant === 'box' && contentRef.current) {
      // DOM 렌더링 완료 후 스크롤 상태 확인
      setTimeout(() => {
        if (contentRef.current) {
          const element = contentRef.current;
          const hasScrollContent = element.scrollHeight > element.clientHeight;
          setHasScroll(hasScrollContent);
        }
      }, 100);
    } else {
      setHasScroll(false);
    }
  }, [isOpen, variant, content]);

  const getItemStyles = () => {
    const baseStyles = 'transition-all duration-200';

    switch (variant) {
      case 'box':
        return `${baseStyles} bg-white border border-gray-300 rounded-2xl`;
      case 'small':
        return `${baseStyles}`;
      default:
        return `${baseStyles}`;
    }
  };

  const getHeaderStyles = () => {
    const baseStyles = 'w-full flex items-start justify-between text-left transition-colors duration-200';
    const paddingStyles = variant === 'box' ? 'px-8 py-5' : variant === 'small' ? 'py-2' : 'py-0';
    const heightStyles = variant === 'default' ? 'min-h-[32px]' : variant === 'box' ? 'min-h-[64px]' : '';
    const disabledStyles = disabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer';

    return `${baseStyles} ${paddingStyles} ${heightStyles} ${disabledStyles}`;
  };

  const getTitleStyles = () => {
    switch (variant) {
      case 'box':
        return 'font-semibold text-base text-[#121315]';
      case 'small':
        return 'font-semibold text-caption-2 text-gray-800';
      default:
        return 'font-bold text-[22px] leading-[32px] text-[#121315]';
    }
  };

  const getContentStyles = () => {
    const baseStyles = 'transition-transform duration-200 ease-in-out overflow-hidden';
    let contentStyles = 'pb-2';

    if (variant === 'box') {
      contentStyles = isOpen ? 'px-8 pt-4 pb-5' : ''; // 열렸을 때 좌우 패딩과 상단 패딩 추가
    } else if (variant === 'small') {
      contentStyles = 'pl-8 pb-2'; // pl-8는 아이콘(24px) + gap(8px)
    }

    return `${baseStyles} ${contentStyles}`;
  };

  const getArrowIcon = () => {
    const iconName = 'ic-system-24-outline-small-down';

    return <UIIcon2 className={`${iconName} transition-transform duration-200 ${isOpen ? 'rotate-flip' : ''}`} />;
  };

  return (
    <div className={getItemStyles()}>
      <button type='button' className={getHeaderStyles()} onClick={handleToggle} disabled={disabled} aria-expanded={isOpen}>
        {variant === 'box' && (
          <>
            <div className='flex-1'>
              {arrowPosition === 'right' ? (
                <div className='flex items-center justify-between'>
                  <span className={getTitleStyles()}>{title}</span>
                  {icon ? <UIIcon2 className={icon} /> : getArrowIcon()}
                </div>
              ) : (
                <div className='flex items-center gap-4'>
                  {icon ? <UIIcon2 className={icon} /> : getArrowIcon()}
                  <span className={getTitleStyles()}>{title}</span>
                </div>
              )}
              {titleSub && (
                <div className='mt-1 ml-10'>
                  <UITypography variant='body-1' className='secondary-neutral-600'>
                    {titleSub}
                  </UITypography>
                </div>
              )}
            </div>
            <div className='flex items-center justify-center gap-2'>
              {showNoticeIcon && <UIIcon2 className='ic-system-24-delete' />}
              {actionButton && <div onClick={e => e.stopPropagation()}>{actionButton}</div>}
            </div>
          </>
        )}
        {variant === 'small' && (
          <div className='flex items-center gap-2'>
            {getArrowIcon()}
            <span className={getTitleStyles()}>{title}</span>
          </div>
        )}
        {variant === 'default' && (
          <>
            <span className={getTitleStyles()}>{title}</span>
            {getArrowIcon()}
          </>
        )}
      </button>

      {isOpen && variant === 'box' && arrowPosition !== 'right' && <div className='border-t border-gray-200 mx-8' />}
      <div
        ref={contentRef}
        className={`${getContentStyles()} ${
          isOpen ? (variant === 'box' ? 'opacity-100' : 'max-h-screen opacity-100') : 'max-h-0 opacity-0'
        } ${variant === 'box' && isOpen ? 'overflow-y-auto relative' : ''}`}
      >
        {isNoticeContent ? (
          <div className='flex items-start gap-1.5'>
            <div className='flex-shrink-0 flex items-center h-5'>
              <UIIcon2
                className={`${noticeType === 'warning' ? 'ic-system-16-info-red' : 'ic-system-16-info-gray'} ${noticeType === 'warning' ? 'text-negative-red' : 'text-gray-600'}`}
              />
            </div>
            <span className={`text-sm ${noticeType === 'warning' ? 'text-negative-red' : 'text-gray-600'} font-normal`} style={{ fontSize: '14px', lineHeight: '20px' }}>
              {content}
            </span>
          </div>
        ) : (
          <div
            className={
              variant === 'small'
                ? 'text-caption-2 text-gray-600'
                : variant === 'box'
                  ? 'text-base text-[#242A34] leading-6 whitespace-pre-line'
                  : 'text-body-3 text-gray-700'
            }
            style={
              variant === 'box'
                ? {
                    fontSize: '16px',
                    lineHeight: '24px',
                    letterSpacing: '-0.08px',
                    fontWeight: 400,
                    fontFamily: 'Pretendard',
                  }
                : undefined
            }
            contentEditable={false}
            suppressContentEditableWarning={true}
          >
            {typeof content === 'string' && variant === 'box'
              ? content.split('\n').map((line, index) => {
                  // 피그마에서 확인한 리스트 형태 처리
                  const isListItem = line.startsWith(' ') || line.includes('여신 실행 시') || line.includes('여신 실행 이후');

                  if (isListItem) {
                    return (
                      <div key={index} className='flex items-start gap-2 ml-4'>
                        <span
                          className='text-[#242A34]'
                          style={{
                            fontSize: '16px',
                            lineHeight: '24px',
                            letterSpacing: '-0.08px',
                          }}
                        >
                          {line.trim()}
                        </span>
                      </div>
                    );
                  }
                  return (
                    <div key={index} className={line.trim() ? 'mb-1' : 'txt-wrap'}>
                      {line.trim()}
                    </div>
                  );
                })
              : content}
          </div>
        )}

        {/* 스크롤 시 하단 그라디언트 */}
        {variant === 'box' && isOpen && hasScroll && !titleSub && (
          <div
            className='absolute bottom-0 left-8 right-8 h-[34px] pointer-events-none'
            style={{
              background: 'linear-gradient(180deg, rgba(255, 255, 255, 0) 0%, #FFFFFF 100%)',
            }}
          />
        )}
      </div>
    </div>
  );
}

/**
 * 아코디언 컴포넌트 (Atomic Design: molecule)
 * - 접을 수 있는 콘텐츠 패널 목록
 * - 세 가지 변형 지원: default, box, small
 * - 단일/다중 선택 지원
 * - 키보드 접근성 지원
 */
export function UIAccordion({ items, variant = 'default', allowMultiple = false, className = '', onChange }: UIAccordionProps) {
  const [openItems, setOpenItems] = useState<number[]>(() => {
    return items
      .map((item, index) => ({ item, index }))
      .filter(({ item }) => item.defaultOpen)
      .map(({ index }) => index);
  });

  const handleToggle = useCallback(
    (index: number) => {
      setOpenItems(prev => {
        let newOpenItems: number[];

        if (allowMultiple) {
          newOpenItems = prev.includes(index) ? prev.filter(i => i !== index) : [...prev, index];
        } else {
          newOpenItems = prev.includes(index) ? [] : [index];
        }

        onChange?.(newOpenItems);
        return newOpenItems;
      });
    },
    [allowMultiple, onChange]
  );

  const getContainerStyles = () => {
    const baseStyles = 'w-full';

    switch (variant) {
      case 'box':
        return `${baseStyles} space-y-4`;
      case 'small':
        return `${baseStyles}`;
      default:
        return `${baseStyles}`;
    }
  };

  return (
    <div className={`${getContainerStyles()} ${className}`}>
      {items.map((item, index) => (
        <UIAccordionItem key={index} {...item} index={index} isOpen={openItems.includes(index)} variant={variant} onToggle={handleToggle} />
      ))}
    </div>
  );
}
