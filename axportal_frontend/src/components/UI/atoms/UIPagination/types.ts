export interface UIPaginationProps {
  /** 현재 페이지 번호 (1부터 시작) */
  currentPage: number;
  /** 전체 페이지 수 */
  totalPages: number;
  /** 페이지 변경 시 호출되는 콜백 함수 */
  onPageChange: (page: number) => void;
  /** 표시할 페이지 번호 개수 (기본값: 10) */
  displayPageCount?: number;
  /** 처음/마지막 페이지 이동 버튼 표시 여부 (기본값: true) */
  showFirstLastButtons?: boolean;
  /** 이전/다음 페이지 이동 버튼 표시 여부 (기본값: true) */
  showPrevNextButtons?: boolean;
  /** 추가 CSS 클래스명 */
  className?: string;
  /** 페이징 컴포넌트 비활성화 여부 */
  disabled?: boolean;
  /** 다음 페이지 존재 여부 (lazy mode일 때 사용, API에서 제공) */
  hasNext?: boolean;
}
