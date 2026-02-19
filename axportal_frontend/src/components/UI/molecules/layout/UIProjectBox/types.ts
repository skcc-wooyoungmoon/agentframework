export interface UIProjectBoxProps {
  /** 프로젝트 목록 */
  projectList?: ProjectItemType[];
  /** 추가 CSS 클래스 */
  className?: string;
  /** 클릭 핸들러 */
  onCreateProject?: (e: React.MouseEvent<HTMLButtonElement>) => void;
  onJoinProject?: (e: React.MouseEvent<HTMLButtonElement>) => void;
  onQuitProject?: (e: React.MouseEvent<HTMLButtonElement>) => void;
  onProjectSelect?: (e: React.MouseEvent<HTMLElement>, id?: string) => void;
  disabled?: boolean;
}

export type ProjectItemType = { id: string; name: string; selected: boolean; count: number };
