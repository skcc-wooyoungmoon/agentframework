export interface UIUserPopupProps {
  /** 팝업 표시 여부 */
  isOpen?: boolean;
  /** 사용자 이름 */
  userName?: string;
  /** 사용자 역할 (예: 프로, 매니저 등) */
  userRole?: string;
  /** 사용자 소속 팀/부서 */
  userTeam?: string;
  /** 프로필 이니셜 (직접 지정 시) */
  userInitial?: string;
  /** 팝업 닫기 핸들러 */
  onClose?: () => void;
  /** 로그아웃 클릭 핸들러 */
  onLogout?: () => void;
  /** 추가 CSS 클래스 */
  className?: string;
}
