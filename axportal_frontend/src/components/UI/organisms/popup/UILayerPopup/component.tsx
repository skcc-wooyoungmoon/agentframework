import { useEffect } from 'react';
import { createPortal } from 'react-dom';

import type { UILayerPopupProps } from './types';

export function UILayerPopup({
  isOpen = false,
  onClose,
  title,
  children,
  size = 'md',
  position = 'center',
  showOverlay = true,
  className = '',
  headerActions,
  leftContent,
}: UILayerPopupProps) {
  useEffect(() => {
    if (isOpen) {
      if (size === 'fullscreen') {
        // 풀스크린 팝업일 때 세로 스크롤만 막기
        document.body.style.overflowY = 'hidden';
        // 가로 스크롤은 유지
        document.body.style.overflowX = 'auto';
      } else {
        // 일반 팝업일 때 모든 스크롤 막기
        document.body.style.overflow = 'hidden';
      }
    } else {
      // 팝업이 닫힐 때 body 스크롤 복원
      document.body.style.overflow = '';
      document.body.style.overflowY = '';
      document.body.style.overflowX = '';
    }

    // 컴포넌트 언마운트 시 스크롤 복원
    return () => {
      document.body.style.overflow = '';
      document.body.style.overflowY = '';
      document.body.style.overflowX = '';
    };
  }, [isOpen, size]);

  if (!isOpen) {
    return null;
  }

  const sizeClasses = {
    sm: 'max-w-md',
    md: 'max-w-lg',
    lg: 'max-w-2xl',
    xl: 'max-w-4xl',
    full: 'max-w-full',
    fullscreen: 'w-full h-full max-w-none',
  };

  const positionClasses = {
    center: 'items-center justify-center',
    left: 'items-center justify-start pl-4',
    right: 'items-center justify-end pr-4',
  };

  // Fullscreen 레이아웃 (MainLayout 위에 오버레이)
  if (size === 'fullscreen') {
    return createPortal(
      <div className='fixed inset-0 z-50 min-w-[1920px]'>
        {/* Dimmed 오버레이 (MainLayout 전체를 덮음) */}
        <div className='absolute inset-0 bg-dimmed'></div>

        {/* 팝업 컨텐츠 영역 - 우측 정렬, 세로는 전체 높이 */}
        <div className='relative h-full flex justify-end'>
          <div className='flex h-full bg-white shadow-2xl'>
            {/* Step 영역 (좌측) - leftContent가 있을 때만 표시 */}
            {leftContent && <div className='w-[270px] h-full flex items-start justify-center bg-gray-100'>{leftContent}</div>}

            {/* Contents 영역 (우측) */}
            <div className='w-[1344px] h-full bg-white overflow-y-auto'>
              {children || (
                <div className='h-full flex items-center justify-center'>
                  <div className='text-center'>
                    <div className='text-lg font-semibold text-gray-800 mb-3'>Contents</div>
                    <div className='text-base text-gray-600'>1344×100%</div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>,
      document.body
    );
  }

  // 기존 모달 레이아웃
  return createPortal(
    <div className='fixed inset-0 z-50 flex overflow-y-auto'>
      {/* 오버레이 */}
      {showOverlay && <div className='fixed inset-0 bg-gray-600 bg-opacity-60 transition-opacity' onClick={onClose} />}

      {/* 팝업 컨테이너 */}
      <div className={`relative flex min-h-full w-full ${positionClasses[position]}`}>
        {/* 팝업 콘텐츠 */}
        <div className={`relative bg-white rounded-lg shadow-xl m-4 w-full ${sizeClasses[size]} ${className}`}>
          {/* 팝업 헤더 */}
          {(title || headerActions || onClose) && (
            <div className='flex items-center p-6 border-b border-gray-200'>
              {/* 좌측: 제목 */}
              <div className='flex-shrink-0'>{title && <h3 className='text-lg font-semibold text-gray-900'>{title}</h3>}</div>

              {/* 중앙: 헤더 액션 */}
              <div className='flex-1 flex justify-center'>{headerActions}</div>

              {/* 우측: 닫기 버튼 */}
              <div className='flex-shrink-0'>
                {onClose && (
                  <button onClick={onClose} className='p-2 hover:bg-gray-100 rounded-md transition-colors' title='닫기'>
                    <svg className='w-5 h-5 text-gray-500' fill='none' stroke='currentColor' viewBox='0 0 24 24'>
                      <path strokeLinecap='round' strokeLinejoin='round' strokeWidth={2} d='M6 18L18 6M6 6l12 12' />
                    </svg>
                  </button>
                )}
              </div>
            </div>
          )}

          {/* 팝업 본문 */}
          <div className='p-6'>
            {children || (
              <div className='text-center py-8'>
                <div className='text-4xl mb-4'>🔔</div>
                <p className='text-lg font-medium text-gray-900 mb-2'>레이어 팝업</p>
                <p className='text-sm text-gray-500'>팝업 콘텐츠가 여기에 표시됩니다.</p>
              </div>
            )}
          </div>

          {/* 팝업 푸터 (기본 액션 버튼) */}
          <div className='flex justify-end gap-3 p-6 border-t border-gray-200'>
            <button onClick={onClose} className='px-4 py-2 text-sm font-medium text-gray-700 bg-gray-200 rounded-md hover:bg-gray-300 transition-colors'>
              취소
            </button>
            <button className='px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 transition-colors'>확인</button>
          </div>
        </div>
      </div>
    </div>,
    document.body
  );
}
