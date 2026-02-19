import type { RouteObject } from 'react-router';

// 메뉴 타입 정의
export type RouteType = {
  id: string;
  path: string;
  label: string;
  element?: React.ReactNode;
  children?: RouteType[];
} & RouteObject;
