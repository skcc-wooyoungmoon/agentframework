import { type CSSProperties, type ReactNode } from 'react';

interface CardProps {
  children?: ReactNode;
  className?: string;
  style?: CSSProperties;
}

export const Card = ({ children, className, style }: CardProps) => {
  return (
    <div
      className={['card', 'bg-white rounded-xl border border-gray-200 shadow-sm',
        'relative',
        className].filter(e => !!e).join(' ')
      }
      style={style}
    >
      {children}
    </div>
  );
};
