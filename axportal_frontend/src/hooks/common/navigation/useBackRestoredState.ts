/**
 * 뒤로가기 시 상태를 자동으로 복원하는 커스텀 훅
 *
 * 목록 페이지에서 검색 조건, 페이지네이션 등의 상태를 sessionStorage에 저장하고,
 * 뒤로가기로 돌아왔을 때 이전 상태를 자동으로 복원합니다.
 *
 * @template T - 상태 객체의 타입 (object를 확장해야 함)
 * @param key - sessionStorage에 저장할 키 (STORAGE_KEYS.SEARCH_VALUES의 키)
 * @param defaults - 기본값 (새로운 페이지 진입 시 사용될 기본 상태)
 * @returns {Object} 상태 관리 객체
 * @returns {T} filters - 현재 상태 값
 * @returns {Function} updateFilters - 상태 업데이트 함수 (Partial<T> 또는 함수형 업데이트 지원)
 * @returns {Function} reset - 상태 초기화 함수 (sessionStorage에서 제거)
 *
 * @example
 * ```tsx
 * const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(
 *   STORAGE_KEYS.SEARCH_VALUES.MY_LIST,
 *   { page: 1, size: 12, searchKeyword: '' }
 * );
 * ```
 */
// src/hooks/useListFiltersState.ts
import { useCallback, useEffect, useRef, useState } from 'react';

import { useNavigationType } from 'react-router-dom';

import { STORAGE_KEYS } from '@/constants/common/storage.constants';

export function useBackRestoredState<T extends object>(key: keyof typeof STORAGE_KEYS.SEARCH_VALUES, defaults: T) {
  // React Router의 네비게이션 타입 확인 (PUSH: 새 페이지 진입, POP: 뒤로가기)
  const navigationType = useNavigationType();
  // const location = useLocation();

  // 첫 마운트 여부를 추적하는 ref (초기화 로직에서 사용)
  const isFirstMount = useRef(true);

  /**
   * 새로운 페이지 진입(PUSH) 시 sessionStorage의 이전 데이터를 제거
   * 뒤로가기로 돌아온 경우에는 저장된 데이터를 유지하여 복원 가능하도록 함
   */
  useEffect(() => {
    if (isFirstMount.current && navigationType === 'PUSH') {
      isFirstMount.current = false;
      sessionStorage.removeItem(key);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // useEffect(() => {
  //   storageUtils.clearAllSearchValues();
  // }, [location.key]);

  /**
   * 상태 초기화: sessionStorage에서 읽어오거나 기본값 사용
   * - PUSH (새 페이지 진입): 기본값 사용
   * - POP (뒤로가기): sessionStorage에서 저장된 값 복원, 없으면 기본값 사용
   */
  const [filters, setFilters] = useState<T>(() => {
    // 새로운 페이지 진입 시 기본값 반환
    if (isFirstMount.current && navigationType === 'PUSH') {
      return defaults;
    }
    // 뒤로가기 시 저장된 값 복원 시도
    const stored = sessionStorage.getItem(key);
    if (stored) {
      try {
        return JSON.parse(stored);
      } catch {
        // JSON 파싱 실패 시 기본값 반환
        return defaults;
      }
    }
    return defaults;
  });

  /**
   * 상태 업데이트 함수
   * 상태 변경 시 자동으로 sessionStorage에 저장하여 뒤로가기 시 복원 가능하도록 함
   *
   * @param patch - 업데이트할 부분 객체 또는 이전 상태를 받아 업데이트 객체를 반환하는 함수
   *
   * @example
   * // 객체로 업데이트
   * updateFilters({ page: 2 });
   *
   * // 함수형 업데이트
   * updateFilters(prev => ({ ...prev, page: prev.page + 1 }));
   */
  const updateFilters = useCallback(
    (patch: Partial<T> | ((prev: T) => Partial<T>)) => {
      const prev = filters;
      // 함수형 업데이트인 경우 실행, 아니면 그대로 사용
      const nextPatch = typeof patch === 'function' ? patch(prev) : patch;
      // 이전 상태와 병합
      const next = { ...prev, ...nextPatch };

      // sessionStorage에 저장 (뒤로가기 시 복원을 위해)
      sessionStorage.setItem(key, JSON.stringify(next));
      setFilters(next);
    },
    [filters, key]
  );

  /**
   * 상태 초기화 함수
   * sessionStorage에서 해당 키의 데이터를 제거하여 다음 진입 시 기본값이 사용되도록 함
   */
  const reset = useCallback(() => {
    sessionStorage.removeItem(key);
  }, [key]);

  return { filters, updateFilters, reset };
}
