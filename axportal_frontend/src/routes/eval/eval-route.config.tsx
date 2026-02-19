import { Navigate } from 'react-router';

import { EvalListPage } from '@/pages/eval/EvalListPage.tsx';
import type { RouteType } from '@/routes/types';

// 임시 컴포넌트들 (실제 페이지 컴포넌트가 생성되면 교체 필요)

/**
 * path: 경로
 * label : 메뉴 명
 * element : 레이아웃 컴포넌트
 * children : 하위 메뉴 리스트
 */
export const evalRouteConfig: RouteType[] = [
  {
    id: 'eval',
    path: '',
    label: '평가',
    children: [
      {
        id: 'eval-redirect',
        path: '',
        label: '평가',
        element: <Navigate to='evalList' replace />,
      },
      {
        id: 'eval-list',
        path: 'evalList',
        label: '평가',
        element: <EvalListPage />,
      },
    ],
  },
];
