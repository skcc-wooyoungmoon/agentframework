/**
 * 리스트 목록 컨테이너
 */
export function UIListContainer({ children, className = 'h-full' }: { children: React.ReactNode; className?: string }) {
  return <div className={'article-grid ' + className}>{children}</div>;
}
