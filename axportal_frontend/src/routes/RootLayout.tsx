import { Suspense } from 'react';
import { Outlet } from 'react-router-dom';

import { ErrorHandler, LoadingHandler } from '@/components/common';
import { UILoading } from '@/components/UI/molecules';
import { UITostRenderer } from '@/components/UI/molecules/toast/UITostRenderer';
import { TransactionInterceptor } from '@/pages/auth/login/LogoutNotiInterceptor.tsx';
import { ModalProvider, QueryProvider } from '@/providers/common';

/**
 * 라우터의 루트 레이아웃
 * 모든 라우트에서 사용할 Provider들을 포함합니다.
 */
export function RootLayout() {
  return (
    <QueryProvider>
      <ModalProvider>
        <LoadingHandler />
        <ErrorHandler />
        <UITostRenderer />
        <TransactionInterceptor />
        <Suspense fallback={<UILoading title='로딩 중입니다.' label='잠시만 기다려 주세요.' />}>
          <Outlet />
        </Suspense>
      </ModalProvider>
    </QueryProvider>
  );
}
