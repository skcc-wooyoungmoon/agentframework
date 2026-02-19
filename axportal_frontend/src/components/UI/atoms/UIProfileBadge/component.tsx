import type { UIProfileBadgeProps } from './types';
import { stringUtils } from '@/utils/common';

export const UIProfileBadge: React.FC<UIProfileBadgeProps> = ({ name, bgColor = '#3B82F6', textColor = '#FFFFFF', onClick, className = '', size = 'medium' }) => {
  const sizeClasses = {
    small: 'w-10 h-10 text-sm',
    medium: 'w-[38px] h-[38px] text-body-2',
  };

  const Component = onClick ? 'button' : 'div';

  return (
    <Component
      type={onClick ? 'button' : undefined}
      onClick={onClick}
      className={
        'inline-flex items-center justify-center rounded-full font-normal ' +
        (onClick ? 'cursor-pointer transition-opacity hover:opacity-80 ' : '') +
        sizeClasses[size] +
        ' ' +
        className
      }
      style={{
        backgroundColor: bgColor,
        color: textColor,
      }}
      title={name}
    >
      <span className='text-body-2'>{stringUtils.getProfileIconString(name)}</span>
    </Component>
  );
};
