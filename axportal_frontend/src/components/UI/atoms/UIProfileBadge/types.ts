export interface UIProfileBadgeProps {
  /** 사용자 이름 */
  name: string;
  /** 배경 색상 (hex 또는 rgb) */
  bgColor?: string;
  /** 텍스트 색상 */
  textColor?: string;
  /** 클릭 이벤트 핸들러 */
  onClick?: () => void;
  /** 추가 CSS 클래스 */
  className?: string;
  /** 배지 크기 */
  size?: 'small' | 'medium';
}
