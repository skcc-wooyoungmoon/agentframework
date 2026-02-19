import { Navigate } from 'react-router';

import { LogAgentDetailPage, LogAgentListPage } from '@/pages/log/agent';
import { LogModelDetailPage } from '@/pages/log/model/LogModelDetailPage';
import { LogModelPage } from '@/pages/log/model/LogModelPage';
import type { RouteType } from '@/routes/types';

/**
 * path: 경로
 * label : 메뉴 명
 * element : 레이아웃 컴포넌트
 * children : 하위 메뉴 리스트
 */
export const logRouteConfig: RouteType[] = [
  {
    id: 'log-main',
    path: '',
    label: '로그',
    element: <Navigate to='modelDeployLog' replace />,
  },
  {
    id: 'log-model',
    path: 'modelDeployLog',
    label: '모델사용 로그',
    children: [
      {
        id: 'log-model-list',
        path: '',
        label: '모델사용 로그 목록',
        element: <LogModelPage />,
      },
      {
        id: 'log-model-detail',
        path: ':id',
        label: '모델사용 로그 조회',
        element: <LogModelDetailPage />,
      },
    ],
  },
  {
    id: 'log-agent',
    path: 'agentDeployLog',
    label: '에이전트사용 로그',
    children: [
      {
        id: 'log-agent-list',
        path: '',
        label: '에이전트사용 목록',
        element: <LogAgentListPage />,
      },
      {
        id: 'log-agent-detail',
        path: ':id',
        label: '에이전트사용 로그 조회',
        element: <LogAgentDetailPage />,
      },
    ],
  },
];
