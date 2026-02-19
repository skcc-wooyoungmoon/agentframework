/**
 * 리스트 목록 헤더
 */
function Header({ children }: { children: React.ReactNode }) {
  return <div className='ui-data-grp-hdr flex items-center justify-between min-h-10 relative'>{children}</div>;
}

/**
 * 리스트 목록 컨테이너
 */
function Body({ children, className = '' }: { children: React.ReactNode; className?: string }) {
  return <div className={'ui-data-grp-bdy pt-2 flex items-center ' + className}>{children}</div>;
}

/**
 * 리스트 하단 영역 컨테이너
 */
function Footer({ children, className = 'ui-data-grp-ftr' }: { children: React.ReactNode; className?: string }) {
  return <div className={className}>{children}</div>;
}

const UIListContentBox = {
  Header,
  Body,
  Footer,
};

export { UIListContentBox };
