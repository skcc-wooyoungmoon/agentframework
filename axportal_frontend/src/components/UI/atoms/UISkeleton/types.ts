export interface UISkeletonProps {
  variant?: 'text' | 'rect' | 'circle';
  width?: string | number;
  height?: string | number;
  animate?: boolean;
  className?: string;
  /** 행 인덱스 (짝수일 때만 배경색 적용) */
  rowIndex?: number;
}
