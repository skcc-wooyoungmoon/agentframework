import React, { memo, type ReactNode, useCallback, useState } from 'react';
import { ABClassNames, ABCollapse } from '@/components/builder/components/ui';

interface CustomIAccordionItemProps {
  title: ReactNode; // Title of the accordion item
  indicator?: ReactNode; // Optional indicator, like an icon
  children: ReactNode; // Content of the accordion item
  isOpen?: boolean; // To control open/close state of the item
  onClick?: () => void; // Function to handle click event on the item
  defaultOpen?: boolean; // Default open state
}

const CustomAccordionItemComponent = ({ title, indicator, children, isOpen: controlledIsOpen, onClick: controlledOnClick, defaultOpen = false }: CustomIAccordionItemProps) => {
  const [internalIsOpen, setInternalIsOpen] = useState(defaultOpen);

  // Use controlled state if provided, otherwise use internal state
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
    <div className={ABClassNames('accordion-item border-b-gray-200 [&:not(:last-child)]:border-b', isOpen && 'active')}>
      <button
        type='button'
        className='accordion-toggle flex w-full cursor-pointer justify-start gap-2.5 py-4 text-left hover:bg-gray-50 focus:outline-none'
        onClick={handleClick}
        aria-expanded={isOpen}
        aria-controls='accordion-content'
      >
        {buildIndicator()}
        <div className='flex-1 min-w-0 text-base text-gray-900'>
          <div className='block truncate'>{title}</div>
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
