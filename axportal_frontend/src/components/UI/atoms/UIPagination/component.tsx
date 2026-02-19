import { UIIcon2 } from '../UIIcon2';

import type { UIPaginationProps } from './types';

/**
 * 페이지네이션 컴포넌트
 * 페이지 이동 네비게이션을 제공하는 UI 컴포넌트
 */
export function UIPagination({
  currentPage,
  totalPages,
  onPageChange,
  displayPageCount = 10,
  showFirstLastButtons = true,
  showPrevNextButtons = true,
  className = '',
  disabled = false,
  hasNext,
}: UIPaginationProps) {
  // Lazy mode: totalPages가 음수일 경우 활성화
  const lazy_mode = totalPages < 0;

  // 페이지 번호 그룹 계산
  const getPageNumbers = () => {
    // Lazy mode일 때는 현재 페이지만 표시
    if (lazy_mode) {
      return [currentPage];
    }

    const pageNumbers: number[] = [];
    const halfDisplay = Math.floor(displayPageCount / 2);

    let startPage = Math.max(1, currentPage - halfDisplay);
    const endPage = Math.min(totalPages, startPage + displayPageCount - 1);

    // 끝 페이지 근처에서 시작 페이지 재조정
    if (endPage - startPage + 1 < displayPageCount) {
      startPage = Math.max(1, endPage - displayPageCount + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pageNumbers.push(i);
    }

    return pageNumbers;
  };

  const pageNumbers = getPageNumbers();
  const isFirstPage = currentPage === 1;
  const isLastPage = lazy_mode ? false : currentPage === totalPages;

  // 다음 페이지 가능 여부: lazy mode일 때는 hasNext 사용, 아닐 때는 기존 로직
  const canGoNext = lazy_mode
    ? (hasNext ?? false)
    : !isLastPage;

  const handlePageChange = (page: number) => {
    // Lazy mode일 때는 totalPages 체크 제외
    if (lazy_mode) {
      if (page >= 1 && page !== currentPage) {
        onPageChange(page);
      }
    } else {
      if (page >= 1 && page <= totalPages && page !== currentPage) {
        onPageChange(page);
      }
    }
  };

  return (
    <div className={'flex items-center gap-2 ' + className}>
      {/* 첫 페이지 버튼 */}
      {showFirstLastButtons && (
        <button
          onClick={() => handlePageChange(1)}
          disabled={isFirstPage || disabled}
          className={'flex items-center justify-center w-6 h-6 ' + (isFirstPage || disabled ? 'opacity-30 cursor-not-allowed' : 'cursor-pointer hover:opacity-70')}
          aria-label='첫 페이지로 이동'
        >
          <UIIcon2 className='ic-system-24-outline-left-double' />
        </button>
      )}

      {/* 이전 페이지 버튼 */}
      {showPrevNextButtons && (
        <button
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={isFirstPage || disabled}
          className={'flex items-center justify-center w-6 h-6 ' + (isFirstPage || disabled ? 'opacity-30 cursor-not-allowed' : 'cursor-pointer hover:opacity-70')}
          aria-label='이전 페이지로 이동'
        >
          <UIIcon2 className='ic-system-24-arrow-right-1 rotate-icon' />
        </button>
      )}

      {/* 페이지 번호 버튼들 */}
      <div className='flex items-center gap-2'>
        {pageNumbers.map(pageNumber => {
          const isActive = pageNumber === currentPage;

          return (
            <button
              key={pageNumber}
              onClick={() => handlePageChange(pageNumber)}
              disabled={disabled}
              className={
                'flex items-center justify-center h-8 px-[10px] text-sm font-normal ' +
                'transition-all duration-200 ' +
                (disabled ? 'opacity-30 cursor-not-allowed' : isActive ? 'text-[#2670FF] rounded-full cursor-default' : 'text-gray-600 hover:text-gray-900 cursor-pointer')
              }
              style={{
                fontFamily: 'Pretendard',
                fontSize: '14px',
                lineHeight: '20px',
                letterSpacing: '-0.08px',
              }}
              aria-label={'페이지 ' + pageNumber}
              aria-current={isActive ? 'page' : undefined}
            >
              {pageNumber}
            </button>
          );
        })}
      </div>

      {/* 다음 페이지 버튼 */}
      {showPrevNextButtons && (
        <button
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={!canGoNext || disabled}
          className={'flex items-center justify-center w-6 h-6 ' + (!canGoNext || disabled ? 'opacity-30 cursor-not-allowed' : 'cursor-pointer hover:opacity-70')}
          aria-label='다음 페이지로 이동'
        >
          <UIIcon2 className='ic-system-24-arrow-right-1' />
        </button>
      )}

      {/* 마지막 페이지 버튼 - lazy mode일 때는 숨김 */}
      {showFirstLastButtons && !lazy_mode && (
        <button
          onClick={() => handlePageChange(totalPages)}
          disabled={isLastPage || disabled}
          className={'flex items-center justify-center w-6 h-6 ' + (isLastPage || disabled ? 'opacity-30 cursor-not-allowed' : 'cursor-pointer hover:opacity-70')}
          aria-label='마지막 페이지로 이동'
        >
          <UIIcon2 className='ic-system-24-outline-left-double rotate-icon' />
        </button>
      )}
    </div>
  );
}
