export interface UIDropdownProps {
  id?: string;
  /** 드랍다운 상태 */
  state?: 'inactive' | 'focused' | 'error' | 'disabled' | 'read';
  /** 드랍다운 변형 타입 */
  variant?: 'default' | 'dataGroup';
  /** 플레이스홀더 텍스트 */
  placeholder?: string;
  /** 선택된 값 - 필수: 외부에서 반드시 제어 */
  value: string;
  /** 옵션 목록 (외부에서 data 조회 후 전달) */
  options: Array<{
    value: string;
    label: string;
  }>;
  /** 클릭 시 refetch 콜백. options가 비어 있을 때만 호출됨 (데이터 있으면 호출 안 함) */
  refetchOnOpen?: () => void | Promise<unknown>;
  /** 오류 메시지 */
  errorMessage?: string;
  /** 드랍다운 열림 상태 - 필수: 외부에서 반드시 제어 */
  isOpen?: boolean;
  /** 높이 설정 */
  height?: 40 | 48;
  /** 너비 설정 (예: 'w-[160px]', 'w-full') */
  width?: string;
  /** 추가 CSS 클래스 */
  className?: string;
  /** 클릭 이벤트 핸들러 - 필수: 외부에서 반드시 제어 */
  onClick?: () => void;
  /** 옵션 선택 이벤트 핸들러 - 필수: 외부에서 반드시 제어 */
  onSelect: (value: string) => void;
  /** 에러 메시지 표시 여부 - 에러 상태에서 선택 시 숨김 처리 */
  showErrorMessage?: boolean;
  /** 라벨 텍스트 */
  label?: string;
  /** 필수 여부 */
  required?: boolean;
  /** 비활성화 여부 */
  disabled?: boolean;
  /** 읽기 전용 여부 */
  readonly?: boolean;
  /** 폰트 크기 설정 (예: '14px', '16px', '1rem') */
  fontSize?: string;
  /** 텍스트 색상 설정 (예: '#000000', 'rgb(0,0,0)', 'text-gray-900') */
  color?: string;
}
