import type { RouteType } from '../types';
import { AgentBuilderDetailPage, AgentBuilderListPage } from '@/pages/agent/builder';

import GraphPage from '@/components/agents/builder/pages/GraphPage';

export const testRouteConfig: RouteType[] = [
  {
    id: 'TEST2',
    path: 'secret',
    label: 'secret',
    children: [
      {
        id: 'agent-builder-list2',
        path: '',
        label: '',
        element: <AgentBuilderListPage />,
      },
      {
        id: 'agent-builder-detai2',
        path: ':agentId',
        label: '빌더 조회',
        element: <AgentBuilderDetailPage />,
      },
      {
        id: 'agent-builder-canvas2',
        path: 'graph2',
        label: '빌더 캔버스',
        element: <GraphPage />,
      },
    ],
  },
];
