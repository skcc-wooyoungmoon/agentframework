import { useLocation } from 'react-router-dom';
import type { UIContentsProps } from './types';

export function UIContents({
  children,
  className = '',
  showBreadcrumb = false,
  breadcrumbItems = [],
  pageTitle,
  padding = true,
  backgroundColor,
  enableHorizontalScroll = false,
  enableVerticalScroll = true,
}: UIContentsProps) {
  const location = useLocation();

  // Graph.tsx 페이지에서만 자동으로 세로 스크롤 비활성화
  const isGraphPage = location.pathname.includes('/test/secret/graph2');
  const finalEnableVerticalScroll = isGraphPage ? false : enableVerticalScroll;

  const getOverflowClass = () => {
    if (enableHorizontalScroll) return 'overflow-auto';
    if (finalEnableVerticalScroll) return 'overflow-y-auto';
    return 'overflow-hidden';
  };

  return (
    // overflow-y-auto > overflow-hidden 전체 컨텐츠 영역으로 다시 수정
    <main className={`flex-1 ${getOverflowClass()} h-full ${className}`} style={{ backgroundColor: backgroundColor || '#FFFFFF' }}>
      <div className={padding ? 'py-[40px] px-[48px]' : ''}>
        {/* 페이지 헤더 (Breadcrumb, 페이지 타이틀) */}
        {(showBreadcrumb || pageTitle) && (
          <div className='bg-white border-b border-gray-200 px-6 py-4'>
            {showBreadcrumb && breadcrumbItems.length > 0 && (
              <nav className='text-sm text-gray-500 mb-2'>
                <ol className='flex items-center space-x-2'>
                  {breadcrumbItems.map((item, index) => (
                    <li key={index} className='flex items-center'>
                      {index > 0 && (
                        <svg className='w-4 h-4 mx-2 text-gray-400' fill='none' stroke='currentColor' viewBox='0 0 24 24'>
                          <path strokeLinecap='round' strokeLinejoin='round' strokeWidth={2} d='M9 5l7 7-7 7' />
                        </svg>
                      )}
                      {item.href ? (
                        <a href={item.href} className='hover:text-blue-600 transition-colors'>
                          {item.label}
                        </a>
                      ) : (
                        <span className={index === breadcrumbItems.length - 1 ? 'text-gray-900 font-medium' : ''}>{item.label}</span>
                      )}
                    </li>
                  ))}
                </ol>
              </nav>
            )}

            {pageTitle && <h2 className='text-xl font-semibold text-gray-900'>{pageTitle}</h2>}
          </div>
        )}

        {/* 페이지 콘텐츠 */}
        <div>
          {/* <div className={`${padding ? 'px-10 pt-12 pb-14' : ''}`}> */}
          {children || (
            <div className='flex items-center justify-center h-full'>
              <div className='text-center text-gray-500'>
                <div className='text-4xl mb-4'>📄</div>
                <p className='text-lg font-medium'>Contents Area</p>
                <p className='text-sm mt-2'>페이지 콘텐츠가 여기에 표시됩니다.</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </main>
  );
}
