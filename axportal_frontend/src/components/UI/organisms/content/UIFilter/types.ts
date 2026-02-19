export interface UIFilterProps {
  /** 필터 위치 */
  position?: 'left' | 'right';
  /** 필터 너비 */
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
  /** 채팅 초기화 버튼 클릭 핸들러 */
  onChatReset?: () => void;
  /** 드롭다운 표시 여부 */
  showDropdown?: boolean;
  /** 드롭다운 옵션 */
  dropdownOptions?: Array<{
    value: string;
    label: string;
  }>;
  /** 드롭다운 기본 선택값 */
  defaultDropdownValue?: string;
  /** 드롭다운 선택 변경 핸들러 */
  onDropdownChange?: (value: string) => void;
}
