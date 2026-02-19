import React, { memo, type ReactNode, useCallback, useState } from 'react';
import { ABCollapse } from '@/components/agents/builder/components/ui/ABCollapse';

interface CustomIAccordionItemProps {
  title: ReactNode;
  indicator?: ReactNode;
  children: ReactNode;
  isOpen?: boolean;
  onClick?: () => void;
  defaultOpen?: boolean;
}

const CustomAccordionItemComponent = ({ title, indicator, children, isOpen: controlledIsOpen, onClick: controlledOnClick, defaultOpen = false }: CustomIAccordionItemProps) => {
  const [internalIsOpen, setInternalIsOpen] = useState(defaultOpen);
  const isOpen = controlledIsOpen !== undefined ? controlledIsOpen : internalIsOpen;

  const handleClick = useCallback(
    (e: React.MouseEvent) => {
      e.preventDefault();
      e.stopPropagation();

      if (controlledOnClick) {
        controlledOnClick();
      } else {
        setInternalIsOpen(prev => !prev);
      }
    },
    [controlledOnClick]
  );

  const buildIndicator = () => {
    return (
      indicator || (
        <span className='accordion-indicator'>
          <span className='text-gray-600 text-xs leading-none align-middle'>{isOpen ? '➖' : '➕'}</span>
        </span>
      )
    );
  };

  return (
    <div className={['accordion-item border-b-gray-200 [&:not(:last-child)]:border-b', isOpen && 'active'].filter(e => !!e).join(' ')}>
      <button
        type='button'
        className='accordion-toggle flex w-full cursor-pointer justify-start gap-2.5 py-4 text-left hover:bg-gray-50 focus:outline-none'
        onClick={handleClick}
        aria-expanded={isOpen}
        aria-controls='accordion-content'
      >
        {buildIndicator()}
        <div className='flex-1 min-w-0 text-base text-gray-900'>
          <div className='block truncate'>
            {title}
          </div>
        </div>
      </button>
      <ABCollapse isOpened={isOpen || false}>
        <div className='accordion-content' id='accordion-content'>
          <div className='pt-4 pb-4 text-md text-gray-700'>{children}</div>
        </div>
      </ABCollapse>
    </div>
  );
};

const CustomAccordionItem = memo(CustomAccordionItemComponent);
export { CustomAccordionItem, type CustomIAccordionItemProps };
