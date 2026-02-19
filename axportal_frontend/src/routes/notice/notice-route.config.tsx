import { Navigate } from 'react-router';

import { NotiDetailPage } from '@/pages/notice/NotiDetailPage';
import { NotiListPage } from '@/pages/notice/NotiListPage';
import type { RouteType } from '@/routes/types';

/**
 * path: 경로
 * label : 메뉴 명
 * element : 레이아웃 컴포넌트
 * children : 하위 메뉴 리스트
 */
export const noticeRouteConfig: RouteType[] = [
  {
    id: 'notice',
    path: '',
    label: '공지사항',
    children: [
      {
        id: 'notice-redirect',
        path: '',
        label: '공지사항',
        element: <Navigate to='noticeList' replace />,
      },
      {
        id: 'notice-list',
        path: 'noticeList',
        label: '공지사항',
        element: <NotiListPage />,
      },
      {
        id: 'notice-detail-view',
        path: ':id',
        label: '공지사항 조회',
        element: <NotiDetailPage />,
      },
    ],
  },
];
