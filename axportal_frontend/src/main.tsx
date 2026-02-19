import { StrictMode } from 'react';

import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';

// CSS는 빌드 시 번들에 포함됩니다
// import '@/styles/style.css';
import '@/index.css';

import { router } from '@/routes/AppRouter';

import storageUtils from './utils/common/storage.utils';

// 새로고침 감지 및 모든 검색 조건 상태 초기화
storageUtils.handlePageReload();

// PROD 환경에서 콘솔 로그 비활성화
// if (import.meta.env.VITE_RUN_MODE === 'E-LOCAL') {
//   console.log = function () {};
//   console.warn = function () {};
//   console.error = function () {};
// }

// Edge/저사양 PC 최적화를 위한 안전한 렌더링
const root = createRoot(document.getElementById('root')!);

root.render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>
);
