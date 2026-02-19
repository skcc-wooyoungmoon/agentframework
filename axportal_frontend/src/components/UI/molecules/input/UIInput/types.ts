export type UIInputDatePickerProps = {
  /** 날짜 선택기 타입 (기본값: 'day') */
  type?: 'DAY' | 'MONTH';
  /** 최대 선택 가능 날짜 (이 날짜 이후로는 선택 불가능) - 'YYYY.MM.DD' 형식 */
  maxDate?: string;
  /** 캘린더 팝업의 left 위치 설정 (기본값: 'initial') */
  calendarLeftPosition?: 'initial' | '0';
  /** 캘린더 팝업의 방향 설정 (기본값: 'right') - 'left'는 왼쪽 정렬, 'right'는 오른쪽 정렬 */
  calendarPosition?: 'left' | 'right';
  /** 날짜 선택기 타입 (Date 컴포넌트용) - 'date-weekly-first'는 7일 이후, 'date-weekly-last'는 7일 이전 */
  dateType?: 'date-weekly-first' | 'date-weekly-last';
} & Omit<UIInputFieldProps, 'type' | 'customButton'>;

export type UIInputFieldProps = {
  type?: 'text' | 'password' | 'date' | 'search' | 'custom' | 'number';
  placeholder?: string;
  value?: string;
  onChange?: React.ChangeEventHandler<HTMLInputElement>;
  error?: string;
  customButton?: React.ReactNode;
  /** 입력 필드 크기 */
  size?: 'default' | 'small';
  /** 날짜 선택기 래퍼 className (Date 컴포넌트용) */
  className?: string;
  /**날짜 직접 수정 가능 여부 (기본값: false) */
  editable?: boolean;
  /** 선택된 날짜로부터 N일 이후 날짜에 'date-on-after' 클래스 추가 */
  highlightDays?: {
    after?: number;
    before?: number;
  };
} & Omit<React.InputHTMLAttributes<HTMLInputElement>, 'onChange' | 'size'>;

export type UIInputTagsProps = {
  /**
   * 현재 태그 배열 */
  tags: string[];
  /** 콜백 */
  onChange?: (value: string[]) => void;
  /** 플레이스홀더 텍스트 */
  placeholder?: string;
  /** 오류 메시지 */
  error?: string;
  /** 도움말 텍스트 */
  helperText?: string;
  /** 도움말 텍스트 스타일 타입 */
  helperTextType?: 'default' | 'bullet';
  /** 도움말 텍스트 간격 타입 */
  helperTextSpacing?: 'default' | 'none';
  /** 라벨 텍스트 */
  label?: string;
  /** 필수 입력 표시 여부 */
  required?: boolean;
  /** 추가 CSS 클래스명 */
  className?: string;
  /** 포커스 시 호출되는 콜백 함수 */
  onFocus?: () => void;
  /** 블러 시 호출되는 콜백 함수 */
  onBlur?: () => void;
  /** 우측 버튼 텍스트 (기본값 : 추가) */
  buttonText?: string;
  /** 우측 버튼 클릭 시 호출되는 콜백 함수 */
  onButtonClick?: (e: React.MouseEvent<HTMLButtonElement>) => void;
  /** 태그 하나당 최대 글자수 (기본값: 8) */
  tagMaxLength?: number;
} & Omit<React.InputHTMLAttributes<HTMLInputElement>, 'type' | 'onChange' | 'value' | 'onKeyDown'>;
