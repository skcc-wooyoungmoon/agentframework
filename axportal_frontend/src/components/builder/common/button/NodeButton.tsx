import React, { useEffect, useRef, useState } from 'react';
import { createPortal } from 'react-dom';

import { UIImage } from '@/components/UI/atoms/UIImage';

interface NodeButtonProps {
  title: string;
  type: string;
  icon: string;
  description: string;
  onClick?: () => void;
  // eslint-disable-next-line no-unused-vars
  onDragStart?: (event: any, type: string) => void;
  disabled?: boolean;
  sidebarRef?: React.RefObject<HTMLDivElement | null>;
}

export const NodeButton = ({ title, type, icon, description, onClick, onDragStart, disabled = false, sidebarRef }: NodeButtonProps) => {
  const [isHovered, setIsHovered] = useState(false);
  const [tooltipPosition, setTooltipPosition] = useState<{ top: number; left: number } | null>(null);
  const buttonRef = useRef<HTMLButtonElement>(null);

  const handleButtonClick = () => {
    if (disabled || !onClick) return;
    onClick();
  };

  const handleDragStart = (event: React.DragEvent<HTMLButtonElement>) => {
    if (disabled || !onDragStart) return;
    onDragStart(event, type);
  };

  // GraphSidebar를 기준으로 버튼 위치 계산
  useEffect(() => {
    if (isHovered && buttonRef.current && sidebarRef?.current) {
      const buttonRect = buttonRef.current.getBoundingClientRect();
      const sidebarRect = sidebarRef.current.getBoundingClientRect();

      setTooltipPosition({
        top: buttonRect.bottom + 8,
        left: sidebarRect.right, // sidebar의 오른쪽 끝을 기준으로
      });
    } else {
      setTooltipPosition(null); // hover가 끝나면 위치 초기화
    }
  }, [isHovered, sidebarRef]);

  const getIcon = (iconName: string) => {
    switch (iconName) {
      case 'ki-bookmark':
        // return '📝';
        return <UIImage src='/assets/images/builder/ico-system-20-builder.svg' alt='builder' className='w-[20px] h-[20px]' />;
      case 'ki-questionnaire-tablet':
        // return '📥';
        return <UIImage src='/assets/images/builder/ic-input-20.svg' alt='questionnaire' className='w-[20px] h-[20px]' />;
      case 'ki-message-programming':
        // return '🔑';
        return <UIImage src='/assets/images/builder/ic-output-keys-20.svg' alt='programming' className='w-[20px] h-[20px]' />;
      case 'ki-message-text-2':
        // return '💬';
        return <UIImage src='/assets/images/builder/ic-output-chat-20.svg' alt='message' className='w-[20px] h-[20px]' />;
      case 'ki-technology-1':
        // return '🤖';
        return <UIImage src='/assets/images/builder/ic-generator-20.svg' alt='technology' className='w-[20px] h-[20px]' />;
      case 'ki-square-brackets':
        // return '💻';
        return <UIImage src='/assets/images/builder/ic-code-20.svg' alt='code' className='w-[20px] h-[20px]' />;
      case 'ki-category':
        // return '📊';
        return <UIImage src='/assets/images/builder/ic-categorizer-20.svg' alt='category' className='w-[20px] h-[20px]' />;
      case 'ki-abstract-26':
        // return '🔄';
        return <UIImage src='/assets/images/builder/ic-doc-compressor-20.svg' alt='abstract' className='w-[20px] h-[20px]' />;
      case 'ki-ranking':
        // return '📈';
        return <UIImage src='/assets/images/builder/ic-doc-reranker-20.svg' alt='DOC ReRanker' className='w-[20px] h-[20px]' />;
      case 'ki-filter-tablet':
        // return '🔍';
        return <UIImage src='/assets/images/builder/ic-doc-filter-20.svg' alt='DOC Filter' className='w-[20px] h-[20px]' />;
      case 'ki-wrench':
        // return '🔧';
        return <UIImage src='/assets/images/builder/ic-tool-20.svg' alt='tool' className='w-[20px] h-[20px]' />;
      default:
        // return '📋';
        return <UIImage src='/assets/images/builder/ic-rewriter-20.svg' alt='rewriter' className='w-[20px] h-[20px]' />;
    }
  };

  return (
    <>
      <button
        ref={buttonRef}
        className={`group relative w-full rounded-lg border border-gray-200 px-3 py-2 text-left shadow-sm transition-all duration-200 ${
          disabled ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-white hover:border-blue-300 hover:bg-blue-50 hover:shadow-md active:scale-95'
        }`}
        onClick={handleButtonClick}
        draggable={!disabled}
        onDragStart={handleDragStart}
        onMouseEnter={() => setIsHovered(true)}
        onMouseLeave={() => setIsHovered(false)}
        disabled={disabled}
      >
        <div className='flex items-center gap-2'>
          {' '}
          {/* gap-3에서 gap-2로 줄임 */}
          {/* <div className='flex h-6 w-6 items-center justify-center rounded-md bg-blue-100 text-blue-600 group-hover:bg-blue-200'> */}
          <div className='flex W-[20px] h-[20px] items-center justify-center'>
            {' '}
            {/* h-8 w-8에서 h-6 w-6으로 줄임 */}
            {/* <div className={`flex h-8 w-8 items-center justify-center rounded-md ${
            disabled ? "bg-gray-200 text-gray-400" : "bg-blue-100 text-blue-600 group-hover:bg-blue-200"
          }`}> */}
            {typeof getIcon(icon) === 'string' ? <span className='text-xs'>{getIcon(icon)}</span> : getIcon(icon)}
          </div>
          <div className='flex-1'>
            <div className='font-medium text-xs text-gray-900 group-hover:text-blue-700'>
              {' '}
              {/* text-sm에서 text-xs로 줄임 */}
              {/* <div className={`font-medium ${
              disabled ? "text-gray-400" : "text-gray-900 group-hover:text-blue-700"
            }`}> */}
              {title}
            </div>
          </div>
          <div className='opacity-0 transition-opacity group-hover:opacity-100'>
            {/* {!disabled && (
            <div className="opacity-0 transition-opacity group-hover:opacity-100">
          )} */}
            <span className='text-xs text-gray-400'>드래그</span>
          </div>
        </div>
      </button>

      {/* builder-tooltip 활용한 description 툴팁 - Portal로 렌더링 */}
      {isHovered &&
        !disabled &&
        description &&
        tooltipPosition &&
        createPortal(
          <div
            className='builder-tooltip'
            style={{
              position: 'fixed',
              top: tooltipPosition.top + 'px',
              left: `calc(${tooltipPosition.left}px + 50px)`,
              transform: 'translateX(-50%)',
            }}
          >
            <div className='tooltip-content'>
              <p className='tooltip-content-text'>{description}</p>
            </div>
          </div>,
          document.body
        )}
    </>
  );
};
