export interface UITooltipProps {
  /** 툴팁을 표시할 대상 요소 */
  children: React.ReactNode;
  /** 툴팁 타입 (디자인 가이드 기준) */
  type?: 's' | 's-fixed' | 'notice' | 'info' | 'title-notice' | 'title-notice-l' | 'grid' | 'gridHover';
  /** 툴팁 제목 */
  title?: string;
  /** 툴팁 리스트 아이템들 */
  items?: string[];
  /** 단일 콘텐츠 (type이 "s"나 "info"일 때 사용) */
  content?: string;
  /** 불릿 포인트 타입 */
  bulletType?: 'circle' | 'dash' | 'default';
  /** 툴팁 표시 위치 */
  position?: 'top' | 'top-start' | 'top-end' | 'bottom' | 'bottom-start' | 'bottom-end' | 'left' | 'right' | 'auto';
  /** 화살표 표시 여부 */
  showArrow?: boolean;
  /** 닫기 버튼 표시 여부 */
  showCloseButton?: boolean;
  /** 추가 CSS 클래스명 */
  className?: string;
  /** 툴팁 컨테이너 추가 CSS 클래스명 */
  tooltipClassName?: string;
  /** 툴팁 활성화 여부 */
  disabled?: boolean;
  /** 툴팁 트리거 방식 */
  trigger?: 'hover' | 'click' | 'focus' | 'manual';
  /** 수동 모드에서 툴팁 표시 여부 */
  visible?: boolean;
  /** 툴팁 표시 상태 변경 콜백 */
  onVisibleChange?: (visible: boolean) => void;
  /** 닫기 버튼 클릭 콜백 */
  onClose?: () => void;
}
