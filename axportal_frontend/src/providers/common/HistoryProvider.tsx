// src/history/AppHistoryProvider.tsx
import React, { createContext, useContext, useEffect, useMemo, useRef, useState } from 'react';

import { NavigationType, useLocation, useNavigate, useNavigationType } from 'react-router-dom';

type Entry = {
  pathname: string;
  search: string;
  hash: string;
  state: unknown;
  key: string;
};

type AppHistoryContextType = {
  canGoBack: boolean;
  canGoForward: boolean;
  goBack: () => void;
  goForward: () => void;
  resetHistory: () => void; // 로그인 성공 시 호출
};

const AppHistoryContext = createContext<AppHistoryContextType | null>(null);

export const AppHistoryProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const navType = useNavigationType();

  // 스택과 포인터는 ref로 보관(리렌더 최소화)
  const stackRef = useRef<Entry[]>([]);
  const indexRef = useRef<number>(-1);

  // 버튼 활성화 표시를 위해 상태로도 노출
  const [canGoBack, setCanGoBack] = useState(false);
  const [canGoForward, setCanGoForward] = useState(false);

  // 최초 마운트: 현재 위치를 스택에 기록
  useEffect(() => {
    if (indexRef.current === -1) {
      const entry: Entry = {
        pathname: location.pathname,
        search: location.search,
        hash: location.hash,
        state: location.state,
        key: (location as any).key ?? crypto.randomUUID(),
      };
      stackRef.current = [entry];
      indexRef.current = 0;
      setCanGoBack(false);
      setCanGoForward(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // 라우트 변경마다 스택 업데이트
  useEffect(() => {
    // 마운트 직후 초기화가 안 됐으면 스킵
    if (indexRef.current === -1) return;

    const entry: Entry = {
      pathname: location.pathname,
      search: location.search,
      hash: location.hash,
      state: location.state,
      key: (location as any).key ?? crypto.randomUUID(),
    };

    if (navType === NavigationType.Push) {
      // 앞으로 가기 히스토리 제거 후 push
      stackRef.current = stackRef.current.slice(0, indexRef.current + 1).concat(entry);
      indexRef.current += 1;
    } else if (navType === NavigationType.Replace) {
      // 현재 위치를 교체
      stackRef.current[indexRef.current] = entry;
    } else {
      // POP (브라우저 기본 뒤/앞 이동 등) 발생 시:
      // React Router가 POP을 알려주지만 목표 인덱스를 모름 -> 가장 단순하게는
      // 동일 key를 가진 엔트리를 찾고 포인터만 맞춘다.
      const found = stackRef.current.findIndex(e => e.pathname === entry.pathname && e.search === entry.search && e.hash === entry.hash);
      if (found !== -1) {
        indexRef.current = found;
      } else {
        // 스택에 없으면(새 엔트리) 현재 위치를 삽입(보수적 처리)
        stackRef.current = stackRef.current.slice(0, indexRef.current + 1).concat(entry);
        indexRef.current += 1;
      }
    }

    setCanGoBack(indexRef.current > 0);
    setCanGoForward(indexRef.current < stackRef.current.length - 1);
  }, [location, navType]);

  const goBack = () => {
    if (indexRef.current <= 0) return;
    const target = stackRef.current[indexRef.current - 1];
    indexRef.current -= 1;
    navigate(target.pathname + target.search + target.hash, { replace: true, state: target.state });
  };

  const goForward = () => {
    if (indexRef.current >= stackRef.current.length - 1) return;
    const target = stackRef.current[indexRef.current + 1];
    indexRef.current += 1;
    navigate(target.pathname + target.search + target.hash, { replace: true, state: target.state });
  };

  const resetHistory = () => {
    // 현재 location만 남김 → 로그인 이전으로 Back 불가
    const entry: Entry = {
      pathname: location.pathname,
      search: location.search,
      hash: location.hash,
      state: location.state,
      key: (location as any).key ?? crypto.randomUUID(),
    };
    stackRef.current = [entry];
    indexRef.current = 0;
    setCanGoBack(false);
    setCanGoForward(false);
  };

  const value = useMemo(() => ({ canGoBack, canGoForward, goBack, goForward, resetHistory }), [canGoBack, canGoForward]);

  return <AppHistoryContext.Provider value={value}>{children}</AppHistoryContext.Provider>;
};

export const useAppHistory = () => {
  const ctx = useContext(AppHistoryContext);
  if (!ctx) throw new Error('useAppHistory must be used within AppHistoryProvider');
  return ctx;
};
