export interface UIDropdownOptionProps {
  /** 옵션 값 */
  value: string;
  /** 옵션 표시 텍스트 (최대 40자) */
  label: string;
  /** 선택 여부 */
  isSelected?: boolean;
  /** 드랍다운 상태 (기본/오류) */
  state?: 'default' | 'error' | 'focused';
  /** 클릭 이벤트 핸들러 */
  onClick?: (value: string) => void;
  /** 폰트 크기 */
  fontSize?: 14 | 16;
}
