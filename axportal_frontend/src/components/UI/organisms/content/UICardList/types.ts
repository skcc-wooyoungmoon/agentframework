export type UICardListItemType = {
  /** 고유 식별자 */
  id: string;
  /** 모델명 */
  title: string;
  /** 상세 설명 */
  desc?: string;

  /** 상태 (이용가능, 진행중, 실패 등) */
  status: 'complete' | 'progress' | 'error' | 'stop';
  /** 메타데이터 항목들 (라벨: 값 형태) */
  metadata?: Array<{
    label: string;
    value: string;
    /** 값 앞에 표시할 로고 아이콘 */
    icon?: string;
  }>;
  /** 클릭 핸들러 */
  onClick?: () => void;
  /** 더보기 버튼 클릭 핸들러 */
  onMoreClick?: () => void;
  /** 체크박스 표시 여부 */
  showCheckbox?: boolean;
  /** 체크박스 선택 상태 */
  checked?: boolean;
  /** 체크박스 변경 핸들러 */
  onCheckChange?: (checked: boolean) => void;
  /** 진행률 표시 여부 */
  showProgress?: boolean;
  /** 진행률 값 (0-100) */
  progress?: number;
  /** 진행률 상태 */
  progressStatus?: 'normal' | 'error';
  /** 상태 뱃지 숨김 여부 */
  hideBadge?: boolean;
  /** 로고 아이콘 숨김 여부 */
  hideLogo?: boolean;
  /** 더보기 버튼 숨김 여부 */
  hideMoreButton?: boolean;
  /** 태그 목록 */
  tags?: Array<{
    text: string;
    variant?: 'line' | 'solid';
    intent?: 'blue' | 'black' | 'purple' | 'red' | 'green' | 'gray';
  }>;
  /** 상위 영역 라벨 데이터 */
  upperLabels?: Array<{
    label: string;
    value: string;
  }>;
  /** 상위 영역 프로그레스 데이터 */
  upperProgress?: {
    value: number;
    status?: 'normal' | 'error';
    label?: string;
  };
};

export type UICardListProps = {
  /** 카드 리스트 데이터 */
  items: UICardListItemType[];
  /** 추가 CSS 클래스 */
  className?: string;
  /** 레이아웃 타입 */
  layout?: 'vertical' | 'grid';
  /** 카드 높이 (기본값: auto, 숫자 입력시 px 단위로 고정 높이 적용) */
  cardHeight?: 'auto' | number;
  /** 메타데이터 영역의 빈 행 숨김 여부 */
  hideEmptyMetadata?: boolean;
  /** 메타데이터 영역 전체를 숨김 (컨테이너 div 포함) */
  hideMetadataSection?: boolean;
  /** 상위 영역(라벨/프로그레스)의 빈 데이터만 숨김 (컨테이너 유지) */
  hideEmptyUpperData?: boolean;
  /** 상위 영역(라벨/프로그레스) 전체를 숨김 (컨테이너 div 포함) */
  hideUpperSection?: boolean;
};
