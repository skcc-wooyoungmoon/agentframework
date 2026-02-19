import { DefaultTooltip } from '@/components/builder/common/tooltip/Tooltip.tsx';
import { type DataCellProps } from '@/components/builder/types/table.ts';
import React, { useEffect, useRef, useState } from 'react';

// 간단한 InfoBadge 컴포넌트 구현
interface InfoBadgeProps {
  label: string;
  className?: string;
}

const InfoBadge: React.FC<InfoBadgeProps> = ({ label, className = '' }) => {
  const badgeStyle = {
    display: 'inline-flex',
    alignItems: 'center',
    padding: '0.25rem 0.5rem',
    backgroundColor: '#3b82f6',
    color: 'white',
    borderRadius: '0.375rem',
    fontSize: '0.75rem',
    fontWeight: '500',
    lineHeight: '1',
  };

  return (
    <span style={badgeStyle} className={className}>
      {label}
    </span>
  );
};

const DataCell = <T extends object>({ data, field, className, renderAs = 'text', align = 'left', isAllowCopy = false }: DataCellProps<T>) => {
  const [isEllipsisActive, setIsEllipsisActive] = useState(false);
  const textRef = useRef<HTMLDivElement>(null);
  const badgeContainerRef = useRef<HTMLDivElement>(null);
  const value = data[field];

  useEffect(() => {
    const checkEllipsis = () => {
      if (renderAs === 'info-badge' && badgeContainerRef.current) {
        const isEllipsis = badgeContainerRef.current.scrollWidth > badgeContainerRef.current.clientWidth;
        setIsEllipsisActive(isEllipsis);
      } else if (textRef.current) {
        const isEllipsis = textRef.current.scrollWidth > textRef.current.clientWidth;
        setIsEllipsisActive(isEllipsis);
      }
    };

    checkEllipsis();
    window.addEventListener('resize', checkEllipsis);

    return () => {
      window.removeEventListener('resize', checkEllipsis);
    };
  }, [value, renderAs]);

  const handleCopy = (e: React.MouseEvent) => {
    e.stopPropagation();
    navigator.clipboard.writeText(String(value));
  };

  if (value === null) return null;

  if (renderAs === 'info-badge') {
    const badgeContent = (
      <div ref={badgeContainerRef} className='w-full overflow-hidden text-ellipsis whitespace-nowrap'>
        {Array.isArray(value) ? (
          value.map((item, index) => (
            <span key={`${item}-${index}`} className='mr-2 inline-block'>
              <InfoBadge label={String(item)} className={className} />
            </span>
          ))
        ) : (
          <InfoBadge label={String(value)} className={className} />
        )}
      </div>
    );

    if (isEllipsisActive) {
      const tooltipContent = Array.isArray(value) ? value.join(', ') : String(value);
      return (
        <DefaultTooltip title={tooltipContent} placement='top'>
          {badgeContent}
        </DefaultTooltip>
      );
    }

    return badgeContent;
  }

  const content = (
    <div className='flex w-full items-center justify-between gap-2'>
      <div
        ref={textRef}
        className={`truncate ${align === 'center' ? 'text-center' : ''} ${align === 'right' ? 'text-right' : ''} ${align === 'left' ? 'text-left' : ''} ${className || ''} `}
      >
        {String(value)}
      </div>
      {isAllowCopy && (
        <button onClick={handleCopy} className='flex-shrink-0 rounded-full p-1 transition-colors hover:bg-gray-100'>
          <span className='h-4 w-4 text-gray-500 hover:text-gray-700'>📋</span>
        </button>
      )}
    </div>
  );

  if (isEllipsisActive) {
    return (
      <DefaultTooltip title={String(value)} placement='top'>
        {content}
      </DefaultTooltip>
    );
  }

  return content;
};

export { DataCell };
