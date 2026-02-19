export interface UIContentsProps {
  children?: React.ReactNode;
  className?: string;
  showBreadcrumb?: boolean;
  breadcrumbItems?: Array<{ label: string; href?: string }>;
  pageTitle?: string;
  padding?: boolean;
  backgroundColor?: string;
  /** main 태그에 overflow-x-auto 적용 여부 */
  enableHorizontalScroll?: boolean;
  /** main 태그에 세로 스크롤 적용 여부 (기본값: true) */
  enableVerticalScroll?: boolean;
}
