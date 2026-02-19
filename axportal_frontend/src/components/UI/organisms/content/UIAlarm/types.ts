export interface UIAlarmProps {
  /** 필터 위치 */
  position?: 'left' | 'right';
  /** 필터 크기 */
  size?: 'small' | 'large';
  /** 필터 너비 (size가 지정되지 않은 경우에만 사용) */
  width?: string;
  /** 자식 요소 */
  children?: React.ReactNode;
  /** 추가 CSS 클래스 */
  className?: string;
  /** 필터 표시 여부 */
  isVisible?: boolean;
  /** 필터 닫기 함수 */
  onClose?: () => void;
  /** 필터 제목 */
  title?: string;
}
