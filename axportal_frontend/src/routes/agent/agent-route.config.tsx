import { Navigate } from 'react-router';

import GraphPage from '@/components/builder/pages/GraphPage';
import { AgentMcpCtlgDetailPage, AgentMcpCtlgListPage, AgentToolDetailPage, AgentToolListPage } from '@/pages/agent';
import { AgentBuilderDetailPage, AgentBuilderListPage } from '@/pages/builder';
import type { RouteType } from '@/routes/types';

export const agentRouteConfig: RouteType[] = [
  {
    id: 'agent-main',
    path: '',
    label: '에이전트',
    element: <Navigate to='builder' replace />,
  },
  {
    id: 'agent-builder',
    path: 'builder',
    label: '빌더',
    children: [
      {
        id: 'agent-builder-list',
        path: '',
        label: '',
        element: <AgentBuilderListPage />,
      },
      {
        id: 'agent-builder-detail',
        path: ':agentId',
        label: '빌더 조회',
        element: <AgentBuilderDetailPage />,
      },
      {
        id: 'agent-builder-canvas',
        path: 'graph',
        label: '빌더 캔버스',
        element: <GraphPage />,
      },
    ],
  },
  {
    id: 'agent-tools',
    path: 'tools',
    label: 'Tools',
    children: [
      {
        id: 'agent-tools-list',
        path: '',
        label: '',
        element: <AgentToolListPage />,
      },
      {
        id: 'agent-tools-detail',
        path: ':toolId',
        label: 'Tool 조회',
        element: <AgentToolDetailPage />,
      },
    ],
  },
  {
    id: 'agent-mcp',
    path: 'mcp',
    label: 'MCP 서버',
    children: [
      {
        id: 'agent-mcp-list',
        path: '',
        label: '',
        element: <AgentMcpCtlgListPage />,
      },
      {
        id: 'agent-mcp-detail',
        path: ':mcpId',
        label: 'MCP 서버 조회',
        element: <AgentMcpCtlgDetailPage />,
      },
    ],
  },
];
