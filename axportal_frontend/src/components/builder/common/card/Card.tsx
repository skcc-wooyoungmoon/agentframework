import { ABClassNames } from '../../components/ui/ABClassNames';
import { type CSSProperties, type ReactNode } from 'react';

interface CardProps {
  children?: ReactNode;
  className?: string;
  style?: CSSProperties;
}

export const Card = ({ children, className, style }: CardProps) => {
  return (
    <div
      className={ABClassNames(
        'card',
        'bg-white rounded-xl border border-gray-200 shadow-sm',
        // 'min-w-[500px] w-[500px]', // 고정 width 설정
        'relative', // Handle 정렬을 위한 position relative 설정
        className
      )}
      style={style}
    >
      {children}
    </div>
  );
};
