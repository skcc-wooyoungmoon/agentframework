export interface UIVersionCardItem {
  createdAt?: string;
  createdBy?: string;
  version: string;
  date: string;
  tags?: Array<{
    label: string;
    intent: 'blue' | 'gray' | 'violet';
  }>;
  isActive?: boolean;
  id?: string; // 버전 ID (onVersionClick에서 사용)
}

export interface UIVersionCardProps {
  /** 버전 목록 데이터 */
  versions: Array<UIVersionCardItem>;
  /** 버전 클릭 핸들러 */
  onVersionClick?: (version: UIVersionCardItem) => void;
  /** 추가 CSS 클래스 */
  className?: string;
}
