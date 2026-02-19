import type { ReactNode } from 'react';
export interface UIListProps {
  /* 유닛간 간격 */
  gap: number;
  /* 정렬 */
  align?: 'start' | 'center' | 'end';
  /* 정렬 방향 */
  direction?: 'row' | 'column';
  /* 클래스 명 */
  className?: string;
  /* 데이터 리스트 */
  data?: Array<{
    dataItem: ReactNode;
  }>;
}
