import { Navigate } from 'react-router';

import { ApiKeyMgmtDetailPage } from '@/pages/admin/apiKeyMgmt/ApiKeyMgmtDetailPage';
import { ApiKeyMgmtListPage } from '@/pages/admin/apiKeyMgmt/ApiKeyMgmtListPage';
import { IdeMgmtMainPage, IdeImageDetailPage } from '@/pages/admin/ideMgmt';
import { NoticeMgmtDetailPage } from '@/pages/admin/noticeMgmt/NoticeMgmtDetailPage';
import { NoticeMgmtListPage } from '@/pages/admin/noticeMgmt/NoticeMgmtListPage';
import { ProjDetailPage, ProjListPage, ProjRoleDetailPage } from '@/pages/admin/projMgmt';
import { ResrcMgmtGpuNodeDetailPage } from '@/pages/admin/resrcMgmt/ResrcMgmtGpuNodeDetailPage';
import { ResrcMgmtMainPage } from '@/pages/admin/resrcMgmt/ResrcMgmtMainPage';
import { ResrcMgmtPortalAgentPage } from '@/pages/admin/resrcMgmt/ResrcMgmtPortalAgentPage';
import { ResrcMgmtPortalModelPage } from '@/pages/admin/resrcMgmt/ResrcMgmtPortalModelPage';
import { ResrcMgmtSolutionDetailPage } from '@/pages/admin/resrcMgmt/ResrcMgmtSolutionDetailPage';
import { UserDetailPage, UserListPage, UserProjDetailPage } from '@/pages/admin/userMgmt';
import { UserUsageMgmtDetailPage } from '@/pages/admin/userUsageMgmt/UserUsageMgmtDetailPage';
import { UserUsageMgmtListPage } from '@/pages/admin/userUsageMgmt/UserUsageMgmtPage';
import type { RouteType } from '@/routes/types';

/**
 * path: 경로
 * label : 메뉴 명
 * element : 레이아웃 컴포넌트
 * children : 하위 메뉴 리스트
 */
export const managementRouteConfig: RouteType[] = [
  {
    id: 'mgmt-main',
    path: '',
    label: 'mgmt',
    element: <Navigate to='user-mgmt' />,
  },

  // ======= 사용자 관리 ======= //
  {
    id: 'admin-user-management',
    path: 'user-mgmt',
    label: '사용자 관리',
    children: [
      {
        id: 'admin-user-list',
        path: '',
        label: '사용자 관리',
        element: <UserListPage />,
      },

      {
        id: 'admin-user-detail-main',
        path: ':userId',
        label: '사용자 조회',
        children: [
          {
            id: 'admin-user-detail',
            path: '',
            label: '',
            element: <UserDetailPage />,
          },
          {
            id: 'admin-user-project-detail',
            path: 'projects/:projectId',
            label: '프로젝트 조회',
            element: <UserProjDetailPage />,
          },
        ],
      },

      // {
      //   id: 'admin-user-project-detail',
      //   path: ':userId/projects/:projectId',
      //   label: '프로젝트 조회',
      //   element: <UserProjDetailPage />,
      // },
    ],
  },

  // ======= 프로젝트 관리 ======= //
  {
    id: 'admin-project-management',
    path: 'project-mgmt',
    label: '프로젝트 관리',
    children: [
      {
        id: 'admin-project-list',
        path: '',
        label: '프로젝트 관리',
        element: <ProjListPage />,
      },

      {
        id: 'admin-project-detail-main',
        path: ':projectId',
        label: '프로젝트 조회',
        children: [
          {
            id: 'admin-project-detail',
            path: '',
            label: '',
            element: <ProjDetailPage />,
          },
          {
            id: 'admin-project-role-detail',
            path: 'roles/:roleId',
            label: ' 역할 조회',
            element: <ProjRoleDetailPage />,
          },
        ],
      },
    ],
  },

  // ======= 자원 관리 ======= //
  {
    id: 'admin-resrc-management',
    path: 'resrc-mgmt',
    label: '자원 관리',
    children: [
      {
        id: 'admin-resrc-main',
        path: '',
        label: '',
        element: <ResrcMgmtMainPage />,
      },
      {
        id: 'admin-resrc-portal-agent',
        path: 'portal-agent',
        label: '에이전트 배포 자원 현황 조회',
        element: <ResrcMgmtPortalAgentPage />,
      },
      {
        id: 'admin-resrc-portal-model',
        path: 'portal-model',
        label: '모델 배포 자원 현황 조회',
        element: <ResrcMgmtPortalModelPage />,
      },
      {
        id: 'admin-resrc-gpu-node-detail',
        path: 'gpu-node-detail',
        label: 'GPU 노드별 자원 현황 조회',
        element: <ResrcMgmtGpuNodeDetailPage />,
      },
      {
        id: 'admin-resrc-solution-detail',
        path: 'solution-detail',
        label: '솔루션 자원 현황 조회',
        element: <ResrcMgmtSolutionDetailPage />,
      },
    ],
  },

  // ======= 사용 이력관리 ======= //
  {
    id: 'admin-usage-history-main',
    path: 'usage-hist-mgmt',
    label: '사용자 이용 현황',
    children: [
      {
        id: 'admin-usage-history-list',
        path: '',
        label: '',
        element: <UserUsageMgmtListPage />,
      },
      {
        id: 'admin-usage-history-detail',
        path: 'detail',
        label: '사용 이력 조회',
        element: <UserUsageMgmtDetailPage />,
      },
    ],
  },

  // {
  //   id: 'admin-usage-history-detail',
  //   path: 'usage-hist-mgmt-detail',
  //   label: '사용 이력관리 상세',
  //   element: <UserUsageMgmtDetailPage />,
  // },

  // ======= 공지사항 관리 ======= //
  {
    id: 'admin-notice-management',
    path: 'notice-mgmt',
    label: '공지사항 관리',
    children: [
      {
        id: 'admin-notice-list',
        path: '',
        label: '공지사항 관리',
        element: <NoticeMgmtListPage />,
      },
      {
        id: 'admin-notice-detail',
        path: ':id',
        label: '공지사항 조회',
        element: <NoticeMgmtDetailPage />,
      },
    ],
  },

  // ======= API Key 관리 ======= //
  {
    id: 'admin-apikey-management',
    path: 'api-key-mgmt',
    label: 'API Key 관리',
    children: [
      {
        id: 'admin-apikey-list',
        path: '',
        label: 'API Key 관리',
        element: <ApiKeyMgmtListPage />,
      },
      {
        id: 'admin-apikey-detail',
        path: 'detail',
        label: 'API Key 조회',
        element: <ApiKeyMgmtDetailPage />,
      },
    ],
  },

  // ======= IDE 관리 ======= //
  {
    id: 'admin-ide-management',
    path: 'ide-mgmt',
    label: 'IDE 관리',
    children: [
      {
        id: 'admin-ide-main',
        path: '',
        label: '',
        element: <IdeMgmtMainPage />,
      },
      {
        id: 'admin-ide-image-detail',
        path: 'image/:imageId',
        label: '이미지 조회',
        element: <IdeImageDetailPage />,
      },
    ],
  },
];
