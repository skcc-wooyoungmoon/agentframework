export type UITextArea2Props = {
  value: string;
  placeholder?: string;
  hint?: string;
  error?: string;
  /** 추가 CSS 클래스 */
  className?: string;
  /** 텍스트영역 라인 타입 (기본값: 'multi-line') */
  lineType?: 'multi-line' | 'single-line';
  /** 텍스트영역 행 수 (기본값: 4) */
  rows?: number;
  /** 텍스트영역 크기 조절 가능 여부 (기본값: true) */
  resizable?: boolean;
  /** 최대 길이 */
  maxLength?: number;
  /** 테두리 제거 여부 (기본값: false) */
  noBorder?: boolean;
  /** 스크롤 fade 효과 활성화 여부 (기본값: false) */
  enableScrollFade?: boolean;
} & Omit<React.InputHTMLAttributes<HTMLTextAreaElement>, 'maxLength'>;
