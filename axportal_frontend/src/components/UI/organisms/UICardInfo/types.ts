import type { UICardInfoItemProps } from "../../molecules/UICardInfoItem/types";

export interface UICardInfoProps {
  /** 카드 정보 데이터 목록 */
  items: Array<{
    /** 카드 아이템 데이터 */
    data: UICardInfoItemProps['data'];
  }>;
  /** 그리드 컬럼 개수 (기본값: 2) */
  columns?: number;
  /** 추가 CSS 클래스 */
  className?: string;
  /** 데이터가 없을 때 표시할 메시지 */
  noDataMessage?: string;
}