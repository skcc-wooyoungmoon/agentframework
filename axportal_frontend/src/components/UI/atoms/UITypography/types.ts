import type * as React from 'react';
// TypographyProps: 폰트 스타일(variant)과 children(내용)만 전달받음
export interface UITypographyProps extends React.HTMLAttributes<HTMLDivElement> {
  /** 폰트 스타일(variant): 디자인 시스템 네이밍과 1:1 매칭 */
  variant:
    | 'headline-1'
    | 'headline-2-product'
    | 'headline-2'
    | 'title-1'
    | 'title-2'
    | 'title-3'
    | 'title-4'
    | 'body-1'
    | 'body-2'
    | 'body-3'
    | 'caption-1'
    | 'caption-2'
    | 'appbar-1'
    | 'appbar-2'
    | 'bottom-1';
  /** 텍스트 콘텐츠(필수, 컴포넌트 내부 하드코딩 금지) */
  children: React.ReactNode;
  /** 추가 className (선택) */
  className?: string;
  /** 필수 항목 표시 여부 - 빨간 별표(*) 자동 추가 */
  required?: boolean;
  /** inline style (선택) */
  style?: React.CSSProperties;
  /** before 구분선 표시 여부 */
  divider?: boolean;
  /** 구분선으로 연결된 텍스트 배열 (예: ['장정현', 'Data기획Unit']) */
  separator?: string[];
}
