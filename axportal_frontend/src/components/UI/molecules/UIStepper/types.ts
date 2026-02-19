export interface UIStepperItem {
  /**
   * 스텝 고유 식별자
   * - 각 스텝을 구분하기 위한 유니크 ID
   */
  id?: string;

  /**
   * step 번호
   */
  step: number;

  /**
   * 스텝 라벨 텍스트
   * - 스텝 옆에 표시될 텍스트
   */
  label: string;

  /**
   * 스텝 상태
   * - "completed": 완료된 스텝 (체크 아이콘)
   * - "ongoing": 진행중인 스텝 (파란색 배경)
   * - "incompleted": 미완료 스텝 (회색 배경)
   */
  // status: 'completed' | 'ongoing' | 'incompleted';
}

export interface UIStepperProps {
  /**
   * 스텝 항목 리스트
   * - 여러 개의 스텝을 렌더링하기 위한 데이터 배열
   */
  items: UIStepperItem[];

  /**
   * 현재 진행된 step 번호
   */
  currentStep: number;

  /**
   * 스텝 방향
   * - "vertical": 세로 방향 스텝퍼 (기본값)
   * - "horizontal": 가로 방향 스텝퍼
   */
  direction?: 'vertical' | 'horizontal';

  /**
   * 추가 CSS 클래스명
   * - 스텝퍼 컨테이너에 적용될 커스텀 스타일
   */
  className?: string;
}
