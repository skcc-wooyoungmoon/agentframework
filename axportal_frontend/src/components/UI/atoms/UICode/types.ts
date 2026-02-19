export interface UICodeProps {
  /** 코드 에디터의 값 */
  value?: string | undefined;
  /** 초기값 */
  initialValue?: string;
  /** 값 변경 시 호출되는 함수 */
  // eslint-disable-next-line no-unused-vars
  onChange?: (value: string) => void;
  /** 테마 설정 */
  theme?: 'light' | 'dark' | 'none';
  /** 읽기 전용 여부 */
  readOnly?: boolean;
  /** 비활성화 여부 */
  disabled?: boolean;
  width?: string;
  height?: string;
  minHeight?: string;
  maxHeight?: string;
  maxWidth?: string;
  language?: 'python' | 'json' | 'yaml';
  /** 텍스트 자동 줄바꿈 여부 (기본값: false) */
  wordWrap?: boolean;
}
