import { type ReactNode } from 'react';

import { ABClassNames } from '@/components/builder/components/ui';

interface CardBodyProps {
  children?: ReactNode;
  className?: string;
}

export const CardBody = ({ children, className }: CardBodyProps) => {
  return (
    <div
      className={ABClassNames(`card-body gap-5`, className)}
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
