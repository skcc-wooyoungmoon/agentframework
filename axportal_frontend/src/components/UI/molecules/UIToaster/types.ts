export interface UIToasterProps {
  /** Toaster 위치 */
  position?: "top-left" | "top-center" | "top-right" | "bottom-left" | "bottom-center" | "bottom-right";
  /** 토스트 표시 시간 (밀리초, 기본값: 2000) */
  duration?: number;
  /** 추가 CSS 클래스 */
  className?: string;
}

export interface UseToasterOptions {
  /** 토스트 표시 시간 (밀리초) */
  duration?: number;
}
