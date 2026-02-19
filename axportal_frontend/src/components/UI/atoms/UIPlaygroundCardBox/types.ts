export interface UIPlaygroundCardBoxProps {
  /** 자식 요소 */
  children?: React.ReactNode;
  /** 추가 CSS 클래스 */
  className?: string;
  /** 에러 메시지 (className이 'error'일 때 표시) */
  message?: string;
}
