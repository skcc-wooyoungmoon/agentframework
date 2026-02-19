export type UILabelVariant = 'badge' | 'line' | 'solid' | 'gray';

export type UILabelIntent =
  | 'complete' // 완료, 이용가능
  | 'progress' // 진행중
  | 'warning' // 경고
  | 'error' // 실패, 취소, 학습실패, 자원소진
  | 'stop' // 중지중
  | 'neutral' // 중립, 미사용
  | 'choice' // 할당완료
  | 'tag' // 태그 (파란 배경에 흰 텍스트)
  | 'blue' // 블루 계열 (line, solid)
  | 'black' // 블랙 계열 (line, solid)
  | 'purple' // 퍼플 계열 (line, solid)
  | 'red' // 레드 계열 (solid)
  | 'green' // 그린 계열 (solid)
  | 'gray' // 그레이 계열 (solid)
  | 'gray-type1' // gray variant 타입1
  | 'gray-type2' // gray variant 타입2
  | 'gray-2-outline' // 회색 아웃라인 타입 (line variant용)
  | 'overload'; // 과부하 추가

export interface UILabelProps {
  /** 라벨 내부 텍스트 */
  children: React.ReactNode;
  /** 라벨 스타일 변형 */
  variant?: UILabelVariant;
  /** 라벨 색상 테마 */
  intent?: UILabelIntent;
  /** 아이콘 표시 여부 (badge variant에서만 사용) */
  showIcon?: boolean;
  /** 추가 CSS 클래스 */
  className?: string;
}
