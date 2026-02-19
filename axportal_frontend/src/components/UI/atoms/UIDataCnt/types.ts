export interface UIDataCntProps {
  /** 전체 데이터 개수 */
  count?: number;
  /** 단위 텍스트 */
  unit?: string;
  /** 접두사 텍스트 */
  prefix?: string;
  /** 숫자 포맷팅 여부 (천단위 콤마) */
  format?: boolean;
  /** 확장 스타일 클래스 */
  className?: string;
}
