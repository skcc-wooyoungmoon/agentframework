export interface UICardInfoItemProps {
  /** 카드 아이템 데이터 */
  data: Array<{
    /** 라벨 텍스트 */
    label: string;
    /** 값 텍스트 */
    value: string;
  }>;
  /** 추가 CSS 클래스 */
  className?: string;
}