import { Children, cloneElement, isValidElement, memo, type ReactNode, useState } from 'react';
import { type IAccordionItemProps } from './AccordionItem.tsx';

interface IAccordionProps {
  className?: string;
  children: ReactNode;
  allowMultiple?: boolean;
}

const AccordionComponent = ({ className, children, allowMultiple }: IAccordionProps) => {
  const [openIndex, setOpenIndex] = useState<number | null>(null);

  const handleItemClick = (index: number) => {
    setOpenIndex(prevIndex => (prevIndex === index ? null : index));
  };

  const modifiedChildren = Children.map(children, (child, index) => {
    if (isValidElement<IAccordionItemProps>(child)) {
      return cloneElement<IAccordionItemProps>(child, {
        isOpen: allowMultiple ? child.props.isOpen : openIndex === index,
        onClick: () => handleItemClick(index),
      });
    }
    return child;
  });

  return <div className={['accordion', className].filter(e => !!e).join(' ')}>{modifiedChildren}</div>;
};

const Accordion = memo(AccordionComponent);
export { Accordion, type IAccordionProps };
