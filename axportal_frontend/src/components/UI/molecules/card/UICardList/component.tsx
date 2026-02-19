import { useMemo } from 'react';
import { UICardBox } from '../UICardBox';
import { UICardListNoData } from '../UICardListNoData';
import { UISkeletonCard } from '../UISkeletonCard';

type UICardListProps = {
  /** 카드 데이터 배열 */
  rowData: any[];
  /** 카드 렌더링 함수 */
  card: (item: any) => React.ReactNode;
  /** flex 타입 */
  flexType?: 'none' | 'shrink' | 'grow';
  /** 추가 CSS 클래스 */
  className?: string;
  /** 유닛간 간격 */
  gap?: number;
  /** 로딩 상태 */
  loading?: boolean;
};

export const UICardList = ({ rowData, card, flexType, className, gap, loading = false }: UICardListProps) => {
  // 로딩 상태일 때 스켈레톤 카드 데이터 생성
  const displayRowData = useMemo(() => {
    if (loading) {
      return Array.from({ length: 6 }, (_, index) => ({
        __skeleton: true,
        id: `skeleton-${index}`,
      }));
    }
    return rowData;
  }, [loading, rowData]);

  return (
    <>
      {displayRowData.length > 0 ? (
        <ul className='w-full'>
          <UICardBox flexType={flexType} className={className} gap={gap}>
            {displayRowData?.map((item, index) => (
              <li key={item.id || index}>{item.__skeleton ? <UISkeletonCard /> : card(item)}</li>
            ))}
          </UICardBox>
        </ul>
      ) : (
        <UICardListNoData />
      )}
    </>
  );
};
