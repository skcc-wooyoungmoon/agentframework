export interface UIProgressProps {
  /** 진행률 (0-100) */
  value?: number;
  /** 프로그레스 바 상태 (normal: 파란색, error: 빨간색) */
  status?: 'normal' | 'error';
  /** 퍼센트 텍스트 표시 여부 */
  showPercent?: boolean;
  /** 추가 className */
  className?: string;
}
