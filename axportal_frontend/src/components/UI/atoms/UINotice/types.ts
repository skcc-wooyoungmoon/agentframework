export interface UINoticeProps {
  /** Notice 타입 - info(회색), warning(빨간색) */
  variant?: 'info' | 'warning';
  /** Notice 메시지 텍스트 */
  message: string | React.ReactNode;
  /** 불릿 타입 - icon(아이콘), dash(대시), circle(원형), number(넘버링) */
  bulletType?: 'icon' | 'dash' | 'circle' | 'number';
  /** 넘버링 타입일 때 숫자 */
  number?: number;
  /** 텍스트 굵기 - regular, bold */
  fontWeight?: 'regular' | 'bold';
  /** 텍스트 깊이 - 1depth, 2depth */
  depth?: '1depth' | '2depth';
  /** 불릿과 텍스트 사이 간격 - normal(8px), large(12px) */
  gapSize?: 'normal' | 'large';
  /** 추가 CSS 클래스명 */
  className?: string;
}