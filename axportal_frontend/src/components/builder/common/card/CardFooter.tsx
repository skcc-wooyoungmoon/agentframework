import { ABClassNames } from '@/components/builder/components/ui';
import { type ReactNode } from 'react';

interface CardFooterProps {
  children?: ReactNode;
  className?: string;
}

export const CardFooter = ({ children, className }: CardFooterProps) => {
  return (
    <div
      className={ABClassNames(`card-footer pb-2 pt-5`, className)}
      style={{
        padding: '20px 20px 20px 20px',
        backgroundColor: 'white',
        borderTop: '1px solid #f3f4f6',
      }}
    >
      {children}
    </div>
  );
};
