import { type ReactNode } from 'react';

interface CardBodyProps {
  children?: ReactNode;
  className?: string;
}

export const CardBody = ({ children, className }: CardBodyProps) => {
  return (
    <div
      className={[`card-body gap-5`, className].filter(e => !!e).join(' ')}
      style={{
        padding: '20px',
        paddingTop: '0px',
        backgroundColor: 'white',
      }}
    >
      {children}
    </div>
  );
};
