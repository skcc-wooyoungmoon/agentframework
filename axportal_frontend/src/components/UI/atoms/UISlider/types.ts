export interface UISliderProps {
  /** 슬라이더의 최솟값 */
  min?: number;
  /** 슬라이더의 최댓값 */
  max?: number;
  /** 슬라이더의 현재 값 */
  value?: number;
  /** 슬라이더 값 변경 시 호출되는 콜백 함수 */
  onChange?: (value: number) => void;
  /** 슬라이더의 비활성화 상태 */
  disabled?: boolean;
  /** 슬라이더 바의 너비 (픽셀 단위 또는 CSS 값) */
  width?: number | string;
  /** 추가 CSS 클래스명 */
  className?: string;
  /** 시작 라벨 텍스트 */
  startLabel?: string;
  /** 끝 라벨 텍스트 */
  endLabel?: string;
  /** 툴팁 표시 여부 */
  showTooltip?: boolean;
  /** 툴팁에 단위를 표시할 때 사용 (예: %) */
  unit?: string;
  /** 시작/끝 라벨의 좌우 패딩 (픽셀 단위) */
  startEndLabelPadding?: number;
  /** 라벨 텍스트 */
  label?: string;
  /** 필수 입력 표시 여부 */
  required?: boolean;
  /** 텍스트 필드 표시 여부 */
  showTextField?: boolean;
  /** 텍스트 필드 값 */
  textValue?: string;
  /** 텍스트 필드 변경 콜백 */
  onTextChange?: (value: string) => void;
  /** 텍스트 필드 너비 */
  textFieldWidth?: string;
  /** 슬라이더 색상 (hex 색상 코드) */
  color?: string;
  /** 텍스트 필드에 표시할 소수점 자릿수 (undefined면 자동) */
  decimalPlaces?: number;
  /** 슬라이더 이동 단위 (기본값: 1, 소수점 범위는 자동 계산) */
  step?: number;
}
