import type { ReactNode } from 'react';

export interface UITabsItem {
  /**
   * 탭 고유 식별자
   * - 각 탭을 구분하기 위한 유니크 ID
   * - activeId와 비교하여 활성 상태 결정
   */
  id: string;

  /**
   * 탭 라벨 텍스트
   * - 탭 버튼에 표시될 텍스트
   */
  label: string;

  /**
   * 탭 비활성화 여부
   * - true: 클릭 불가능한 비활성 탭
   * - false/undefined: 정상 클릭 가능
   */
  disabled?: boolean;
}

export interface UITabsProps {
  /**
   * 탭 항목 리스트
   * - 전체 탭 시스템에서 사용할 탭들의 정보
   * - UITabList에 전달되어 개별 UITabButton들로 변환
   */
  items: UITabsItem[];

  /**
   * 현재 활성화된 탭 ID
   * - 전체 탭 시스템에서 현재 선택된 탭의 식별자
   * - 상태 관리의 중심이 되는 값
   */
  activeId: string;

  /**
   * 탭 시스템 크기
   * - "large": 48px 높이, 18px 폰트 (주요 내비게이션용)
   * - "medium": 40px 높이, 16px 폰트 (보조 내비게이션용)
   */
  size?: 'large' | 'medium';

  /**
   * 타이포그래피 스타일
   * - "default": 기본 타이포그래피
   * - "body-2": UITypography variant-body2 사용 (작은 글씨)
   */
  variant?: 'default' | 'body-2';

  /**
   * 탭 변경 이벤트 핸들러
   * - 사용자가 다른 탭을 클릭했을 때 호출
   * - 상위 컴포넌트에서 activeId 상태 업데이트 처리
   * - 탭 콘텐츠 전환 등의 추가 로직 처리 가능
   */
  onChange?: (tabId: string) => void;

  /**
   * 탭 콘텐츠 영역 (선택사항)
   * - 탭에 따라 변경될 콘텐츠 영역
   * - TabPanel 역할을 하는 콘텐츠
   * - 상위 컴포넌트에서 activeId에 따라 조건부 렌더링 처리
   */
  children?: ReactNode;

  /**
   * 추가 CSS 클래스명
   * - 전체 탭 시스템 컨테이너에 적용될 커스텀 스타일
   */
  className?: string;
}
