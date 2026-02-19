import { fontSizeClassMap } from './constants';

import type { UITypographyProps } from './types';

/**
 * Typography 아톰 컴포넌트 (span 고정)
 * - Tailwind fontSize 커스텀 네이밍 1:1 매핑
 * - Atomic Design: atom
 * - 의미론적 태그(as) 미사용, 항상 <span> 태그
 * - 재사용/독립적, 데이터 연동/로직 없음
 * - 콘텐츠는 props로만 전달
 */
export function UITypography({ variant, children, className = '', required = false, divider = false, ...txtProps }: UITypographyProps) {
  // Tailwind fontSize 클래스 + 추가 클래스 조합
  const classes = `${fontSizeClassMap[variant]} ${divider ? 'has-divider' : 'inline-block'} ${required ? 'ui-typography-required' : ''} ${className}`.trim();

  return (
    // 항상 <span> 태그 사용, alt 등은 상위에서 처리
    <div className={classes} {...txtProps}>
      {children}
    </div>
  );
}

// 사용 예시는 스토리북에서 제공
