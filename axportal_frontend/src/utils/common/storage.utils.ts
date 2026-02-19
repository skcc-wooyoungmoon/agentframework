import { STORAGE_KEYS } from '@/constants/common/storage.constants';

/**
 * 새로고침 여부를 감지하는 함수
 * Performance Navigation API를 사용하여 페이지가 새로고침되었는지 확인합니다.
 *
 * @returns {boolean} 새로고침 여부 (true: 새로고침, false: 일반 네비게이션)
 */
const isPageReload = (): boolean => {
  // Performance Navigation API 사용 (최신 방법)
  const navigation = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming;
  if (navigation?.type === 'reload') {
    return true;
  }

  // Fallback: 레거시 API 사용 (구형 브라우저 지원)
  if ('performance' in window && 'navigation' in performance) {
    const legacyNav = (performance as any).navigation;
    if (legacyNav?.type === legacyNav.TYPE_RELOAD) {
      return true;
    }
  }

  return false;
};

/**
 * sessionStorage에서 모든 SEARCH_VALUES 키를 제거하는 함수
 * 새로고침 시 검색 조건 상태를 초기화하기 위해 사용됩니다.
 */
const clearAllSearchValues = (): void => {
  // STORAGE_KEYS.SEARCH_VALUES의 모든 값(키)을 순회하며 제거
  Object.values(STORAGE_KEYS.SEARCH_VALUES).forEach(key => {
    sessionStorage.removeItem(key);
  });
};

/**
 * 새로고침 감지 및 모든 검색 조건 상태 초기화
 * 앱 초기화 시 호출하여 새로고침된 경우 모든 SEARCH_VALUES를 제거합니다.
 */
const handlePageReload = (): void => {
  if (isPageReload()) {
    clearAllSearchValues();
    // console.log('🔄 페이지 새로고침 감지: 모든 검색 조건 상태 초기화');
  }
};

export default {
  isPageReload,
  clearAllSearchValues,
  handlePageReload,
};
