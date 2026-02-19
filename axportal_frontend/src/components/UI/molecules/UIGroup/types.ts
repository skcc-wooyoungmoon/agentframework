import type { CSSProperties, ReactNode } from 'react';
export interface UIGroupProps {
  /* 유닛간 간격 */
  gap: number;
  /* 가로 정렬 */
  align?: 'start' | 'center' | 'end';
  /* 세로 정렬 */
  vAlign?: 'start' | 'center' | 'end';
  /* 정렬 방향 */
  direction?: 'row' | 'column';
  /* 자식노드 */
  children?: ReactNode;
  /* 클래스 명 */
  className?: string;
  /* 추가 인라인 스타일 */
  style?: CSSProperties;
}
