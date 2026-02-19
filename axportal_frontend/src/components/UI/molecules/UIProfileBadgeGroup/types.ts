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

export interface UIProfileBadgeGroupProps {
  /** 사용자 목록 */
  users: ProfileUser[];
  /** 표시할 최대 배지 수 (기본값: 3) */
  maxVisible?: number;
  /** 드롭다운 표시 여부 (기본값: true) */
  showDropdown?: boolean;
  /** 드롭다운 최대 표시 개수 (기본값: 5) */
  maxDropdownDisplay?: number;
  /** 추가 CSS 클래스 */
  className?: string;
}
