import type { UIModalType } from '@/components/UI/organisms/modal';
import type { UIBaseModalContainerProps } from './types';

const sizeClasses: Record<UIModalType, string> = {
  '2xsmall': 'w-[384px]',
  xsmall: 'w-[496px]',
  small: 'w-[800px]',
  medium: 'w-[880px]',
  large: 'w-[1102px]',
  xlarge: 'w-[496px]',
  alert: 'w-[384px]',
  confirm: 'w-[385px]',
};

export function UIModalContainer({ type, children, className }: UIBaseModalContainerProps) {
  return <div className={`relative bg-white rounded-2xl shadow-xl max-h-[91vh] flex flex-col ${sizeClasses[type]} ${className}`}>{children}</div>;
}
