export interface UIToggleProps {
  /** 토글의 선택 상태 */
  checked?: boolean;
  /** 토글의 비활성화 상태 */
  disabled?: boolean;
  /** 토글 상태 변경 시 호출되는 콜백 함수 */
  onChange?: (checked: boolean) => void;
  /** 토글의 크기 (medium: 46x24px, small: 38x20px) */
  size?: 'medium' | 'small';
  /** 추가 CSS 클래스명 */
  className?: string;
  /** 라벨 텍스트 */
  label?: string;
  /** 라벨 위치 (left: 왼쪽, right: 오른쪽, top: 위) */
  labelPosition?: 'left' | 'right' | 'top';
  /** 토글 내부에 표시될 텍스트 */
  innerText?: string;
  /** 토글 타입 (basic: 기본형, text: 텍스트 포함형, dataView: 데이터 리스트 뷰 타입 선택형, segment: 세그먼트 탭형) */
  variant?: 'basic' | 'text' | 'dataView' | 'segment';
  /** 세그먼트 옵션들 (variant가 segment일 때 사용) */
  segmentOptions?: Array<{
    id: string;
    label: string;
    disabled?: boolean;
  }>;
  /** 선택된 세그먼트 값 (variant가 segment일 때 사용) */
  segmentValue?: string;
  /** 세그먼트 변경 핸들러 (variant가 segment일 때 사용) */
  onSegmentChange?: (value: string) => void;
}
