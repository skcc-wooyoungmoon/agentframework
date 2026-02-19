export interface UITabListItem {
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

export interface UITabListProps {
  /**
   * 탭 항목 리스트
   * - 여러 개의 탭 버튼을 렌더링하기 위한 데이터
   * - 각 항목은 UITabButton으로 변환됨
   */
  items: UITabListItem[];

  /**
   * 현재 활성화된 탭 ID
   * - items 배열에서 해당 id를 가진 탭이 활성 상태로 표시
   * - 슬라이딩 인디케이터 위치 계산에 사용
   */
  activeId: string;

  /**
   * 탭 리스트 크기
   * - "large": 48px 높이의 탭 버튼들
   * - "medium": 40px 높이의 탭 버튼들
   * - 모든 하위 UITabButton에 전달됨
   */
  size?: 'large' | 'medium';

  /**
   * 타이포그래피 스타일
   * - "default": 기본 타이포그래피
   * - "body-2": UITypography variant-body2 사용 (작은 글씨)
   */
  variant?: 'default' | 'body-2';

  /**
   * 탭 클릭 이벤트 핸들러
   * - 탭이 클릭될 때 호출되는 콜백 함수
   * - 클릭된 탭의 ID를 매개변수로 전달
   * - 상위 컴포넌트에서 상태 관리 처리
   */
  onTabClick?: (tabId: string) => void;

  /**
   * 추가 CSS 클래스명
   * - 탭 리스트 컨테이너에 적용될 커스텀 스타일
   */
  className?: string;
}
