import { Navigate } from 'react-router';

import { DeployAgentDetailPage, DeployAgentListPage, DeployAgentVerInfoPage } from '@/pages/deploy/agent';
import { DeployApiKeyDetailPage, DeployApiKeyListPage } from '@/pages/deploy/apikey';
import { MigDeployDetailPage } from '@/pages/deploy/mig/MigDeployDetailPage';
import { MigDeployListPage } from '@/pages/deploy/mig/MigDeployListPage';
import { DeployModelDetailPage, DeployModelListPage } from '@/pages/deploy/model';
import { SafetyFilterDetailPage, SafetyFilterListPage } from '@/pages/deploy/safetyFilter';
import type { RouteType } from '@/routes/types';

// 임시 컴포넌트들 (실제 페이지 컴포넌트가 생성되면 교체 필요)

/**
 * path: 경로
 * label : 메뉴 명
 * element : 레이아웃 컴포넌트
 * children : 하위 메뉴 리스트
 */
export const deployRouteConfig: RouteType[] = [
  {
    id: 'deploy-main',
    path: '',
    label: 'deploy',
    element: <Navigate to='modelDeploy' />,
  },
  {
    id: 'model-deployment',
    path: 'modelDeploy',
    label: '모델 배포',
    children: [
      {
        id: 'model-deployment-list',
        path: '',
        label: '',
        element: <DeployModelListPage />,
      },
      {
        id: 'model-deployment-detail',
        path: ':servingId',
        label: '모델 배포 조회',
        element: <DeployModelDetailPage />,
      },
    ],
  },
  {
    id: 'agent-deployment',
    path: 'agentDeploy',
    label: '에이전트 배포',
    children: [
      {
        id: 'agent-deployment-list',
        path: '',
        label: '',
        element: <DeployAgentListPage />,
      },
      {
        id: 'agent-deployment-detail',
        path: ':appId',
        label: '에이전트 배포 조회',
        children: [
          {
            id: 'agent-deployment-detail-main',
            path: '',
            label: '에이전트 배포 조회',
            element: <DeployAgentDetailPage />,
          },
          {
            id: 'agent-deployment-ver-info',
            path: 'deploy/:deployId',
            label: '에이전트 배포 버전 조회',
            element: <DeployAgentVerInfoPage />,
          },
        ],
      },
    ],
  },
  {
    id: 'api-key',
    path: 'apiKey',
    label: 'API Key',
    children: [
      {
        id: 'api-key-list',
        path: '',
        label: '',
        element: <DeployApiKeyListPage />,
      },
      {
        id: 'api-key-detail',
        path: ':apiKeyId',
        label: 'API Key 조회',
        element: <DeployApiKeyDetailPage />,
      },
    ],
  },
  {
    id: 'safety-filter',
    path: 'safetyFilter',
    label: '세이프티 필터',
    children: [
      {
        id: 'safety-filter-list',
        path: '',
        label: '',
        element: <SafetyFilterListPage />,
      },
      {
        id: 'safety-filter-detail',
        path: ':id',
        label: '세이프티 필터 조회',
        element: <SafetyFilterDetailPage />,
      },
    ],
  },
  {
    id: 'mig-deployment',
    path: 'migDeploy',
    label: '운영 이행',
    children: [
      {
        id: 'mig-deployment-list',
        path: '',
        label: '',
        element: <MigDeployListPage />,
      },
      {
        id: 'mig-deployment-detail',
        path: ':migId',
        label: '운영 이행 조회',
        element: <MigDeployDetailPage />,
      },
    ],
  },
];
