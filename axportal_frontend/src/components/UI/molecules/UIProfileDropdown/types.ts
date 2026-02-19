export interface ProfileUser {
  /** 사용자 ID */
  id: string;
  /** 사용자 이름 (짧은 이름) */
  name: string;
  /** 사용자 전체 이름 */
  fullName: string;
  /** 배경 색상 */
  bgColor: string;
  /** 관리자 여부 */
  isAdmin?: boolean;
  /** 부서명 */
  department?: string;
}

export interface UIProfileDropdownProps {
  /** 사용자 목록 */
  users: ProfileUser[];
  /** 드롭다운 열림 상태 */
  isOpen: boolean;
  /** 드롭다운 닫기 핸들러 */
  onClose: () => void;
  /** 추가 CSS 클래스 */
  className?: string;
  /** 최대 표시 개수 (기본값: 5) */
  maxDisplay?: number;
}
