import type { ReactNode } from 'react';
export interface UICardBoxProps {
  /* 유닛간 간격 */
  gap?: number;
  /* flex 타입 */
  flexType?: 'none' | 'shrink' | 'grow';
  /* 자식노드 */
  children: ReactNode;
  /* 클래스 명 */
  className?: string;
}
