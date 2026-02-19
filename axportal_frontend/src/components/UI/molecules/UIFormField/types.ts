import type { ReactNode } from 'react';
export interface UIFormFieldProps {
  /* 유닛간 간격 */
  gap: number;
  /* 가로 정렬 */
  align?: 'start' | 'center' | 'end';
  /* 세로 정렬 */
  vAlign?: 'start' | 'center' | 'end';
  /* 분할 간격 균등/비균등(직접 사이즈 설정) */
  division?: 'evenly' | 'unevenly';
  /* 정렬 방향 */
  direction?: 'row' | 'column';
  /* 자식노드 */
  children: ReactNode;
  /* 클래스 명 */
  className?: string;
}
