import React from 'react';

// import { Navigate } from "react-router-dom";
// import { useAuthStatus } from '@/hooks/common/auth';
// import { useTokenRefresh } from '@/hooks/common/auth/useTokenRefresh';
// import { authUtils } from '@/utils/common';

interface AuthProviderProps {
  children: React.ReactNode;
}

// TODO : 인증 방식에 따라 수정 혹은 삭제 예정
export function AuthProvider({ children }: AuthProviderProps) {
  // console.log('🎄 AuthProvider', window.location.href);
  // useEffect(() => {
  //   console.log('🎄 AuthProvider', window.location.href);
  // }, [window.location.href]);
  // 1. 토큰 갱신
  // const { isRefreshing } = useTokenRefresh({
  //   checkInterval: 30 * 1000, // 30초마다 체크
  //   refreshBeforeExpiry: 10, // TODO : 변경 필요 (개발용 - 10)
  //   enabled: true,
  // });
  // console.log('🎄 isRefreshing', isRefreshing);

  // 2. 통합 인증 상태 관리
  // const { isLoading, error } = useAuthStatus();
  // console.log('🎄 authStatus', { isAuthenticated, isLoading, error });

  // 3. 로딩 상태 처리
  // if (isLoading || isRefreshing) {
  //   // return (
  //   //   <div className='flex items-center justify-center min-h-screen'>
  //   //     <div className='animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600'></div>
  //   //     <span className='ml-2 text-gray-600'>{isRefreshing ? '토큰을 갱신 중입니다...' : '인증 상태를 확인 중입니다...'}</span>
  //   //   </div>
  //   // );
  // }

  // 4. 에러 상태 처리
  // if (error) {
  //   console.error('🎄 인증 에러:', error);
  //   // 에러가 발생했지만 토큰이 있으면 갱신 시도
  //   if (authUtils.hasAccessToken()) {
  //     return <>{children}</>; // 갱신 시도 중이므로 잠시 대기
  //   }
  // }

  // 6. 인증된 사용자 - 정상 렌더링
  return <>{children}</>;
}
