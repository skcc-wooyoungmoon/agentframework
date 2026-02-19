import { ABCollapse } from '../../components/ui/ABCollapse';
import { memo, type ReactNode } from 'react';

interface IAccordionItemProps {
  title: string;
  indicator?: ReactNode;
  children: ReactNode;
  isOpen?: boolean;
  onClick?: () => void;
}

const AccordionItemComponent = ({ title, indicator, children, isOpen, onClick }: IAccordionItemProps) => {
  const buildIndicator = () => {
    return (
      indicator || <span className='accordion-indicator'>{isOpen ? <span className='text-gray-600 text-sm'>➖</span> : <span className='text-gray-600 text-sm'>➕</span>}</span>
    );
  };

  return (
    <div className={['accordion-item [&:not(:last-child)]:border-b border-b-gray-200', isOpen && 'active'].filter(e => !!e).join(' ')}>
      <button type='button' className='accordion-toggle py-4 cursor-pointer' onClick={onClick}>
        <span className='text-base text-gray-900'>{title}</span>
        {buildIndicator()}
      </button>
      <ABCollapse isOpened={isOpen || false}>
        <div className='accordion-content'>
          <div className='text-gray-700 text-md pb-4'>{children}</div>
        </div>
      </ABCollapse>
    </div>
  );
};

const AccordionItem = memo(AccordionItemComponent);
export { AccordionItem, type IAccordionItemProps };
