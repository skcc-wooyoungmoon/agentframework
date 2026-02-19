import { Navigate } from 'react-router';

import { DashboardPage } from '@/pages/home/DashboardPage';
import type { RouteType } from '@/routes/types';

/**
 * path: 경로
 * label : 메뉴 명
 * element : 레이아웃 컴포넌트
 * children : 하위 메뉴 리스트
 */
export const homeRouteConfig: RouteType[] = [
  {
    id: 'home-main',
    path: '',
    label: 'home',
    element: <Navigate to='dashboard' replace />,
  },
  {
    id: 'dashboard',
    path: 'dashboard',
    label: '대시보드',
    element: <DashboardPage />,
  },
];
