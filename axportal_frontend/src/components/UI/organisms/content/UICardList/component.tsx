import { UIIcon2 } from '../../../atoms/UIIcon2';
import { UILabel } from '../../../atoms/UILabel';
import { UIImage } from '../../../atoms/UIImage';

import type { UICardListItemType, UICardListProps } from './types';

/**
 * CardList 컴포넌트 (Atomic Design: organism)
 * - 다양한 형태의 카드를 리스트로 표시하는 컴포넌트
 * - 체크박스, 상태 뱃지, 진행률 바, 태그, 메타데이터 등을 선택적으로 표시
 * - 유연한 구성으로 다양한 카드 유형 지원
 */
/**
 * @deprecated
 */
export function UICardList({ items, className = '', layout = 'vertical', cardHeight = 'auto' }: UICardListProps) {
  const layoutClass = layout === 'grid' ? 'grid' : 'space-y-4';

  const gridStyle =
    layout === 'grid'
      ? {
          gridTemplateColumns: 'repeat(3, minmax(506px, 1fr))',
          gap: '16px',
        }
      : {};

  return (
    <div className={`${layoutClass} ${className}`} style={gridStyle}>
      {items.map(item => (
        <UICardListItem key={item.id} item={item} cardHeight={cardHeight} />
      ))}
    </div>
  );
}

function UICardListItem({ item, cardHeight }: { item: UICardListItemType; cardHeight?: 'auto' | number }) {
  const handleClick = () => {
    if (item.onClick) {
      item.onClick();
    }
  };


  const cardStyle = cardHeight === 'auto' ? { width: '506px', flex: 'none' } : { width: '506px', height: `${cardHeight}px`, flex: 'none' };

  return (
    <div
      className={`
        bg-white rounded-2xl border border-[#dce2ed]
        ${item.onClick ? 'cursor-pointer' : ''}
        p-6 flex flex-col space-y-4
      `}
      style={cardStyle}
      onClick={handleClick}
    >
      {/* 헤더 영역: 체크박스 + 제목/설명 + 더보기 */}
      <div className='flex items-start justify-between'>
        <div className='flex items-start flex-1 min-w-0'>
          {/* 제목 및 설명 영역 */}
          <div className='flex-1 min-w-0 overflow-hidden'>
            <div className='text-[#373e4d] font-semibold text-lg leading-[26px] tracking-[0px] truncate'>{item.title}</div>
            <div className='text-[#576072] font-normal text-base leading-[24px] tracking-[-0.08px] truncate'>{item.desc || ''}</div>
          </div>
        </div>

        {/* 더보기 버튼 - Figma 스펙: 24x24px */}
        {!item.hideMoreButton && (
          <button
            className='flex-shrink-0 cursor-pointer w-6 h-6 flex items-center justify-center'
            onClick={e => {
              e.stopPropagation();
              // 더보기 버튼 전용 핸들러가 있으면 실행, 없으면 기본 onClick 실행
              if (item.onMoreClick) {
                item.onMoreClick();
              } else if (item.onClick) {
                item.onClick();
              }
            }}
          >
            <UIIcon2 className='ic-system-24-more' />
          </button>
        )}
      </div>

      {/* 상태 배지 영역 */}
      {!item.hideBadge && (
        <div>
          <UILabel variant='badge' intent={getBadgeIntent(item.status)}>
            {getStatusText(item.status)}
          </UILabel>
        </div>
      )}

      {/* 메타데이터 영역 */}
      <div className='space-y-1'>
        {item.metadata &&
          item.metadata.slice(0, 4).map((meta, index) => (
            <div key={index} className='flex items-center h-6'>
              <span className='text-[#576072] font-normal text-sm leading-5 tracking-[-0.14px] w-[76px] flex-shrink-0'>{meta.label}</span>
              <span className='text-[#373e4d] font-normal text-base leading-6 tracking-[-0.08px] flex-1 truncate ml-3'>
                {meta.icon && (
                  <span className='inline-flex items-center gap-1 mr-1'>
                    <UIImage src={`/assets/images/logo/${meta.icon}.svg`} alt={meta.icon} className='w-6 h-6 flex-shrink-0' />
                  </span>
                )}
                {meta.value}
              </span>
            </div>
          ))}
      </div>
    </div>
  );
}

function getStatusText(status: UICardListItemType['status']): string {
  switch (status) {
    case 'complete':
      return '이용 가능';
    case 'progress':
      return '진행중';
    case 'error':
      return '실패';
    case 'stop':
      return '중지중';
    default:
      return '이용 가능';
  }
}

function getBadgeIntent(status: UICardListItemType['status']): 'complete' | 'progress' | 'error' | 'stop' {
  switch (status) {
    case 'complete':
      return 'complete';
    case 'progress':
      return 'progress';
    case 'error':
      return 'error';
    case 'stop':
      return 'stop';
    default:
      return 'complete';
  }
}
