import type { UITextLabelProps } from './types';

/**
 * UITextLabel
 * - 그리드에 들어가는 상태 라벨
 */
export function UITextLabel({ intent, children, className = '' }: UITextLabelProps) {
  // 클래스 명
  const buildStatusObj = () => {
    switch (intent) {
      case 'blue':
        return { className: 'text-label blue' };
      case 'gray':
        return { className: 'text-label gray' };
      case 'tag':
        return { className: 'text-label tag' };
      case 'violet':
        return { className: 'text-label violet' };
      case 'red':
        return { className: 'text-label red' };
      default:
        return { className: '' };
    }
  };

  const baseClass = buildStatusObj()?.className || '';
  const combinedClass = className ? `${baseClass} ${className}` : baseClass;

  return <span className={combinedClass}>{children}</span>;
}
