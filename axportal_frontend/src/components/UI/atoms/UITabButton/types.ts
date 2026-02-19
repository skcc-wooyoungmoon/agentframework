export interface UITabButtonProps {
  /**
   * 탭 라벨 텍스트
   * - 단일 탭 버튼에 표시될 텍스트
   * - 최대 너비 120px 내에서 ellipsis 처리
   */
  label: string;

  /**
   * 탭 활성화 상태
   * - true: 현재 선택된 탭 (Bold, #005DF9 색상)
   * - false: 비선택 탭 (SemiBold, #8B95A9 색상)
   */
  isActive: boolean;

  /**
   * 탭 비활성화 상태
   * - true: 클릭 불가능한 상태 (회색 처리)
   * - false/undefined: 정상 클릭 가능
   */
  disabled?: boolean;

  /**
   * 탭 버튼 크기
   * - "large": 48px 높이, 18px 폰트
   * - "medium": 40px 높이, 16px 폰트
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
   * - 부모 컴포넌트에서 탭 선택 상태 변경 처리
   * - disabled가 true일 때는 호출되지 않음
   */
  onClick?: () => void;

  /**
   * 추가 CSS 클래스명
   * - 커스텀 스타일링을 위한 className
   */
  className?: string;
}
