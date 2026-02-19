export interface UIIDEPopupProps {
  /** 팝업 표시 여부 */
  isOpen?: boolean;
  /** 팝업 닫기 핸들러 */
  onClose: () => void;
  /** 이동하기 버튼 클릭 핸들러 */
  onMoveClick?: () => void;
  /** 팝업 내부 콘텐츠 */
  children?: React.ReactNode;
  /** 추가 CSS 클래스 */
  className?: string;
}
