import { type ReactNode } from 'react';

interface CardFooterProps {
  children?: ReactNode;
  className?: string;
}

export const CardFooter = ({ children, className }: CardFooterProps) => {
  return (
    <div
      className={[`card-footer pb-2 pt-5`, className].filter(e => !!e).join(' ')}
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
