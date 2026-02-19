import type { UIProjectBoxProps } from '@/components/UI/molecules/layout/UIProjectBox';
import type { UIUserPopupProps } from '@/components/UI/molecules/popup/UIUserPopup';
import type { UIIDEPopupProps } from '@/components/UI/molecules/popup/UIIDEPopup';

export interface UIHeaderProps {
  /** 헤더 타이틀 */
  title?: string;

  /** 로고 표시 여부 */
  showLogo?: boolean;

  /** 오른쪽 영역 컨텐츠 */
  rightContent?: React.ReactNode;

  /** 추가 CSS 클래스명 */
  className?: string;

  /** 로고 클릭 이벤트 핸들러 */
  onLogoClick?: () => void;

  /** 타이틀 클릭 이벤트 핸들러 */
  onTitleClick?: () => void;

  /** 헤더 클릭 이벤트 핸들러 */
  onHeaderClick?: (event: React.MouseEvent<HTMLElement>) => void;

  /** 프로필 클릭 이벤트 핸들러 */
  onProfileClick?: () => void;

  /** 사용자 이름 */
  name?: string;

  /*네비게이션 */
  navigateActions?: {
    left: {
      available: boolean;
      onClick: () => void;
    };
    right: {
      available: boolean;
      onClick: () => void;
    };
  };

  /** 위치 목록 */
  locations?: string[];

  /** 프로젝트 박스 컴포넌트 속성 */
  projectBoxProps?: UIProjectBoxProps;

  /** 알림 클릭 이벤트 핸들러 */
  onAlarmClick?: () => void;

  /** 읽지 않은 알림 개수 */
  unreadAlarmCount?: number;

  /** 사용자 팝업 컴포넌트 속성 */
  userPopupProps?: UIUserPopupProps;

  /** IDE 이동 팝업 오픈 이벤트 핸들러 */
  onOpenIDEPopup?: () => void;

  /** IDE 팝업 컴포넌트 속성 */
  idePopupProps?: UIIDEPopupProps;
}
