// import { lazy } from 'react';
import { Outlet } from 'react-router';

import { MainLayout } from '@/components/common';
import { designRouteConfig } from '@/design/routes/design-route.config';
import { evalRouteConfig } from '@/routes/eval/eval-route.config.tsx';
import type { RouteType } from '@/routes/types';

// Lazy load pages
// const UIPageList = lazy(() => import('@/design/pages/UIPageList').then(module => ({ default: module.UIPageList })));
// const LoginComp = lazy(() => import('@/pages/auth/login/LoginComp').then(module => ({ default: module.LoginComp })));
// const LoginPage = lazy(() => import('@/pages/auth/login/LoginPage').then(module => ({ default: module.LoginPage })));
// const NotFoundPage = lazy(() => import('@/pages/etc').then(module => ({ default: module.NotFoundPage })));

import { UIPageList } from '@/design/pages';
import { LoginComp } from '@/pages/auth/login/LoginComp';
import { LoginPage } from '@/pages/auth/login/LoginPage';
import { NotFoundPage } from '@/pages/etc';
import { agentRouteConfig } from './agent/agent-route.config';
import { dataRouteConfig } from './data/data-route.config';
import { deployRouteConfig } from './deploy/deploy-route.config';
import { protectedLoader, publicLoader, rootLoader } from './guards/authLoaders';
import { homeRouteConfig } from './home/home-route.config';
import { logRouteConfig } from './log/log-route.config';
import { managementRouteConfig } from './management/management-route.config';
import { modelRouteConfig } from './model/model-route.config';
import { noticeRouteConfig } from './notice/notice-route.config';
import { promptRouteConfig } from './prompt/prompt-route.config';
import { testRouteConfig } from './test/test-route.config';

// 1depth route config
/**
 * key : 1depth 메뉴 고유키
 * path: 경로
 * label : 메뉴 명 // TODO : 제거 예정
 * element : 레이아웃 컴포넌트
 * children : 2depth 메뉴 리스트
 */
export const routeConfig: RouteType[] = [
  // 1) 루트 분기: '/' → 인증 여부에 따라 /home 또는 /login
  {
    id: 'ROOT',
    path: '/',
    label: 'root',
    loader: rootLoader,
  },
  {
    id: 'LOGIN-COMPLETE',
    path: 'login-complete',
    label: 'login-complete',
    element: <LoginComp />,
  },
  // 2) Public 그룹: 비인증 전용
  {
    id: 'PUBLIC',
    path: '/',
    label: 'public',
    element: (
      <div className='min-h-screen flex items-center justify-center bg-gray-50'>
        <Outlet />
      </div>
    ),
    loader: publicLoader,
    children: [
      {
        id: 'LOGIN',
        path: 'login',
        label: 'login',
        element: <LoginPage />,
      },
    ],
  },
  // 3) Protected 그룹: 인증 전용 (모든 업무 섹션)
  {
    id: 'PROTECTED',
    path: '/',
    label: 'protected',
    element: <MainLayout />,
    loader: protectedLoader,
    children: [
      { id: 'HOME', path: 'home', label: '홈', children: homeRouteConfig },
      { id: 'DATA', path: 'data', label: '데이터', children: dataRouteConfig },
      { id: 'MODEL', path: 'model', label: '모델', children: modelRouteConfig },
      { id: 'PROMPT', path: 'prompt', label: '프롬프트', children: promptRouteConfig },
      { id: 'AGENT', path: 'agent', label: '에이전트', children: agentRouteConfig },
      { id: 'EVAL', path: 'eval', label: '평가', children: evalRouteConfig },
      { id: 'DEPLOY', path: 'deploy', label: '배포', children: deployRouteConfig },
      { id: 'LOG', path: 'log', label: '로그', children: logRouteConfig },
      { id: 'NOTICE', path: 'notice', label: '공지사항', children: noticeRouteConfig },
      { id: 'MANAGEMENT', path: 'admin', label: '관리', children: managementRouteConfig },
      { id: 'TEST', path: 'test', label: 'test', children: testRouteConfig },
    ],
  },
  {
    id: 'design',
    path: 'design',
    label: 'Design',
    element: <Outlet />,
    children: [
      {
        id: 'design-list',
        path: '',
        label: 'Page List',
        element: <UIPageList />,
      },
      ...designRouteConfig,
    ],
  },
  {
    id: 'notfound',
    path: '*',
    label: 'NOT FOUND',
    element: <NotFoundPage />,
  },
];
