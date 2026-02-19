import type { ReactNode } from 'react';
export interface UIDataListProps {
  /* 유닛간 간격 */
  gap: number;
  /* 유닛간 간격 */
  align?: 'start' | 'center' | 'end';
  /* 정렬 방향 */
  direction?: 'row' | 'column';
  /* 자식노드 */
  children: ReactNode;
  /* 클래스 명 */
  className?: string;
  /* 데이터 리스트 */
  datalist?: Array<{
    dataName: string;
    dataValue: ReactNode;
  }>;
}
