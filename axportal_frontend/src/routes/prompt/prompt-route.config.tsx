import { Navigate } from 'react-router';

import {
  FewShotDetailPage,
  FewShotListPage,
  InfPromptDetailPage,
  InfPromptListPage,
  WorkFlowDetailPage,
  WorkFlowListPage,
} from '@/pages/prompt';
import { GuardRailDetailPage } from '@/pages/prompt/guardRail/GuardRailDetailPage';
import { GuardRailMainPage } from '@/pages/prompt/guardRail/GuardRailMainPage';
import { GuardRailPromptDetailPage } from '@/pages/prompt/guardRail/GuardRailPromptDetailPage';
import type { RouteType } from '@/routes/types';

/**
 * path: 경로
 * label : 메뉴 명
 * element : 레이아웃 컴포넌트
 * children : 하위 메뉴 리스트
 */
export const promptRouteConfig: RouteType[] = [
  {
    id: 'prompt-main',
    path: '',
    label: 'prompt',
    element: <Navigate to='inference' />,
  },
  {
    id: 'prompt-inference',
    path: 'inferPrompt',
    label: '추론 프롬프트',
    children: [
      {
        id: 'prompt-inference-list',
        path: '',
        label: '',
        element: <InfPromptListPage />,
      },
      {
        id: 'prompt-inference-detail',
        path: ':promptUuid',
        label: '추론 프롬프트 조회',
        element: <InfPromptDetailPage />,
      },
    ],
  },

  {
    id: 'prompt-fewshot',
    path: 'fewShot',
    label: '퓨샷',
    children: [
      {
        id: 'prompt-fewshot-list',
        path: '',
        label: '',
        element: <FewShotListPage />,
      },
      {
        id: 'prompt-fewshot-detail',
        path: ':id',
        label: '퓨샷 조회',
        element: <FewShotDetailPage />,
      },
    ],
  },

  {
    id: 'guardrail',
    path: 'guardrail',
    label: '가드레일',
    children: [
      // 가드레일 목록
      {
        id: 'guardrail-list',
        path: '',
        label: '',
        element: <GuardRailMainPage />,
      },

      // 가드레일 상세
      {
        id: 'guardrail-detail',
        path: 'detail/:id',
        label: '가드레일 조회',
        element: <GuardRailDetailPage />,
      },

      // 가드레일 프롬프트 상세
      {
        id: 'guardrail-prompt-list',
        path: 'guardrail-prompt-detail',
        label: '가드레일 프롬프트 조회',
        element: <GuardRailPromptDetailPage />,
      },
    ],
  },

  {
    id: 'workflow',
    path: 'workflow',
    label: '워크플로우',
    children: [
      {
        id: 'workflow-list',
        path: '',
        label: '워크플로우 리스트',
        element: <WorkFlowListPage />,
      },
      {
        id: 'workflow-detail',
        path: ':workFlowId',
        label: '워크플로우 조회',
        element: <WorkFlowDetailPage />,
      },
    ],
  },
];
