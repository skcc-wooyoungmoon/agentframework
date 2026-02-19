export interface UIPageHeaderProps {
  /** 페이지 메인 제목 */
  title: string | React.ReactNode;
  /** 페이지 설명 텍스트 */
  description?: string | React.ReactNode;
  /** 페이지 액션 버튼 */
  actions?: React.ReactNode;
}
