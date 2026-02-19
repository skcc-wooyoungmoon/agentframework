import { useEffect, useState } from 'react';

import { useIsFetching, useIsMutating } from '@tanstack/react-query';

import { UILoading } from '@/components/UI/molecules';
import { env } from '@/constants/common/env.constants';
import { DONT_SHOW_LOADING_KEYS } from '@/constants/common/loading.constants';

// TODO : 로딩 컴포넌트 처리 필요
export const LoadingHandler = () => {
  // NO_PRESSURE_MODE일 때는 GRID_DATA를 로딩 제외 목록에서 제거하여(=빼서) 로딩 표시 대상에 포함
  const excludeKeys = Object.values(DONT_SHOW_LOADING_KEYS).filter(excludeKey => {
    if (env.VITE_NO_PRESSURE_MODE && excludeKey === DONT_SHOW_LOADING_KEYS.GRID_DATA) return false;
    return true;
  });

  const isFetching =
    useIsFetching({
      predicate: query => {
        // 특정 queryKey를 제외하는 로직
        return !excludeKeys.some(excludeKey => query.queryKey.some((keyPart: unknown) => typeof keyPart === 'string' && keyPart.includes(excludeKey)));
      },
    }) > 0;
  const isMutating =
    useIsMutating({
      predicate: mutation => {
        // 특정 mutationKey를 제외하는 로직
        return !excludeKeys.some(excludeKey =>
          mutation.options.mutationKey?.some((keyPart: unknown) => typeof keyPart === 'string' && keyPart.includes(excludeKey))
        );
      },
    }) > 0;

  const isLoading = isFetching || isMutating;
  const [showLoading, setShowLoading] = useState(false);

  useEffect(() => {
    let timeoutId: NodeJS.Timeout;

    if (isLoading) {
      // 0.3초 후에 로딩 바 표시
      timeoutId = setTimeout(() => {
        setShowLoading(true);
      }, 200);
    } else {
      // 로딩이 완료되면 즉시 숨김
      setShowLoading(false);
    }

    return () => {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
    };
  }, [isLoading]);

  if (!showLoading) return null;
  return <UILoading title='처리중입니다.' label='잠시만 기다려 주세요.' />;
};
