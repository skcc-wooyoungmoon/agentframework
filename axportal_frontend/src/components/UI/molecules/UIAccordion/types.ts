import type { ReactNode } from 'react';

export interface UIAccordionItemProps {
  /** 아코디언 항목의 제목 */
  title: string;
  /** 아코디언 제목 아래의 부제 */
  titleSub?: string;
  /** 아코디언 항목의 내용 */
  content: ReactNode;
  /** 초기 열림 상태 */
  defaultOpen?: boolean;
  /** 비활성화 상태 */
  disabled?: boolean;
  /** 왼쪽 아이콘 (box variant에서만 사용) */
  icon?: string;
  /** 노티스 아이콘 표시 여부 (box variant에서만 사용) */
  showNoticeIcon?: boolean;
  /** 컨텐츠를 노티스 형태로 표시할지 여부 */
  isNoticeContent?: boolean;
  /** 노티스 타입 (info, warning) */
  noticeType?: 'info' | 'warning';
  /** 오른쪽 액션 버튼 (box variant에서만 사용) */
  actionButton?: ReactNode;
  /** 화살표 아이콘 위치 (left: 기본값, right: 오른쪽 끝) */
  arrowPosition?: 'left' | 'right';
}

export interface UIAccordionProps {
  /** 아코디언 아이템 목록 */
  items: UIAccordionItemProps[];
  /** 아코디언 변형 타입 */
  variant?: 'default' | 'box' | 'small';
  /** 여러 항목 동시 열기 허용 여부 */
  allowMultiple?: boolean;
  /** 추가 CSS 클래스명 */
  className?: string;
  /** 아코디언 항목 변경 콜백 */
  onChange?: (openItems: number[]) => void;
}

export interface UIAccordionItemComponentProps extends UIAccordionItemProps {
  /** 항목 인덱스 */
  index: number;
  /** 열림 상태 */
  isOpen: boolean;
  /** 변형 타입 */
  variant: UIAccordionProps['variant'];
  /** 토글 함수 */
  onToggle: (index: number) => void;
}
