export interface UIPercentBarProps {
  /** 진행률 퍼센트 값 (0-100) */
  value: number;
  /** 진행바 채운 부분의 배경색 */
  color?: string;
  /** 퍼센트 텍스트의 색상 */
  textColor?: string;
  /** 진행바 높이 (px) */
  height?: number;
  /** 추가 CSS 클래스명 */
  className?: string;
  /** 진행 상태 (success: 성공, fail: 실패) */
  status?: 'success' | 'error';
}
