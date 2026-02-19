import { redirect } from 'react-router-dom';

import { authUtils } from '@/utils/common';

// '/' 진입 시 인증 여부에 따라 분기
export function rootLoader() {
  const isAuthed = authUtils.isAuthenticated();
  return redirect(isAuthed ? '/home' : '/login'); // TODO : 로그인 페이지 변경 필요
}

// 비인증 전용 라우트 (예: 로그인)
export function publicLoader() {
  const isAuthed = authUtils.isAuthenticated();
  if (isAuthed) {
    return redirect('/home');
  }
  return null;
}

// 인증 전용 라우트 (업무 섹션)
export function protectedLoader({ request: _request }: { request: Request }) {
  const isAuthed = authUtils.isAuthenticated();

  // 리다이렉트 방지 플래그가 설정되어 있으면 인증 우회
  if ((window as any).__PREVENT_REDIRECT__) {
    // console.log('🔍 리다이렉트 방지 플래그 - 인증 우회');
    return null;
  }

  if (!isAuthed) {
    // const next = encodeURIComponent(request.url);
    return redirect(`/login`);
  }
  return null;
}
