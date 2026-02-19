import type { UISkeletonProps } from './types';

export function UISkeleton({ variant = 'text', width = '100%', height = variant === 'text' ? 16 : 40, animate = true, className = '', rowIndex }: UISkeletonProps) {
  const variantClass = `skeleton-${variant}`;
  const isEvenRow = rowIndex !== undefined && rowIndex % 2 === 0;

  const style = {
    width: typeof width === 'number' ? `${width}px` : width,
    height: typeof height === 'number' ? `${height}px` : height,
    backgroundColor: isEvenRow ? '#ffffff' : undefined,
  };

  // console.log('isEvenRow:', isEvenRow, 'rowIndex:', rowIndex);
  if (rowIndex === undefined) {
    const animateClass = animate ? 'skeleton-animate' : '';
    return <div className={`skeleton ${variantClass} ${animateClass} ${className}`.trim()} style={style} />;
  }

  // 홀수일 때: skeleton, skeleton-animate 클래스 제거
  if (!isEvenRow) {
    return <div className={`skeleton-${variant} ${className}`.trim()} style={style} />;
  }

  // 짝수일 때: 모든 클래스 포함
  const animateClass = animate ? 'skeleton-animate' : '';
  return <div className={`skeleton ${variantClass} ${animateClass} ${className}`.trim()} style={style} />;
}
